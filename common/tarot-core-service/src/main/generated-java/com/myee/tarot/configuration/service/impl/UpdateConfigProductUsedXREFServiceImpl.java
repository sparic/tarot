package com.myee.tarot.configuration.service.impl;

import com.myee.tarot.configuration.domain.UpdateConfig;
import com.myee.tarot.core.service.GenericEntityServiceImpl;
import com.myee.tarot.configuration.dao.UpdateConfigProductUsedXREFDao;
import com.myee.tarot.configuration.domain.UpdateConfigProductUsedXREF;
import com.myee.tarot.configuration.service.UpdateConfigProductUsedXREFService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Project Name : tarot
 * User: Jelynn
 * Date: 2016/12/19
 * Time: 16:07
 * Describe:
 * Version:1.0
 */
@Service
public class UpdateConfigProductUsedXREFServiceImpl extends GenericEntityServiceImpl<Long, UpdateConfigProductUsedXREF> implements UpdateConfigProductUsedXREFService {

	private UpdateConfigProductUsedXREFDao updateConfigProductUsedXREFDao;

	@Autowired
	public UpdateConfigProductUsedXREFServiceImpl(UpdateConfigProductUsedXREFDao updateConfigProductUsedXREFDao) {
		super(updateConfigProductUsedXREFDao);
		this.updateConfigProductUsedXREFDao = updateConfigProductUsedXREFDao;
	}

	public UpdateConfigProductUsedXREF getByTypeAndDeviceGroupNO(String type, String deviceGroupNO) {
		return updateConfigProductUsedXREFDao.getByTypeAndDeviceGroupNO(type, deviceGroupNO);
	}

	public List<UpdateConfigProductUsedXREF> listByTypeAndDeviceGroupNO(String type, String deviceGroupNO) {
		return updateConfigProductUsedXREFDao.listByTypeAndDeviceGroupNO(type,deviceGroupNO);
	}

	public List<UpdateConfigProductUsedXREF> listByConfigId(Long configId) {
		return updateConfigProductUsedXREFDao.listByConfigId(configId);
	}

	public void deleteByConfigAndDeviceGroupNO(UpdateConfig updateConfig, String deviceGroupNO) {
		updateConfigProductUsedXREFDao.deleteByConfigAndDeviceGroupNO(updateConfig, deviceGroupNO);
	}
}
