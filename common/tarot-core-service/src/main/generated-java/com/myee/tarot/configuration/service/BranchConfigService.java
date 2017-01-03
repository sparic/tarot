package com.myee.tarot.configuration.service;

import com.myee.tarot.configuration.domain.BranchConfig;
import com.myee.tarot.core.service.GenericEntityService;
import com.myee.tarot.core.util.PageResult;
import com.myee.tarot.core.util.WhereRequest;

import java.text.ParseException;

/**
 * Created by Chay on 2017/1/3.
 */
public interface BranchConfigService extends GenericEntityService<Long, BranchConfig> {

    PageResult<BranchConfig> page(WhereRequest pageRequest) throws ParseException;

    BranchConfig findByName(String branchName, String branchSubName);
}
