package com.myee.tarot.configuration.dao;

import com.myee.tarot.configuration.domain.BranchConfig;
import com.myee.tarot.core.dao.GenericEntityDao;
import com.myee.tarot.core.util.PageResult;
import com.myee.tarot.core.util.WhereRequest;

import java.text.ParseException;

/**
 * Created by Chay on 2017/1/3.
 */
public interface BranchConfigDao extends GenericEntityDao<Long, BranchConfig> {

    PageResult<BranchConfig> page(WhereRequest pageRequest) throws ParseException;

    BranchConfig findByName(String branchName, String branchSubName);
}
