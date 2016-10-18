package com.myee.tarot.remote.util;

import com.myee.djinn.dto.metrics.AppInfo;
import com.myee.tarot.catalog.domain.DeviceUsed;
import com.myee.tarot.catalog.service.DeviceUsedService;
import com.myee.tarot.core.util.DateTimeUtils;
import com.myee.tarot.core.util.StringUtil;
import com.myee.tarot.metric.domain.MetricInfo;
import com.myee.tarot.metric.domain.MetricDetail;
import com.myee.tarot.metric.domain.SystemMetrics;
import com.myee.tarot.remote.service.AppInfoService;
import com.myee.tarot.remote.service.MetricDetailService;
import com.myee.tarot.remote.service.MetricInfoService;
import com.myee.tarot.remote.service.SystemMetricsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by chay on 2016/10/13.
 */
public class MetricsUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(MetricsUtil.class);

    /**
     * 把监控指标批量写入数据库
     *
     * @param list
     * @param deviceUsedService
     * @param systemMetricsService
     * @return
     */
    public static boolean updateSystemMetrics(List<com.myee.djinn.dto.metrics.SystemMetrics> list,
                                              DeviceUsedService deviceUsedService,
                                              AppInfoService appInfoService,
                                              MetricInfoService metricInfoService,
                                              MetricDetailService metricDetailService,
                                              SystemMetricsService systemMetricsService) {
        try {
            if (list == null || list.size() == 0) {
                return false;
            }
            Date now = new Date();
            for (com.myee.djinn.dto.metrics.SystemMetrics systemMetrics : list) {
                if (systemMetrics.getBoardNo() == null || StringUtil.isBlank(systemMetrics.getBoardNo())) {
                    continue;
                }
                DeviceUsed deviceUsed = deviceUsedService.getByBoardNo(systemMetrics.getBoardNo());
                if (deviceUsed == null) {
                    continue;
                }
                SystemMetrics systemMetricsDB = new SystemMetrics();
                systemMetricsDB.setDeviceUsed(deviceUsed);
                systemMetricsDB.setCreated(now);

                systemMetricsDB.setNode(systemMetrics.getNode());
                systemMetricsDB.setLogTime(DateTimeUtils.toMillis(systemMetrics.getLogTime()));

                systemMetricsDB = systemMetricsService.update(systemMetricsDB);

                systemMetricsDB.setAppList(transformAppInfo(systemMetricsDB, appInfoService, systemMetrics, deviceUsed, now));
                systemMetricsDB.setMetricInfoList(transformMetricsInfo(systemMetricsDB, metricInfoService, metricDetailService, systemMetrics, deviceUsed, now));


            }
            return true;
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return false;
        }
    }

    /**
     * 把agent传来的MetricsInfo转化成我们web端使用的类
     *
     * @param systemMetricsDB
     * @param metricInfoService
     * @param metricDetailService
     * @param systemMetrics
     * @param deviceUsed
     * @param now    @return
     */
    private static List<MetricInfo> transformMetricsInfo(SystemMetrics systemMetricsDB,
                                                         MetricInfoService metricInfoService,
                                                         MetricDetailService metricDetailService,
                                                         com.myee.djinn.dto.metrics.SystemMetrics systemMetrics,
                                                         DeviceUsed deviceUsed,
                                                         Date now) {
        List<com.myee.djinn.dto.metrics.MetricInfo> metricInfoList = systemMetrics.getMetricInfoList();
        if (metricInfoList == null || metricInfoList.size() == 0) {
            return null;
        }
        List<MetricInfo> result = new ArrayList<MetricInfo>();
        for (com.myee.djinn.dto.metrics.MetricInfo metricInfo : metricInfoList) {
            MetricInfo metricInfoDB = new MetricInfo();
            MetricDetail metricDetail = metricDetailService.findByKey(metricInfo.getName());
            if(metricDetail == null ){
                continue;
            }
            metricInfoDB.setMetricDetail(metricDetail);
            metricInfoDB.setCreated(now);
            metricInfoDB.setDeviceUsed(deviceUsed);
            metricInfoDB.setDescription(metricInfo.getDescription());
            metricInfoDB.setLogTime(DateTimeUtils.toMillis(systemMetrics.getLogTime()));
            metricInfoDB.setNode(metricInfo.getNode());
            metricInfoDB.setSystemMetrics(systemMetricsDB);
            metricInfoDB.setValue(metricInfo.getValue());
            metricInfoDB = metricInfoService.update(metricInfoDB);
            result.add(metricInfoDB);
        }
        return result;
    }

    /**
     * 把agent传来的AppInfo转化成我们web端使用的类
     *
     * @param systemMetricsDB
     * @param appInfoService
     * @param systemMetrics
     * @param deviceUsed
     * @param now
     * @return
     */
    private static List<com.myee.tarot.metric.domain.AppInfo> transformAppInfo(SystemMetrics systemMetricsDB,
                                                                               AppInfoService appInfoService,
                                                                               com.myee.djinn.dto.metrics.SystemMetrics systemMetrics,
                                                                               DeviceUsed deviceUsed,
                                                                               Date now) {
        List<AppInfo> appLists = systemMetrics.getAppLists();
        if (appLists == null || appLists.size() == 0) {
            return null;
        }
        List<com.myee.tarot.metric.domain.AppInfo> result = new ArrayList<com.myee.tarot.metric.domain.AppInfo>();
        for (AppInfo appInfo : appLists) {
            com.myee.tarot.metric.domain.AppInfo appInfoDB = new com.myee.tarot.metric.domain.AppInfo();
            appInfoDB.setCreated(now);
            appInfoDB.setDeviceUsed(deviceUsed);
            appInfoDB.setInstallDate(DateTimeUtils.toMillis(appInfo.getInstallDate()));
            appInfoDB.setLogTime(DateTimeUtils.toMillis(systemMetrics.getLogTime()));
            appInfoDB.setState(appInfo.getState());
            appInfoDB.setType(appInfo.getType());
            appInfoDB.setSystemMetrics(systemMetricsDB);
            appInfoDB.setVersionCode(appInfo.getVersionCode());
            appInfoDB.setVersionName(appInfo.getVersionName());
            appInfoDB = appInfoService.update(appInfoDB);
            result.add(appInfoDB);
        }
        return result;
    }

}
