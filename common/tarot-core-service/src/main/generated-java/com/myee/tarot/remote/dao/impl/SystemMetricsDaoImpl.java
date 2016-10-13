package com.myee.tarot.remote.dao.impl;

import com.myee.tarot.core.dao.GenericEntityDaoImpl;
import com.myee.tarot.metrics.domain.SystemMetrics;
import com.myee.tarot.remote.dao.SystemMetricsDao;
import org.springframework.stereotype.Repository;

/**
 * Created by Chay on 2016/8/10.
 */
@Repository
public class SystemMetricsDaoImpl extends GenericEntityDaoImpl<Long, SystemMetrics> implements SystemMetricsDao {
}
