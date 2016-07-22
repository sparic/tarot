package com.myee.tarot.campaign.service;

import com.myee.tarot.core.service.GenericEntityService;
import com.myee.tarot.campaign.domain.PriceInfo;
import com.myee.tarot.core.util.PageRequest;
import com.myee.tarot.core.util.PageResult;

import java.util.List;

/**
 * Created by Administrator on 2016/7/14.
 */
public interface PriceInfoService extends GenericEntityService<Long ,PriceInfo>{

    List<PriceInfo> findByStatusAndKeyId(Long keyId,int status);

    PageResult<PriceInfo> pageList(Long storeId,PageRequest pageRequest);

    PriceInfo priceCheckCode(Long storeId,String checkCode);
}
