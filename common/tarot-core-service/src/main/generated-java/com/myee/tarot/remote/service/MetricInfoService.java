package com.myee.tarot.remote.service;

import com.myee.tarot.core.service.GenericEntityService;
import com.myee.tarot.metric.domain.MetricInfo;

import java.util.Date;
import java.util.List;

/**
 * Created by Chay on 2016/8/10.
 */
public interface MetricInfoService extends GenericEntityService<Long, MetricInfo> {
    List<MetricInfo> listBySystemMetricsId(Long systemMetricsId, List<String> metricsKeyListToSearch);

    List<MetricInfo> listByBoardNoPeriod(String boardNo, Long now, Long period, String nodeName, List<String> metricsKeyList);

    List<MetricInfo> listMetricsInfoPointsByPeriod(List<String> metricsKeyString, Long period, String boardNo);

    void deleteByTime(Date date);

    List<MetricInfo> listByCreateTime(Date date);
}
