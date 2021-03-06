package com.myee.tarot.djinn.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.myee.djinn.dto.NotificationDTO;
import com.myee.djinn.dto.ShopDetail;
import com.myee.djinn.dto.VersionInfo;
import com.myee.djinn.rpc.RemoteException;
import com.myee.djinn.server.operations.CommonService;
import com.myee.tarot.admin.domain.AdminUser;
import com.myee.tarot.admin.service.AdminUserService;
import com.myee.tarot.catalog.domain.DeviceUsed;
import com.myee.tarot.catalog.service.DeviceUsedService;
import com.myee.tarot.configuration.domain.UpdateConfig;
import com.myee.tarot.configuration.domain.UpdateConfigProductUsedXREF;
import com.myee.tarot.configuration.service.UpdateConfigProductUsedXREFService;
import com.myee.tarot.core.Constants;
import com.myee.tarot.core.service.TransactionalAspectAware;
import com.myee.tarot.core.util.DateTimeUtils;
import com.myee.tarot.core.util.EmailSenderUtil;
import com.myee.tarot.core.util.FileUtils;
import com.myee.tarot.core.util.StringUtil;
import com.myee.tarot.core.util.ajax.AjaxResponse;
import com.myee.tarot.merchant.domain.Merchant;
import com.myee.tarot.merchant.domain.MerchantStore;
import com.myee.tarot.merchant.service.MerchantStoreService;
import com.myee.tarot.profile.domain.Address;
import com.myee.tarot.resource.domain.Notification;
import com.myee.tarot.resource.service.NotificationService;
import org.apache.http.HttpStatus;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.*;

/**
 * Created by Martin on 2016/9/6.
 */
@Service
public class CommonServiceImpl implements CommonService, TransactionalAspectAware {

	private static final Logger LOG = LoggerFactory.getLogger(CommonServiceImpl.class);
	@Autowired
	private DeviceUsedService deviceUsedService;
	@Autowired
	private NotificationService notificationService;
	@Autowired
	private MerchantStoreService merchantStoreService;
	@Autowired
	private AdminUserService adminUserService;
	@Autowired
	private UpdateConfigProductUsedXREFService updateConfigProductUsedXREFService;
	@Value("${cleverm.push.dirs}")
	private String DOWNLOAD_HOME;

	public static final String FROM = "cloudmanager@mrobot.cn";  //密码 Cloud161230
	public static final String[] TO = {"jelynn.tang@mrobot.cn"};

	private List<String> typeList = new ArrayList<>(Arrays.asList("ipc", "app", "tinker", "tinker_test"));

	@Override
	public Boolean isConnection() throws RemoteException {
		return true;
	}

	/**
	 * 原升级方案接口
	 * @param jsonArgs
	 * @return
	 * @throws RemoteException
	 */
	@Override
	public VersionInfo latestVersion(String jsonArgs) throws RemoteException {
		LOG.info("jsonArgs= {}  DOWNLOAD_HOME={}", jsonArgs, DOWNLOAD_HOME);
		VersionInfo info = new VersionInfo();
		try {
			JSONObject object = JSON.parseObject(jsonArgs);
			String name = object.getString("name");
			String type = object.getString("type");
			String orgId = object.getString("orgId");
			StringBuilder sb = new StringBuilder();
			LOG.info("================ request info  name:{} type:{} orgId:{}", name, type, orgId);
			if ("app".equals(type) || "ipc".equals(type)) {
				sb.append(DOWNLOAD_HOME).append(File.separator).append(orgId).append(File.separator).append(type).append(File.separator).append(name).append(File.separator).append("VersionInfo.xml");
				LOG.info("========File path {} " + sb.toString());
				File file = new File(sb.toString());
				if (file.exists()) {
					info = readfile(file);
				}
			}else if(type.indexOf("tinker") >= 0){
				sb.append(DOWNLOAD_HOME).append(File.separator).append(orgId).append(File.separator).append(type).append(File.separator).append(name).append(File.separator).append("VersionInfo.txt");
				LOG.info("========File path {} " + sb.toString());
				String str = FileUtils.readTXT(sb.toString());
				info = JSON.parseObject(str, VersionInfo.class);
			}
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			LOG.info("Error Message {} " + e.getMessage());
		}
		return info;
	}

	/**
	 * 新升级方案接口
	 * @param jsonArgs
	 * @return
	 * @throws RemoteException
	 */
	@Override
	public String latestVersionStr(String jsonArgs) throws RemoteException {
		LOG.info("jsonArgs= {}  DOWNLOAD_HOME={}", jsonArgs, DOWNLOAD_HOME);
		String latestVersionStr = "";
		JSONObject object = JSON.parseObject(jsonArgs);
		String name = object.getString("name");
		String type = object.getString("type");
		String orgId = object.getString("orgId");
		String deviceGroupNO = object.getString("deviceGroupNO");

		//通过 type  deviceGroupNO 获取到对应设备组和配置记录的关系表数据
		UpdateConfigProductUsedXREF updateConfigProductUsedXREF = updateConfigProductUsedXREFService.getByTypeAndDeviceGroupNO(type, deviceGroupNO);
		if (null == updateConfigProductUsedXREF) {
			return latestVersionStr;
		}
		UpdateConfig updateConfig = updateConfigProductUsedXREF.getUpdateConfig();
		if(Constants.UPDATE_SEE_TYPE_NONE.equals(updateConfig.getSeeType())){
			return latestVersionStr;
		}
		String configFilePath = updateConfig.getPath();
		configFilePath = DOWNLOAD_HOME + File.separator + Constants.ADMIN_PACK + File.separator + configFilePath;
		LOG.info("configFilePath ={} ", configFilePath);
		latestVersionStr = FileUtils.readTXT(configFilePath);
		LOG.info("latestVersionStr ={} ", latestVersionStr);
		return latestVersionStr;
	}

	@Override
	public ShopDetail ownerShop() throws RemoteException {
		return null;
	}

	@Override
	public ShopDetail ownerShop(String consistentId) throws RemoteException {
		LOG.info(" ownerShop consistentId = {}", consistentId);
		DeviceUsed deviceUsed = deviceUsedService.getByBoardNo(consistentId);
		MerchantStore merchantStore = deviceUsed.getStore();
//		List<TableType> tableTypeList = tableTypeService.listByStore(merchantStore.getId());
//		for (TableType tableType : tableTypeList) {
//			tableType.setStore(null);
//		}
//		Map<String, String> map = new HashMap<String, String>();
//		if (merchantStore != null) {
//			map.put("shopInfo", JSON.toJSONString(merchantStore));
//			map.put("tableType", JSON.toJSONString(tableTypeList));
//		}
		ShopDetail shopDetail = toDto(merchantStore);
		shopDetail.setOpeningTime(toHourString(merchantStore.getTimeOpen()) + "~" + toHourString(merchantStore.getTimeClose()));
		LOG.info(" ownerShop shopDetail = {}", JSON.toJSONString(shopDetail));
		return shopDetail;
	}

	private Notification convertToNotification(NotificationDTO notificationDTO) {
		Notification notification = new Notification();
		notification.setComment(notificationDTO.getComment());
		notification.setContent(notificationDTO.getContent());
		notification.setCreateTime(notificationDTO.getCreateTime());
		notification.setOperationType(notificationDTO.getOperationType());
		notification.setNoticeType(notificationDTO.getNoticeType());
		notification.setAppId(notificationDTO.getAppId());
		notification.setStoragePath(notificationDTO.getStoragePath());
		notification.setCreateTime(notificationDTO.getCreateTime());
		MerchantStore store = merchantStoreService.findById(notificationDTO.getOrgId());
		notification.setStore(store);
		AdminUser adminUser = adminUserService.findById(notificationDTO.getUserId());
		notification.setAdminUser(adminUser);
		notification.setSuccess(notificationDTO.isSuccess());
		notification.setDeviceUsedName(deviceUsedService.getByBoardNo(notificationDTO.getBoardNO()).getName());
		notification.setUniqueNo(notificationDTO.getBoardNO());
		notification.setUuid(notificationDTO.getUuid());
		return notification;
	}

	@Override
	public boolean receiveNotice(NotificationDTO notificationDTO) throws RemoteException {
		LOG.info(" receiveNotice notificationDTO = {}", JSON.toJSONString(notificationDTO));
		Notification notification = convertToNotification(notificationDTO);
		notificationService.save(notification);
		return true;
	}

	@Override
	public boolean sendEmail(String jsonArgs) throws RemoteException {
		LOG.info(" sendEmail jsonArgs = {}", jsonArgs);
		JSONObject object = JSON.parseObject(jsonArgs);
		String boardNO = object.getString(com.myee.djinn.constants.Constants.BOARD_NO);
		String errorInfo = object.getString(com.myee.djinn.constants.Constants.ERROR_INFO);
		String time = object.getString(com.myee.djinn.constants.Constants.TIME);
		String subject = object.getString(com.myee.djinn.constants.Constants.SUBJECT);
		long timeStamp = time == null ? 0 : Long.parseLong(time);
		Date sendDate = new Date();
		if (timeStamp != 0) {
			sendDate = new Date(timeStamp);
		}
		//查询店铺
		DeviceUsed deviceUsed = deviceUsedService.getByBoardNo(boardNO);
		MerchantStore merchantStore = deviceUsed.getStore();
		//拼接邮件内容

		StringBuilder sb = new StringBuilder();
		sb.append("主板编号：").append(boardNO).append("\n");
		sb.append("设备名称：").append(deviceUsed.getName()).append("\n");
		sb.append("店铺名称：").append(merchantStore.getName()).append("\n");
		sb.append("店铺电话：").append(merchantStore.getPhone()).append("\n");
		sb.append("报错时间：").append(DateTimeUtils.getDefaultDateString(sendDate)).append("\n");
		sb.append("错误信息：").append("\n");
		sb.append(errorInfo).append("\n");
		EmailSenderUtil emailSenderUtil = new EmailSenderUtil();
		subject = StringUtil.isNullOrEmpty(subject) ? "发生异常" : subject;
		AjaxResponse response = emailSenderUtil.send(FROM, TO, subject, sb.toString());
		LOG.info(" sendEmail result = {}", response.getStatus());
		return response.getStatus() == 0;
	}

	private VersionInfo readfile(File file) {
		SAXReader saxReader = new SAXReader();
		JSONObject object = new JSONObject();
		VersionInfo versionInfo = new VersionInfo();
		try {
			Document document = saxReader.read(file);
			Element root = document.getRootElement();
			// 迭代
			for (Iterator iter = root.elementIterator(); iter.hasNext(); ) {
				Element e = (Element) iter.next();
				object.put(e.getName(), e.getData());
			}
			versionInfo = JSON.parseObject(object.toJSONString(), VersionInfo.class);
		} catch (DocumentException e) {
			LOG.error(e.getMessage(), e);
		}
		return versionInfo;
	}

	String toHourString(Date time) {
		if (null == time) {
			return "";
		}
		return new DateTime(time.getTime()).toString("HH:mm");
	}

	public ShopDetail toDto(MerchantStore merchantStore) {
		ShopDetail shopDetail = new ShopDetail();
		shopDetail.setShopId(merchantStore.getId());
		shopDetail.setShopName(merchantStore.getName());
		shopDetail.setCode(merchantStore.getCode());
		shopDetail.setPhone(merchantStore.getPhone());
		shopDetail.setPostalCode(merchantStore.getPostalCode());
		Address address = merchantStore.getAddress();
		shopDetail.setAddress(address != null ? address.toString() : "");
		shopDetail.setRatings(merchantStore.getRatings());
		shopDetail.setCpp(merchantStore.getCpp());
		shopDetail.setDescription(merchantStore.getDescription());
		shopDetail.setExperience(merchantStore.isExperience());
		shopDetail.setShopType(merchantStore.getStoreType());
		Float score = merchantStore.getScore();
		shopDetail.setScore(score == null ? 0 : score);
		Merchant merchant = merchantStore.getMerchant();
		shopDetail.setMerchant(merchant != null ? JSON.toJSONString(merchant) : "");
		shopDetail.setClientId(merchant != null ? merchant.getId() : null);
		shopDetail.setClientName(merchant != null ? merchant.getName() : "");
		shopDetail.setOpeningTime(toHourString(merchantStore.getTimeOpen()) + "~" + toHourString(merchantStore.getTimeClose()));
		return shopDetail;
	}
}
