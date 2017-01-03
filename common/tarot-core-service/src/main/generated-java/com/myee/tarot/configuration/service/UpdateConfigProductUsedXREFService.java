package com.myee.tarot.configuration.service;

import com.myee.tarot.core.service.GenericEntityService;
import com.myee.tarot.configuration.domain.UpdateConfigProductUsedXREF;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Project Name : tarot
 * User: Jelynn
 * Date: 2016/12/19
 * Time: 16:06
 * Describe:
 * Version:1.0
 */
public interface UpdateConfigProductUsedXREFService extends GenericEntityService<Long, UpdateConfigProductUsedXREF> {

	UpdateConfigProductUsedXREF getByTypeAndDeviceGroupNO(String type, String deviceGroupNO);

	List<UpdateConfigProductUsedXREF> listByTypeAndDeviceGroupNO(String type, String deviceGroupNO);

	List<UpdateConfigProductUsedXREF> listByConfigId(Long configId);
}
