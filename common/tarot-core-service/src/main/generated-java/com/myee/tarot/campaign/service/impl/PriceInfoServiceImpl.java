package com.myee.tarot.campaign.service.impl;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.myee.tarot.campaign.dao.PriceInfoDao;
import com.myee.tarot.campaign.domain.MerchantActivity;
import com.myee.tarot.campaign.domain.MerchantPrice;
import com.myee.tarot.campaign.domain.PriceInfo;
import com.myee.tarot.campaign.service.MerchantActivityService;
import com.myee.tarot.campaign.service.ModeSwitchService;
import com.myee.tarot.campaign.service.PriceInfoService;
import com.myee.tarot.core.Constants;
import com.myee.tarot.core.exception.ServiceException;
import com.myee.tarot.core.service.GenericEntityServiceImpl;
import com.myee.tarot.core.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created by Administrator on 2016/7/14.
 */
@Service
public class PriceInfoServiceImpl extends GenericEntityServiceImpl<Long, PriceInfo> implements PriceInfoService {

    @Autowired
    private PriceInfoDao priceInfoDao;

    @Autowired
    private MerchantActivityService merchantActivityService;

    @Autowired
    private ModeSwitchService modeSwitchService;

    @Autowired
    private PriceInfoServiceImpl(PriceInfoDao priceInfoDao) {
        super(priceInfoDao);
        this.priceInfoDao = priceInfoDao;
    }

    @Override
    public List<PriceInfo> listByStatusAndKeyId(String keyId, int status) {
        return priceInfoDao.listByStatusAndKeyId(keyId, status);
    }

    @Override
    public PageResult<PriceInfo> pageList(Long storeId, PageRequest pageRequest) {
        return priceInfoDao.pageList(storeId, pageRequest);
    }

    @Override
    public PriceInfo priceCheckCode(Long storeId, String checkCode) {
        return priceInfoDao.priceCheckCode(storeId, checkCode);
    }

    @Override
    public boolean findByStoreIdAndKeyIdToday(Long storeId, String keyId) {
        List<PriceInfo> priceInfo = priceInfoDao.listByStoreIdAndKeyId(storeId, keyId);
        List<PriceInfo> onlyInfo = Lists.newArrayList();
        for (PriceInfo info : priceInfo) {
            Date getDate = info.getGetDate();
            boolean flag = DateUtil.whetherToday(getDate);
            if (flag) {
                onlyInfo.add(info);
            }
        }
        return onlyInfo != null && onlyInfo.size() == 1 ? false : true;
    }

    @Override
    public Map<String,Object> savePriceInfo(String keyId, Long storeId) throws ServiceException {
        Map<String,Object> mapInfo = new HashMap<String,Object>();
        //在抽奖前需要查看微信抽奖模式 是否开启
       /* ModeSwitch modeSwitch = modeSwitchService.findByStoreId(storeId);
        if(modeSwitch==null||modeSwitch.getStatus() == Constants.WECHAT_CLOSE){
            mapInfo.put("errorString", "商户微信模式未开启");
            mapInfo.put("status", -1);
            return mapInfo;
        }
        //通过keyId和storeId查看此人是否今天已经抽取过奖券
        boolean canDraw = findByStoreIdAndKeyIdToday(storeId,keyId);
        if(!canDraw){
            mapInfo.put("errorString", "你今天已抽取过，请明天再来抽取");
            mapInfo.put("status", -1);
            return mapInfo;
        }*/
        PriceInfo priceInfo = new PriceInfo();
        priceInfo.setKeyId(keyId);
        priceInfo.setCheckCode(AutoNumUtil.getCode(6, 3).toUpperCase()); //设置6位随机数
        priceInfo.setStatus(Constants.PRICEINFO_UNUSED);
        priceInfo.setGetDate(new Date());
        MerchantActivity activity = merchantActivityService.findStoreActivity(storeId);
        //改从数据库直接获取
        if (activity != null) {
            List<Integer> priceList = JSON.parseArray(activity.getPriceList(), Integer.class);
            if (priceList == null || priceList.size() == 0) {
                // MerchantActivity merchantActivity = merchantActivityService.findStoreActivity(storeId);
                if (activity.getActivityStatus() != Constants.ACITIVITY_END) {
                    activity.setActivityStatus(Constants.ACITIVITY_END);
                    merchantActivityService.update(activity);
                }
                mapInfo.put("errorString","活动已结束");
                mapInfo.put("status", -1);
                return mapInfo;
            }
            PriceInfo info = getRandomPrice(priceInfo, priceList, activity.getPrices());
            //更新数据库List
            activity.setPriceList(JSON.toJSONString(priceList));
            merchantActivityService.update(activity);
            super.save(priceInfo);
            PriceInfo result = super.findById(priceInfo.getId());
            mapInfo.put("status",0);
            mapInfo.put("result",result);
            return mapInfo;
        } else {
            mapInfo.put("status",-1);
            mapInfo.put("errorString","该商户暂无奖券");
            return mapInfo;
        }
    }

    @Override
    public void updateRedisDrawList() throws ServiceException {
        List<MerchantActivity> activeActivities = merchantActivityService.findActiveActivity();
        for (MerchantActivity activeActivity : activeActivities) {
            List<MerchantPrice> activePrices = Lists.newArrayList();
            List<MerchantPrice> prices = activeActivity.getPrices();
            boolean isOverdue = false;
            for (MerchantPrice price : prices) {
                // 奖券过期判断
                if (price.getActiveStatus() == Constants.PRICE_START) {
                    activePrices.add(price);
                    //激活的奖券判断是否过期
                    Date endDate = price.getEndDate();
                    Date startToday = DateTimeUtils.startToday();
                    if (endDate.compareTo(startToday) < 0) {
                        price.setActiveStatus(Constants.PRICE_END);
                        isOverdue = true;
                    }
                }
            }
            if(isOverdue){
                for (MerchantPrice activePrice : activePrices) {
                    activePrice.setActiveStatus(Constants.PRICE_END);
                }
                activeActivity.setActivityStatus(Constants.ACITIVITY_START);
                activeActivity.setPriceList(null);
            }else{
                List<Integer> priceList = getPriceCountList(activePrices);
                activeActivity.setActivityStatus(Constants.ACTIVITY_ACTIVE);
                activeActivity.setPriceList(JSON.toJSONString(priceList));
            }
            merchantActivityService.update(activeActivity);
            //不再使用redis
            /*if(priceList.size()==0){
                redisUtil.delete(Constants.PRICEDRAW + "_" + activeActivity.getStore().getId());
            }else {
                redisUtil.set(Constants.PRICEDRAW + "_" + activeActivity.getStore().getId(),priceList,365,TimeUnit.DAYS);
            }*/
        }
    }

    @Override
    public PriceInfo findByIdAndKeyId(Long id, String keyId) {
        return priceInfoDao.findByIdAndKeyId(id, keyId);
    }

    public PriceInfo getRandomPrice(PriceInfo basePriceInfo, List<Integer> priceList, List<MerchantPrice> prices) {
        Random random = new Random();
        int index = random.nextInt(priceList.size());
        int priceInt = priceList.get(index);
        List<MerchantPrice> activePrices = Lists.newArrayList();
        for (MerchantPrice price : prices) {
            if (price.getActiveStatus() == Constants.PRICE_START) {
                activePrices.add(price);
            }
        }
        int compare = 0;
        for (MerchantPrice activePrice : activePrices) {
            compare += activePrice.getTotal();
            if (priceInt <= compare) {
                MerchantPrice getPrice = activePrice;
                basePriceInfo.setPriceName(getPrice.getName());
                basePriceInfo.setPriceLogo(getPrice.getLogoUrl()== null ? "": getPrice.getLogoUrl());
                basePriceInfo.setPriceDescription(getPrice.getDescription());
                basePriceInfo.setPriceStartDate(getPrice.getStartDate());
                basePriceInfo.setPriceEndDate(getPrice.getEndDate());
                basePriceInfo.setPrice(getPrice);
                break;
            }
        }
        priceList.remove(index);
        return basePriceInfo;
    }

    //重新分配奖券list
    public List<Integer> getPriceCountList(List<MerchantPrice> prices) {
        ;
        List<Integer> priceList = Lists.newArrayList();
        int totalAll = 0;
        for (MerchantPrice price : prices) {
            int total = price.getTotal();
            totalAll += total;
        }
        for (int i = 1; i <= totalAll; i++) {
            priceList.add(i);
        }
        return priceList;
    }


}
