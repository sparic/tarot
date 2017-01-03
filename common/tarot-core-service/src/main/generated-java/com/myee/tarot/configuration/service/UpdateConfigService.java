package com.myee.tarot.configuration.service;

import com.myee.tarot.core.service.GenericEntityService;
import com.myee.tarot.core.util.PageResult;
import com.myee.tarot.core.util.WhereRequest;
import com.myee.tarot.configuration.domain.UpdateConfig;

import java.text.ParseException;

/**
 * Created by Chay on 2016/12/15.
 */
public interface UpdateConfigService extends GenericEntityService<Long, UpdateConfig> {

    PageResult<UpdateConfig> page(WhereRequest pageRequest) throws ParseException;
}
