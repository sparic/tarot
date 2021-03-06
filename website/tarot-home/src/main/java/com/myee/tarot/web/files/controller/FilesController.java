package com.myee.tarot.web.files.controller;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.myee.tarot.admin.domain.AdminUser;
import com.myee.tarot.core.Constants;
import com.myee.tarot.core.util.StringUtil;
import com.myee.tarot.core.util.ajax.AjaxResponse;
import com.myee.tarot.fileUpload.domain.FileUpload;
import com.myee.tarot.fileUpload.service.FileUploadService;
import com.myee.tarot.merchant.domain.MerchantStore;
import com.myee.tarot.web.apiold.util.CommonLoginParam;
import com.myee.tarot.web.apiold.util.FileValidCreateUtil;
import com.myee.tarot.web.files.*;
import jodd.io.FileUtil;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.*;

/**
 * Created by Martin on 2016/4/21.
 */
@Controller
public class FilesController {

    private static final Logger LOGGER = LoggerFactory.getLogger(FilesController.class);

    @Autowired
    private FileUploadService fileUploadService;

    @Value("${cleverm.push.dirs}")
    private String DOWNLOAD_HOME;
    @Value("${cleverm.push.http}")
    private String DOWNLOAD_HTTP;
    private static final String GZIP_DIR = "temp/";

    private static final String RESOURCE_TYPE_DIR = "default";
    private static final String RESOURCE_TYPE_FILE = "file";

    private static final String RESOURCE_PARAM_ID = "id";
    private static final String RESOURCE_PARAM_OPERATION = "operation";
    private static final String RESOURCE_PARAM_TEXT = "text";
    private static final String RESOURCE_PARAM_TYPE = "type";

    private static final String RESOURCE_OPERATION_CREATE = "create_node";
    private static final String RESOURCE_OPERATION_DELETE = "delete_node";
    private static final String RESOURCE_OPERATION_RENAME = "rename_node";

   /* @RequestMapping(value = "/admin/files/list.html")
    public
    @ResponseBody
    JQGridResponse processListFiles(HttpServletRequest http) {
        JQGridResponse resp = new JQGridResponse();
        File dir = DOWNLOAD_HOME;
//        if (null != req.getNodeid()) {
//            dir = FileUtils.getFile(req.getNodeid());
//        }
        Map<String, FileDTO> resMap = Maps.newHashMap();
        listFiles(dir, resMap);
        List<FileDTO> dtos = Lists.newArrayList(resMap.values());
        Collections.sort(dtos);
        resp.getRows().addAll(dtos);
        return resp;
    }*/

    @RequestMapping(value = "admin/files/list")
    @ResponseBody
    public List<TreeFileItem> processListFiles(HttpServletRequest request, HttpServletResponse response) {
        List<TreeFileItem> tree = Lists.newArrayList();
        String id = request.getParameter(RESOURCE_PARAM_ID);
        File dir = null;
        if (id.equals("#")) {
            dir = new File(DOWNLOAD_HOME + File.separator + ((MerchantStore) request.getSession().getAttribute(Constants.ADMIN_STORE)).getId());
            //resp.put("text",dir.getPath());
            //resp.put("id",dir.getPath());
        } else {
            dir = new File(id);
        }
//        if (null != req.getNodeid()) {
//            dir = FileUtils.getFile(req.getNodeid());
//        }
        Map<String, FileDTO> resMap = Maps.newHashMap();
        listFiles(dir, resMap, request);
        List<FileDTO> dtos = Lists.newArrayList(resMap.values());
        Collections.sort(dtos);
        for (FileDTO dto : dtos) {
            if (dto.isLeaf()) {
                continue;
            }
            TreeFileItem jt = new TreeFileItem();
            jt.setId(dto.getId());
            jt.setChildren(!dto.isLeaf());
            jt.setText(dto.getName());
            jt.setType(dto.isLeaf() ? RESOURCE_TYPE_FILE : RESOURCE_TYPE_DIR);
            jt.setLastModify(new Date(dto.getMtime()));
            jt.setDetailType(dto.getType());
            jt.setDownloadPath(getDownloadPath(dto.getId(), dto.isLeaf()));
            tree.add(jt);
        }
        return tree;
    }

    //根据FileDTO换算对应的下载文件的URL
    private String getDownloadPath(String filePath, Boolean isLeaf) {
        if (isLeaf) {//是文件，才有下载链接
            String tempFilePath = filePath.replaceAll("\\\\", "/");//把路径中的反斜杠替换成斜杠
            String tempDownloadPath = DOWNLOAD_HOME.replaceAll("\\\\", "/") + "/";//准备用于替换成url的下载文件夹路径
            String path = tempFilePath.replaceAll(tempDownloadPath, DOWNLOAD_HTTP);
            return path;
        }
        return "";
    }

    @RequestMapping(value = "admin/files/showList")
    @ResponseBody
    public AjaxResponse listFiles(HttpServletRequest request, HttpServletResponse response) {
        AjaxResponse resp = new AjaxResponse();
        List<TreeFileItem> tree = Lists.newArrayList();
        String id = request.getParameter(RESOURCE_PARAM_ID);
        if (id == null) {
            return AjaxResponse.failed(-1);
        }

        File dir = null;
        if (id.equals("#")) {
            dir = new File(DOWNLOAD_HOME + File.separator + ((MerchantStore) request.getSession().getAttribute(Constants.ADMIN_STORE)).getId());
            //resp.put("text",dir.getPath());
            //resp.put("id",dir.getPath());
        } else {
            dir = new File(id);
        }
//        if (null != req.getNodeid()) {
//            dir = FileUtils.getFile(req.getNodeid());
//        }
        Map<String, FileDTO> resMap = Maps.newHashMap();
        listFiles(dir, resMap, request);
        List<FileDTO> dtos = Lists.newArrayList(resMap.values());
        Collections.sort(dtos);
        for (FileDTO dto : dtos) {
            if (!dto.isLeaf()) {
                continue;
            }
            TreeFileItem jt = new TreeFileItem();
            jt.setId(dto.getId());
            jt.setChildren(!dto.isLeaf());
            jt.setText(dto.getName());
            jt.setType(dto.isLeaf() ? RESOURCE_TYPE_FILE : RESOURCE_TYPE_DIR);
            jt.setLastModify(new Date(dto.getMtime()));
            jt.setDetailType(dto.getType());
            jt.setDownloadPath(getDownloadPath(dto.getId(), dto.isLeaf()));
            tree.add(jt);
        }
        resp.addEntry("tree", tree);
        return resp;
    }

    /**
     * 新建文件夹，重命名、删除文件和文件夹
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "admin/files/change")
    @ResponseBody
    @PreAuthorize("hasAuthority('explorer_explorer_u')")
    public TreeFileItem changeFile(HttpServletRequest request, HttpServletResponse response) {
        AjaxResponse resp = new AjaxResponse();
        String operation = request.getParameter(RESOURCE_PARAM_OPERATION);
        String id = request.getParameter(RESOURCE_PARAM_ID);
        String text = request.getParameter(RESOURCE_PARAM_TEXT);
        String type = request.getParameter(RESOURCE_PARAM_TYPE);
        if (operation.equals(RESOURCE_OPERATION_CREATE)) {
            File file = new File(id, text);
            try {
                boolean isNew = false;
                if (type.equals(RESOURCE_TYPE_DIR)) {
                    //创建文件夹并不实际创建
                    //isNew = file.mkdirs();
                    isNew = true;
                }
                //20160708创建文件移到另一个接口
//                else if(type.equals("file")){
//                    if(!file.getParentFile().exists()){
//                        file.getParentFile().mkdirs();
//                    }
//                    if(!file.exists()){
//                        isNew = file.createNewFile();
//                    }
//                }
                if (isNew) {
                    return new TreeFileItem(file.getPath());
                }
            } catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
        if (operation.equals(RESOURCE_OPERATION_DELETE)) {
            try {
                File file = new File(id);
                //目前不允许删除文件夹
                if (file.isDirectory()) {
                    return null;
                }

                boolean isCopy = moveToRecycle(file);//复制文件到回收站
                if (isCopy) { //复制成功后执行删除
                    boolean isDelete = delete(file);

                    if (isDelete) {
                        TreeFileItem tree = new TreeFileItem();
                        tree.setStatus("OK");
                        return tree;
                    }
                }

            } catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
        if (operation.equals(RESOURCE_OPERATION_RENAME)) {
            try {
                boolean isRename = false;
                File file = new File(id);
                String parent = file.getParent();
                File newFile = new File(parent, text);
                if (file.exists()) {
                    if (file.exists()) {
                        isRename = file.renameTo(newFile);
                    }
                    if (isRename) {
                        return new TreeFileItem(newFile.getPath());
                    }
                } else {
                    return new TreeFileItem(newFile.getPath());
                }
            } catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
        return null;
    }

    /**
     * 上传文件
     *
     * @param file
     * @param path
     * @param request
     * @return
     * @throws IllegalStateException
     * @throws IOException
     */
    @RequestMapping(value = {"services/public/files/upload", "admin/files/create", "shop/files/create"})
    @ResponseBody
    public AjaxResponse createResource(@RequestParam("file") CommonsMultipartFile file, String path, @RequestParam(value = "storeId", required = false) Long storeId, HttpServletRequest request) throws IllegalStateException, IOException {
        AjaxResponse resp = AjaxResponse.success();
        try {
            String type = request.getParameter("type");
            if (type == null) {
                resp = AjaxResponse.failed(-1);
                resp.setErrorString("参数错误");
                return resp;
            }
            String storeIdStr;
            if (storeId == null) {
                String requestPath = request.getServletPath();
                String sessionName = CommonLoginParam.getRequestInfo(request).get(Constants.REQUEST_INFO_SESSION).toString();
                MerchantStore merchantStore = (MerchantStore) request.getSession().getAttribute(sessionName);
                storeIdStr = String.valueOf(merchantStore.getId());
            } else {
                storeIdStr = String.valueOf(storeId);
            }
            if (StringUtil.isNullOrEmpty(storeIdStr) || StringUtil.equals("null", StringUtil.toLowerCase(storeIdStr))) {
                return AjaxResponse.failed(-1, "店铺ID不能为空");
            }
            File dest = FileUtils.getFile(DOWNLOAD_HOME, storeIdStr, File.separator + path);
            if (type.equals(RESOURCE_TYPE_DIR)) {
                dest.mkdirs();
            } else if (!file.isEmpty() && type.equals(RESOURCE_TYPE_FILE)) {
//                System.out.println(file.getFileItem().getName());
                dest.mkdirs();
//                String fileName = file.getFileItem().getName();
                String fileName = file.getOriginalFilename();
                dest = FileUtils.getFile(dest.getPath(), File.separator + fileName);
                if(!dest.exists()) {
                    dest.createNewFile();
                }
                file.transferTo(dest);
                TreeFileItem jt = new TreeFileItem();
                jt.setId(dest.getPath());
                jt.setChildren(false);
                jt.setText(fileName);
                jt.setType("file");
                jt.setLastModify(new Date(dest.lastModified()));
                jt.setDetailType(FilenameUtils.getExtension(fileName));
                jt.setDownloadPath(storeIdStr + "/" + path + "/" + file.getFileItem().getName());//返回相对路径
                jt.setMd5(FileValidCreateUtil.fileMD5(dest.getPath()));
                resp.addEntry("tree", jt);
            }
            //20160708文本编辑放到另一个接口，以后再做
//        else if (!StringUtil.isNullOrEmpty(vo.getContent(), true)) {
//            FileUtils.writeStringToFile(dest, vo.getContent());
//        }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            resp = AjaxResponse.failed(-1);
            resp.setErrorString("出错");
        }
        return resp;
    }

    /**
     * 判断资源文件是否存在
     * @param path
     * @param storeId
     * @param request
     * @return
     * @throws IllegalStateException
     * @throws IOException
     */
    @RequestMapping(value = {"admin/files/exist"})
    @ResponseBody
    public AjaxResponse isResourceExist(String path, @RequestParam(value = "storeId", required = false) Long storeId, HttpServletRequest request) throws IllegalStateException, IOException {
        AjaxResponse resp = AjaxResponse.success();
        String storeIdStr;
        if (storeId == null) {
            String requestPath = request.getServletPath();
            String sessionName = CommonLoginParam.getRequestInfo(request).get(Constants.REQUEST_INFO_SESSION).toString();
            MerchantStore merchantStore = (MerchantStore) request.getSession().getAttribute(sessionName);
            storeIdStr = String.valueOf(merchantStore.getId());
        } else {
            storeIdStr = String.valueOf(storeId);
        }
        if (StringUtil.isNullOrEmpty(storeIdStr) || StringUtil.equals("null", StringUtil.toLowerCase(storeIdStr))) {
            return AjaxResponse.failed(-1, "店铺ID不能为空");
        }
        File dest = FileUtils.getFile(DOWNLOAD_HOME, storeIdStr, File.separator + path);
        Map entry = new HashMap();
        if( dest.exists() ){
            entry.put(Constants.FILE_IS_EXIST,true);
        }
        else {
            entry.put(Constants.FILE_IS_EXIST,false);
        }
        resp.addDataEntry(entry);
        return resp;
    }

    /**
     * 用zip压缩文件，源文件的文件名不能包含空格
     *
     * @param pushRes
     * @param compress
     * @param request
     * @return
     */
    @RequestMapping("admin/files/packResource")
    @ResponseBody
    public HotfixSetVo packResource(String pushRes, boolean compress, HttpServletRequest request) {
        HotfixSetVo hotfixSetVo = new HotfixSetVo();
        List<TreeFileItem> resList = JSON.parseArray(pushRes, TreeFileItem.class);
        Set<HotfixVo> hotfixSet = Sets.newHashSet();
        hotfixSetVo.setPublisher(currentUser(request).getName());
        if (compress) {
            String basePath = DOWNLOAD_HOME + "/" + GZIP_DIR;
            String fileName = "";
            /*File gzFile = new File(basePath);
            gzFile.mkdirs(); //建立压缩文件根目录*/
            File zipFile = new File(basePath);
            zipFile.mkdirs(); //建立压缩文件根目录
            if (null != resList) {
                for (TreeFileItem treeFileItem : resList) {
                    //用gz压缩文件:gz里面的文件名也会变成md5的名字，不太友好
                    /*fileName = CryptoUtil.md5(treeFileItem.getText()) + ".gz";
                    gzFile = FileUtils.getFile(basePath, fileName);
                    File source = FileUtils.getFile(treeFileItem.getId());
                    if(!gzFile.exists()){//如果压缩文件不存在，则执行压缩
                        GZipUtil.doCompressFile(source,gzFile);
                    }*/
                    //用zip压缩文件，源文件的文件名不能包含空格
//                    fileName = CryptoUtil.md5(treeFileItem.getText()) + ".zip";
//                    zipFile = FileUtils.getFile(basePath, fileName);
//                    if(!zipFile.exists()){//如果压缩文件不存在，则执行压缩
//                        ZipUtil.compress(treeFileItem.getId(),treeFileItem.getText(), zipFile.getPath());
//                    }
//                    hotfixSet.add(new HotfixVo(fileName, getGzipUrl(zipFile), null, true));//将压缩文件信息写入接口返回信息中
                }
            }
        } else {
            if (resList != null && resList.size() > 0) {
                for (TreeFileItem treeFileItem : resList) {
                    if (RESOURCE_TYPE_FILE == treeFileItem.getType()) {
                        String targetDir = treeFileItem.getId();
                        hotfixSet.add(new HotfixVo(treeFileItem.getText(), getDownloadPath(treeFileItem.getId(), true), targetDir, false));
                    }
                }
            }
        }
        hotfixSetVo.setHotfixSet(hotfixSet);
        return hotfixSetVo;
    }

    public AdminUser currentUser(HttpServletRequest request) {
        return (AdminUser) request.getSession().getAttribute(Constants.ADMIN_USER);
    }

    String getGzipUrl(File zipFile) {
        return DOWNLOAD_HTTP + GZIP_DIR + zipFile.getName();
    }

    private void listFiles(File parentFile, Map<String, FileDTO> resMap, HttpServletRequest request) {
        if (!parentFile.exists() || !parentFile.isDirectory() || null == parentFile.listFiles()) {
            return;
        }
        for (File file : parentFile.listFiles()) {
            FileDTO resourceVo = new FileDTO(file, new File(DOWNLOAD_HOME + File.separator + ((MerchantStore) request.getSession().getAttribute(Constants.ADMIN_STORE)).getId()));
            resMap.put(file.getName(), resourceVo);
        }
    }

    /**
     * 循环删除文件夹及文件
     *
     * @param file
     * @return
     */
    public static boolean delete(File file) {
        try {
            FileUtil.delete(file);
            return true;
        } catch (IOException e) {
            LOGGER.error("Unable to delete: ", e);
            return false;
        }
    }

    /**
     * 移到文件至回收站
     *
     * @param file
     * @return
     */
    public boolean moveToRecycle(File file) {
        try {
            if (file.exists()) {
                String tempFilePath = file.getPath().replaceAll("\\\\", "/");//把路径中的反斜杠替换成斜杠
                String tempDownloadPath = DOWNLOAD_HOME.replaceAll("\\\\", "/") + "/";//准备用于替换成url的下载文件夹路径
                String tempTargetPath = (DOWNLOAD_HOME + File.separator + "deleted" + File.separator).replaceAll("\\\\", "/");
                String targetPath = tempFilePath.replaceAll(tempDownloadPath, tempTargetPath);
//                targetPath = targetPath.replaceAll("/","\\\\");//把路径转回linux兼容
                if (file.isFile()) {
                    // Destination directory
                    File dir = new File(targetPath);
                    File parentPath = new File(dir.getParent());
                    parentPath.mkdirs();
                    // Move file to new directory
                    boolean success = file.renameTo(dir);
                } else if (file.isDirectory()) {
                    File files[] = file.listFiles();
                    for (int i = 0; i < files.length; i++) {
                        moveToRecycle(files[i]);
                    }
                }
                return true;
            } else {
                LOGGER.error("所删除的文件不存在！" + '\n');
                return false;
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(),e);
            LOGGER.error("unable to delete the folder!");
        }
        return false;
    }


    //-----------------------------为了视觉心理分析平台视频传输-----------------------------------

    /**
     * 上传大个文件 支持断点续传
     *
     * @return
     * @throws IllegalStateException
     * @throws IOException
     */
    @RequestMapping(value = "services/public/files/largeUpload", method = RequestMethod.POST)
    @ResponseBody
    public AjaxResponse uploadLargeFile(HttpServletRequest request) throws IllegalStateException, IOException {
        String fileName = request.getParameter("fileName");
        InputStream in = request.getInputStream();
        RandomAccessFile tempRandAccessFile = null;
        long length = request.getContentLength();
        File real = null;
        File temp = null;
        boolean isSuccess = false;
        try {
            File upload = FileUtils.getFile(DOWNLOAD_HOME, "upload");
            upload.mkdirs();
            real = FileUtils.getFile(upload.getPath(), File.separator + fileName);
            temp = FileUtils.getFile(upload.getPath(), File.separator + fileName + ".tmp");
            if(real.exists()) {
                return AjaxResponse.success();
            }else {
                long needSkipBytes = 0;
                if(temp.exists()){
                    //续一哈
                    needSkipBytes = temp.length();
                } else {
                    temp.createNewFile();
                }
                System.out.println("跳过字节数为：" + needSkipBytes);
                //in.skip(needSkipBytes);
                tempRandAccessFile = new RandomAccessFile(temp, "rw");
                tempRandAccessFile.seek(needSkipBytes);
                byte[] buffer = new byte[4096];
                int l;
                if(length < 0L) {
                    while((l = in.read(buffer)) != -1) {
                        tempRandAccessFile.write(buffer, 0, l);
                    }
                } else {
                    for(long remaining = length - needSkipBytes; remaining > 0L; remaining -= (long)l) {
                        l = in.read(buffer, 0, (int)Math.min(4096L, remaining));
                        if(l == -1) {
                            break;
                        }
                        tempRandAccessFile.write(buffer, 0, l);
                    }
                }
                isSuccess = true;
            }
            // 传输成功后，添加至数据库
            FileUpload uploadFile = fileUploadService.getByName(fileName);
            if(uploadFile !=null){
                uploadFile.setUpdateTime(new Date());
                uploadFile.setLength(temp.length());
                uploadFile.setStatus(Constants.FILE_OK);
                //uploadFile.setMd5(FileValidCreateUtil.fileMD5(real.getAbsolutePath()));
                fileUploadService.update(uploadFile);
            }else {
                FileUpload newFile = new FileUpload();
                newFile.setCreateTime(new Date());
                newFile.setUpdateTime(newFile.getCreateTime());
                newFile.setLength(temp.length());
                newFile.setName(fileName);
                newFile.setStatus(Constants.FILE_OK);
                newFile.setPath(real.getPath());
                // newFile.setMd5(FileValidCreateUtil.fileMD5(real.getAbsolutePath()));
                fileUploadService.save(newFile);
            }
            return AjaxResponse.success();

        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return AjaxResponse.failed(-1);

        } finally {
            try {
                in.close();
                if(tempRandAccessFile !=null) {
                    tempRandAccessFile.close();
                }
                if(isSuccess){
                    temp.renameTo(real);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 判定是否已存在此文件
     * @param name  文件名称
     * @param md5   文件md5
     */
    @RequestMapping(value = "services/public/files/isExistFile",method = RequestMethod.POST)
    @ResponseBody
    public FileResult isExistFile(@RequestParam(value = "fileName")String name,
                                  @RequestParam(value = "md5",required = false)String md5){
        FileResult result = new FileResult();
        try {
            FileUpload existFile = fileUploadService.getByName(name);
            if(existFile != null ){
                //若存在 直接
                result.setExist(true);
            }else {
                //若不存在,是否存在临时文件
                result.setExist(false);
                File upload = FileUtils.getFile(DOWNLOAD_HOME, "upload");
                File temp = FileUtils.getFile(upload.getAbsolutePath(),name + ".tmp");
                if(temp.exists()&& temp.isFile()){
                    result.setTemp(true);
                    result.setSize(temp.length());
                }else {
                    result.setTemp(false);
                }
            }
            result.setStatus(0);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return new FileResult(1);
        }
    }



}