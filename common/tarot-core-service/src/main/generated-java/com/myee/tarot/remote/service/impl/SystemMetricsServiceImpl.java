package com.myee.tarot.remote.service.impl;

import com.myee.tarot.core.service.GenericEntityServiceImpl;
import com.myee.tarot.metric.domain.SystemMetrics;
import com.myee.tarot.remote.dao.SystemMetricsDao;
import com.myee.tarot.remote.service.SystemMetricsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * Created by Chay on 2016/8/10.
 */
@Service
public class SystemMetricsServiceImpl extends GenericEntityServiceImpl<Long, SystemMetrics> implements SystemMetricsService {

    protected SystemMetricsDao systemMetricsDao;

    @Autowired
    public SystemMetricsServiceImpl(SystemMetricsDao systemMetricsDao) {
        super(systemMetricsDao);
        this.systemMetricsDao = systemMetricsDao;
    }

    @Override
    public SystemMetrics getLatestByBoardNo(String boardNo, String nodeName, Long period, Long now) {
        return systemMetricsDao.getLatestByBoardNo(boardNo, nodeName,period,now);
    }

    @Override
    public List<SystemMetrics> listByBoardNoPeriod(String boardNo, Long now, Long period, String nodeName){
        return systemMetricsDao.listByBoardNoPeriod(boardNo, now, period, nodeName);
    }

    @Override
    public SystemMetrics getByBoardNoLogTimeNod(String boardNo, long logTime, String node) {
        return systemMetricsDao.getByBoardNoLogTimeNod(boardNo,  logTime,  node);
    }

    @Override
    public List<SystemMetrics> listByCreateTime(Date date) {
        return systemMetricsDao.listByCreateTime(date);
    }

    @Override
    public void deleteByTime(Date date) {
        systemMetricsDao.deleteByTime(date);
    }
}
