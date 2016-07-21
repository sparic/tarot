package com.myee.tarot.log.service;

import com.myee.tarot.core.service.GenericEntityService;
import com.myee.tarot.core.util.PageResult;
import com.myee.tarot.core.util.WhereRequest;
import com.myee.tarot.weixin.domain.WxWaitToken;
import com.myee.tarot.weixin.domain.WxWaitTokenState;

import java.util.List;

/**
 * Created by Jelynn on 2016/7/20.
 */
public interface WaitTokenService extends GenericEntityService<Long, WxWaitToken> {

    PageResult<WxWaitToken> pageList(WhereRequest whereRequest);
}