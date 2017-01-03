package com.myee.tarot.configuration.service.impl;

import com.myee.tarot.configuration.dao.BranchConfigDao;
import com.myee.tarot.configuration.domain.BranchConfig;
import com.myee.tarot.configuration.service.BranchConfigService;
import com.myee.tarot.core.service.GenericEntityServiceImpl;
import com.myee.tarot.core.util.PageResult;
import com.myee.tarot.core.util.WhereRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;

/**
 * Created by Chay on 2017/1/3.
 */
@Service
public class BranchConfigServiceImpl extends GenericEntityServiceImpl<Long, BranchConfig> implements BranchConfigService {

    protected BranchConfigDao branchConfigDao;

    @Autowired
    public BranchConfigServiceImpl(BranchConfigDao branchConfigDao) {
        super(branchConfigDao);
        this.branchConfigDao = branchConfigDao;
    }

    @Override
    public PageResult<BranchConfig> page(WhereRequest pageRequest) throws ParseException{
        return branchConfigDao.page(pageRequest);
    }

    @Override
    public BranchConfig findByName(String branchName, String branchSubName) {
        return branchConfigDao.findByName( branchName,  branchSubName);
    }

}
