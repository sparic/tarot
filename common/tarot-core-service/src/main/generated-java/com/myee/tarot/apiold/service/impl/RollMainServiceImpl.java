package com.myee.tarot.apiold.service.impl;

import com.myee.tarot.apiold.dao.RollMainDao;
import com.myee.tarot.apiold.domain.RollMain;
import com.myee.tarot.apiold.service.RollMainService;
import com.myee.tarot.core.service.GenericEntityServiceImpl;
import com.myee.tarot.core.util.PageRequest;
import com.myee.tarot.core.util.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * Created by Chay on 2016/8/10.
 */
@Service
public class RollMainServiceImpl extends GenericEntityServiceImpl<Long, RollMain> implements RollMainService {

    protected RollMainDao rollMainDao;

    @Autowired
    public RollMainServiceImpl(RollMainDao rollMainDao) {
        super(rollMainDao);
        this.rollMainDao = rollMainDao;
    }

    public List<RollMain> listByTypeStoreTime(Long storeId, int type,Date now){
        return rollMainDao.listByTypeStoreTime(storeId, type,now);
    }

    public Long countByStore(Long id){
        return rollMainDao.countByStore(id);
    }

    public PageResult<RollMain> pageByTypeStore(PageRequest pageRequest, Integer type, Long storeId){
        return rollMainDao.pageByTypeStore( pageRequest,  type,  storeId);
    }
}
