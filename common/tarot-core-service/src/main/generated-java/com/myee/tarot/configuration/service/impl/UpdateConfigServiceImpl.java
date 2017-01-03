package com.myee.tarot.configuration.service.impl;

import com.myee.tarot.core.service.GenericEntityServiceImpl;
import com.myee.tarot.core.util.PageResult;
import com.myee.tarot.core.util.WhereRequest;
import com.myee.tarot.configuration.dao.UpdateConfigDao;
import com.myee.tarot.configuration.domain.UpdateConfig;
import com.myee.tarot.configuration.service.UpdateConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;

/**
 * Created by Chay on 2016/12/15.
 */
@Service
public class UpdateConfigServiceImpl extends GenericEntityServiceImpl<Long, UpdateConfig> implements UpdateConfigService {

    protected UpdateConfigDao updateConfigDao;

    @Autowired
    public UpdateConfigServiceImpl(UpdateConfigDao updateConfigDao) {
        super(updateConfigDao);
        this.updateConfigDao = updateConfigDao;
    }

    @Override
    public PageResult<UpdateConfig> page(WhereRequest pageRequest) throws ParseException{
        return updateConfigDao.page(pageRequest);
    }

}
