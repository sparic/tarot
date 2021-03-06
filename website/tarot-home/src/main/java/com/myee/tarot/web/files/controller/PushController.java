package com.myee.tarot.web.files.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.myee.djinn.dto.AppType;
import com.myee.djinn.dto.NoticeType;
import com.myee.djinn.dto.PushResourceDTO;
import com.myee.djinn.endpoint.TrunkingInterface;
import com.myee.djinn.rpc.bootstrap.ServerBootstrap;
import com.myee.tarot.admin.domain.AdminUser;
import com.myee.tarot.catalog.domain.DeviceUsed;
import com.myee.tarot.catalog.service.DeviceUsedService;
import com.myee.tarot.core.Constants;
import com.myee.tarot.core.exception.ServiceException;
import com.myee.tarot.core.util.PageResult;
import com.myee.tarot.core.util.StringUtil;
import com.myee.tarot.core.util.WhereRequest;
import com.myee.tarot.core.util.ajax.AjaxPageableResponse;
import com.myee.tarot.core.util.ajax.AjaxResponse;
import com.myee.tarot.merchant.domain.MerchantStore;
import com.myee.tarot.merchant.service.MerchantStoreService;
import com.myee.tarot.resource.domain.Notification;
import com.myee.tarot.resource.service.NotificationService;
import com.myee.tarot.web.apiold.util.FileValidCreateUtil;
import com.myee.tarot.web.files.FileType;
import com.myee.tarot.web.files.vo.FileItem;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.*;

/**
 * Created by Martin on 2016/4/21.
 */
@Controller
public class PushController {

    private static final Logger LOGGER = LoggerFactory.getLogger(PushController.class);

    @Value("${cleverm.push.dirs}")
    private String DOWNLOAD_HOME;
    @Value("${cleverm.push.http}")
    private String DOWNLOAD_HTTP;

    @Autowired
    private ServerBootstrap serverBootstrap;

    @Autowired
    private NotificationService notificationService;

	@Autowired
	private MerchantStoreService merchantStoreService;

    @Autowired
    private DeviceUsedService deviceUsedService;

    @RequestMapping(value = "admin/file/search", method = RequestMethod.POST)
    @ResponseBody
    public AjaxPageableResponse searchResource(@RequestParam("node") String parentNode, HttpServletRequest request) {
        MerchantStore store = (MerchantStore) request.getSession().getAttribute(Constants.ADMIN_STORE);
        File template = getResFile(100L, parentNode);
//        Map<String, FileItem> resMap = Maps.newLinkedHashMap();
		Map<String, FileItem> resMap = getTreeMap();
        listFiles(template, resMap, 100L, store.getId());
        if (100L != store.getId()) {
            File dir = getResFile(store.getId(), parentNode);
            listFiles(dir, resMap, store.getId(), store.getId());
        }
        return new AjaxPageableResponse(Lists.<Object>newArrayList(resMap.values()));
    }

    @RequestMapping(value = "admin/file/create")
    @ResponseBody
    public AjaxPageableResponse createResource(@RequestParam(value = "file", required = false) CommonsMultipartFile file, @RequestParam("entityText") String entityText, HttpServletRequest request) {
        MerchantStore store = (MerchantStore) request.getSession().getAttribute(Constants.ADMIN_STORE);
        JSONObject jsonObject = JSON.parseObject(entityText);
        //salt是/会导致转对象失败，所以人为处理下
        if ("/".equals(jsonObject.getString("salt"))) {
            jsonObject.remove("salt");
            jsonObject.put("salt", store.getId());
        }

        FileItem vo = JSON.parseObject(jsonObject.toJSONString(), FileItem.class);
		String name = vo.getName();

//        Map<String, FileItem> resMap = Maps.newLinkedHashMap();
		Map<String, FileItem> resMap = getTreeMap();
        try {
            File dest = FileUtils.getFile(DOWNLOAD_HOME, Long.toString(store.getId()), vo.getPath()); //新增文件父路径
			if (!dest.exists())
				dest.mkdirs();
            String currPath = vo.getCurrPath() == null ? "" : vo.getCurrPath();
            if (dest.isFile() && !StringUtil.isNullOrEmpty(vo.getContent(), true)) {  //编辑
                //删除原文件，创建一个新的文件，将内容写入
                String path = dest.getAbsolutePath().substring(0, dest.getAbsolutePath().lastIndexOf(File.separator));
                dest.delete();
                File desFile = new File(path + File.separator + name);
                desFile.createNewFile();
                FileUtils.writeStringToFile(desFile, vo.getContent(),"utf-8");
                dest = new File(path);  //用于查找文件
            }else{  //新增
				File desDir = new File(dest + File.separator + currPath);
				if (!desDir.exists())
					desDir.mkdirs();
				File desFile = new File(desDir + File.separator + name);
				desFile.createNewFile();
				if ( file != null && !file.isEmpty()) {  //文件上传
					file.transferTo(desFile);
				}
				if (!StringUtil.isNullOrEmpty(vo.getContent(), true)) { //界面输入文件内容
					FileUtils.writeStringToFile(desFile, vo.getContent(),"utf-8");
				}
			}
			String shareDir = dest.getAbsolutePath().replace(store.getId()+"","100");
			listFiles(new File(shareDir), resMap, 100L, store.getId()); //列出100下的文件
			if (100L != store.getId()) {
				listFiles(dest, resMap, store.getId(), store.getId());
			}
        } catch (IOException e) {
            LOGGER.error("create file error", e);
        }
        return new AjaxPageableResponse(Lists.<Object>newArrayList(resMap.values()));
    }

    @RequestMapping(value = "admin/file/delete", method = RequestMethod.POST)
    @ResponseBody
    @PreAuthorize("hasAuthority('explorer_explorer_d')")
    @Transactional
    public AjaxResponse deleteResource(@RequestParam("salt") Long orgID, @RequestParam("path") String path, HttpServletRequest request) {
        String message = "";
        //不允许删除别的店铺下的资源
        MerchantStore store = (MerchantStore) request.getSession().getAttribute(Constants.ADMIN_STORE);
//        Map<String, FileItem> resMap = Maps.newLinkedHashMap();
		Map<String, FileItem> resMap = getTreeMap();
        if (!store.getId().equals(orgID)) {
            message = "不允许删除别的店铺下的资源";
            return AjaxResponse.failed(-1, message);
        } else {
            File resFile = getResFile(orgID, path);
            if (resFile.isDirectory()) {
                try {
                    getFiles(resFile);
                } catch (StopMsgException e) {
                    message = "该目录下还有文件，请先删除文件";
                    return AjaxResponse.failed(-2, message);
                }
            }
            boolean isMove = copyToRecycle(resFile);//复制文件或者文件夹到回收站
            if (isMove) { //复制成功后执行删除
                if (resFile.exists()) {
                    boolean result = resFile.delete();
                    if (!result) {
                        message = "删除失败！";
                        return AjaxResponse.failed(-3, message);
                    }
                }
                if (path.lastIndexOf(File.separator) != -1) {
                    File parentPathFile = getResFile(orgID, path.substring(0, path.lastIndexOf(File.separator)));
                    listFiles(parentPathFile, resMap, store.getId(), store.getId());
                }
                return new AjaxPageableResponse(Lists.<Object>newArrayList(resMap.values()));
            } else {
                message = "删除失败！";
                return AjaxResponse.failed(-3, message);
            }
        }
    }

    @RequestMapping("admin/content/get")
    @ResponseBody
    public AjaxResponse getContentText(@RequestParam("data") String data, HttpServletRequest request) {
        AjaxResponse ajaxResponse = new AjaxResponse();
        Map<String, Object> map = new HashMap<String, Object>();
        boolean flag = true;
        FileItem fileItem = JSON.parseObject(data, FileItem.class);
        MerchantStore store = (MerchantStore) request.getSession().getAttribute(Constants.ADMIN_STORE);
        Long orgID = store.getId();
        if (!orgID.equals(fileItem.getSalt())) {
            return AjaxResponse.failed(-1, "文件不属于该店铺，不能修改");
        }
        String fileName = fileItem.getName();
        String suffix = fileName.substring(fileName.lastIndexOf(".") + 1);
        File resFile = getResFile(orgID, fileItem.getPath());
        if (!resFile.exists()) {
            return AjaxResponse.failed(-2, "文件不存在");
        }
        String type = FileType.getFileType(resFile.getAbsolutePath());
        if ((null != type && !type.matches(Constants.ALLOW_EDITOR_TEXT)) || !suffix.matches(Constants.ALLOW_EDITOR_TEXT)) {
            return AjaxResponse.failed(-5, "不支持该格式文件在线编辑。");
        }
        if (resFile.length() > 4096L) {
            return AjaxResponse.failed(-3, "超过文本读取大小限制(4K)。");
        }
        String value = "";
        try {
            value = FileUtils.readFileToString(resFile, "utf-8");
        } catch (IOException ie) {
            LOGGER.error("读取文件错误", ie);
            return AjaxResponse.failed(-4, "读取文件错误");
        } finally {
            map.put("message", value);
            ajaxResponse.addDataEntry(map);
        }
        return ajaxResponse;
    }

    private void listFiles(File parentFile, Map<String, FileItem> resMap, Long orgID, Long storeId) {
        if (!parentFile.exists() || !parentFile.isDirectory() || null == parentFile.listFiles()) {
            return;
        }
		MerchantStore merchantStore = merchantStoreService.findById(orgID);
        String prefix = FilenameUtils.concat(DOWNLOAD_HOME, Long.toString(orgID));
        for (File file : parentFile.listFiles()) {
            FileItem fileItem = FileItem.toResourceModel(file, orgID, storeId);
			fileItem.setSaltName(merchantStore.getName());
            fileItem.setPath((trimStart(fileItem.getPath(), prefix)).replace(Constants.BACKSLASH, Constants.SLASH));
            fileItem.setUrl(DOWNLOAD_HTTP + orgID + Constants.SLASH + fileItem.getPath().replace(Constants.BACKSLASH, Constants.SLASH));
            try{
                if(file.isFile()){//只有是文件才算md5码
                    fileItem.setMd5(FileValidCreateUtil.fileMD5(file.getPath()));
                }
            }
            catch (Exception e){
                LOGGER.error(e.getMessage(),e);
            }
            resMap.put(file.getName(), fileItem);
        }
    }

    private File getResFile(Long orgID, String absPath) {
        return FileUtils.getFile(DOWNLOAD_HOME, Long.toString(orgID), absPath);
    }

    String trimStart(String absPath, String prefix) {
        String result = absPath;
        if (result.startsWith(prefix)) {
            result = result.substring(prefix.length() + 1);
        }
        return result;
    }

    /**
     * 根据切换的门店获取推送日志
     * @param model
     * @param request
     * @param whereRequest
     * @return
     */
    @RequestMapping(value = "admin/pushLog/paging", method = RequestMethod.GET)
    @ResponseBody
	public AjaxPageableResponse pageLogs(Model model, HttpServletRequest request, WhereRequest whereRequest) {
        AjaxPageableResponse resp = new AjaxPageableResponse();
        try {
            Object o = request.getSession().getAttribute(Constants.ADMIN_STORE);
            if (o == null) {
                resp.setErrorString("请先切换门店");
                return resp;
            }
			MerchantStore merchantStore1 = (MerchantStore)o;
			PageResult<Notification> pageList = notificationService.pageByStore(merchantStore1.getId(), whereRequest);
            List<Notification> notificationList = pageList.getList();
            for (Notification notification : notificationList) {
                Map entry = new HashMap();
                entry.put("userName", notification.getAdminUser().getLogin());
                entry.put("deviceUsedName", notification.getDeviceUsedName());
                entry.put("content", notification.getContent());
                entry.put("createTime", notification.getCreateTime());
                entry.put("timeOut", notification.getTimeout());
                entry.put("comment", notification.getComment());
				entry.put("success", notification.getSuccess());
				entry.put("operationType", ("get".equals(notification.getOperationType())? "终端拉取" : "云端推送"));
				entry.put("uuid", notification.getUuid());
                Integer appId = notification.getAppId();
                entry.put("appId", (appId == null || "".equals(appId)) ? 0 : AppType.getValue(appId));
                String noticeType = notification.getNoticeType();
                entry.put("noticeType", (noticeType == null || "".equals(noticeType)) ? "" : NoticeType.getValue(noticeType) );
                resp.addDataEntry(entry);
            }

            resp.setRecordsTotal(pageList.getRecordsTotal());

            return resp;
        } catch (Exception e) {
            LOGGER.error(e.getMessage(),e);
            return resp;
        }
    }

    /**
     * 把deviceUsed的list转为map，以便快速使用。键名是指标BoardNo
     *
     * @param deviceUsedList
     * @return
     */
    public static Map<String, DeviceUsed> deviceUsedListToBoardNoMap(List<DeviceUsed> deviceUsedList) {
        Map<String, DeviceUsed> entry = new HashMap<String, DeviceUsed>();
        for (DeviceUsed deviceUsed : deviceUsedList) {
            entry.put(deviceUsed.getBoardNo(), deviceUsed);
        }
        return entry;
    }

    @RequestMapping(value = "admin/file/push", method = RequestMethod.POST)
    @ResponseBody
    public AjaxResponse pushResource(@Valid @RequestBody PushResourceDTO pushResourceDTO, HttpServletRequest request) {
        TrunkingInterface endpointInterface = null;
//        if (pushResourceDTO == null
//                || StringUtil.isNullOrEmpty(pushResourceDTO.getUniqueNo())
//                || pushResourceDTO.getAppId() == null
//                || pushResourceDTO.getAppId() == 0) {
//            return AjaxResponse.failed(-1, "数据有误");
//        }
		if (pushResourceDTO == null
                || StringUtil.isNullOrEmpty(pushResourceDTO.getUniqueNo())) {
            return AjaxResponse.failed(-1, "数据有误");
        }
        try {
            endpointInterface = serverBootstrap.getClient(TrunkingInterface.class, pushResourceDTO.getUniqueNo());
        } catch (Exception e) {
            return AjaxResponse.failed(-2, "连接客户端错误");
        }
        if (endpointInterface == null) {
            return AjaxResponse.failed(-3, "获取接口出错");
        }
        if (pushResourceDTO.getContent() == null || pushResourceDTO.getContent().size() <= 0) {
            return AjaxResponse.failed(-4, "推送内容为空或格式错误");
        }
        boolean isSuccess = false;
        Notification notification = new Notification();
        try {
            MerchantStore merchantStore = (MerchantStore)request.getSession().getAttribute(Constants.ADMIN_STORE);
			AdminUser adminUser = (AdminUser) request.getSession().getAttribute(Constants.ADMIN_USER);

			WhereRequest whereRequestTemp = new WhereRequest();
			whereRequestTemp.setCount(Constants.COUNT_NOPAGING);
			List<DeviceUsed> deviceUsedList = deviceUsedService.pageByStore(merchantStore.getId(),whereRequestTemp ).getList();
			Map<String,DeviceUsed> deviceUsedMap = deviceUsedListToBoardNoMap(deviceUsedList);
            //入库
			String notificationUUID = UUID.randomUUID().toString();
			pushResourceDTO.setNotificationUUID(notificationUUID);
			pushResourceDTO.setUserId(adminUser.getId());
            isSuccess = endpointInterface.receiveNotice(pushResourceDTO);
            notification.setAppId(pushResourceDTO.getAppId());
			notification.setOperationType("send");
            notification.setContent(JSONArray.toJSONString(pushResourceDTO.getContent()));
            notification.setStoragePath(pushResourceDTO.getStoragePath());
            notification.setTimeout(pushResourceDTO.getTimeout());
			notification.setDeviceUsedName(deviceUsedMap.get(pushResourceDTO.getUniqueNo()).getName());
            notification.setUniqueNo(pushResourceDTO.getUniqueNo());
            notification.setAdminUser(adminUser);
            notification.setStore(merchantStore);
            notification.setCreateTime(new Date());
            notification.setNoticeType(pushResourceDTO.getNoticeType());
			notification.setUuid(notificationUUID);
            if (isSuccess) {
                notification.setSuccess(true);
                notification.setComment("推送成功");
                notificationService.update(notification);
                return AjaxResponse.success("推送成功");
            } else {
                notification.setSuccess(false);
                notification.setComment("发送失败，客户端出错");
                notificationService.update(notification);
                return AjaxResponse.failed(-6, "发送失败，客户端出错");
            }
        } catch (ServiceException e) {
            LOGGER.error(e.getMessage(), e);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return AjaxResponse.failed(-5, "客户端不存在或网络无法连接");
        }
        return null;
    }

    final static Charset charset = Charset.forName("UTF8");

    /**
     * 移动文件至回收站
     *
     * @param file
     * @return
     */
    public boolean copyToRecycle(File file) {
        try {
            if (file.isFile()) {
                String targetPath = getDeletedPath(file);
                // Destination directory
                File dir = new File(targetPath);
                File parentPath = new File(dir.getParent());
                parentPath.mkdirs();
                // Move file to new directory
                com.myee.tarot.core.util.FileUtils.copyFile(file, dir);
                return true;
            } else if (!file.exists()) {
                LOGGER.info("所删除的文件不存在！" + '\n');
                return false;
            } else {
                File files[] = file.listFiles();
                if (files != null && files.length > 0) {
                    for (int i = 0; i < files.length; i++) {
                        copyToRecycle(files[i]);
                    }
                } else { //空目录
                    String targetPath = getDeletedPath(file);
                    // Destination directory
                    File dir = new File(targetPath);
                    if (dir.exists()) {
                        boolean result = file.delete();
                        return result;
                    } else {
                        com.myee.tarot.core.util.FileUtils.copyFile(file, dir);
                        return true;
                    }
                }
                return false;
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            LOGGER.info("unable to copy the file!");
        }
        return false;
    }

    /**
     * 获取移除文件到回收站的后的文件路径
     * @param file
     * @return
     */
    private String getDeletedPath(File file) {
        String tempFilePath = file.getPath().replaceAll("\\\\", "/");//把路径中的反斜杠替换成斜杠
        String tempDownloadPath = DOWNLOAD_HOME.replaceAll("\\\\", "/") + "/";//准备用于替换成url的下载文件夹路径
        String tempTargetPath = (DOWNLOAD_HOME + File.separator + "deleted" + File.separator).replaceAll("\\\\", "/");
        String targetPath = tempFilePath.replaceAll(tempDownloadPath, tempTargetPath);
        return targetPath;
    }

    /*
     * 通过递归得到某一路径下所有的目录及其文件
    */
    static boolean getFiles(File root) {
        File[] files = root.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                /*
                 * 递归调用
                */
                getFiles(file);
            } else {
                // 跳出
                throw new StopMsgException();
            }
        }
        return false;
    }

    static class StopMsgException extends RuntimeException {
    }

	private Map<String, FileItem> getTreeMap(){
		Map<String, FileItem> resultMap = new TreeMap<>(new Comparator<String>() {
			@Override
			public int compare(String str1, String str2) {
				if(null == str1){
					return -1;
				}
				return str1.compareToIgnoreCase(str2);
			}
		});
		return resultMap;
	}

	@RequestMapping(value = "admin/file/getNoticeType", method = RequestMethod.GET)
	@ResponseBody
	public List getNoticeType() {
		AjaxResponse resp = new AjaxResponse() ;
		try {
			for (NoticeType noticeType : NoticeType.values()) {
				Map entry = new HashMap();
				entry.put("name",noticeType.getValue());
				entry.put("value",noticeType.getCaption());
				resp.addDataEntry(entry);
			}
			return resp.getRows();
		}catch (Exception e){
            LOGGER.error(e.getMessage(), e);
		}
		return null;
	}

    @RequestMapping(value = "admin/file/getAppType", method = RequestMethod.GET)
    @ResponseBody
    public List getAppType() {
        AjaxResponse resp = new AjaxResponse() ;
        try {
            for (AppType appType : AppType.values()) {
                Map entry = new HashMap();
                entry.put("name",appType.getValue());
                entry.put("value",appType.getCaption());
                resp.addDataEntry(entry);
            }
            return resp.getRows();
        }catch (Exception e){
            LOGGER.error(e.getMessage(),e);
        }
        return null;
    }

	/*@RequestMapping("admin/test/uploadagenterrorfile")
	@ResponseBody
	public AjaxResponse uploadAgentErrorFile(HttpServletRequest request) {
		try {
			TrunkingInterface endpointInterface = serverBootstrap.getClient(TrunkingInterface.class, "TEST123456");
			JSONObject object = new JSONObject();
			object.put("fileType", "error");
			boolean result = endpointInterface.uploadFile(JSON.toJSONString(object));
			if(result){
				return AjaxResponse.success("命令发送成功，正在上传……");
			}
			return AjaxResponse.failed(-1, "发送命令出错");
		} catch (Exception e) {
			return AjaxResponse.failed(-2, "连接客户端错误");
		}
	}*/
}
