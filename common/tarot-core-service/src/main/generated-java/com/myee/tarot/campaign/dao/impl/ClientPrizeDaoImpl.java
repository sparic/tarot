package com.myee.tarot.campaign.dao.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.myee.tarot.campaign.dao.ClientPrizeDao;
import com.myee.tarot.clientprize.domain.ClientPrize;
import com.myee.tarot.clientprize.domain.QClientPrize;
import com.myee.tarot.core.Constants;
import com.myee.tarot.core.dao.GenericEntityDaoImpl;
import com.myee.tarot.core.util.PageRequest;
import com.myee.tarot.core.util.PageResult;
import com.myee.tarot.core.util.StringUtil;
import com.myee.tarot.core.util.WhereRequest;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQuery;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by Administrator on 2016/8/29.
 */
@Repository
public class ClientPrizeDaoImpl extends GenericEntityDaoImpl<Long, ClientPrize> implements ClientPrizeDao{
    @Override
    public PageResult<ClientPrize> pageList(WhereRequest whereRequest, Long storeId) {
        PageResult<ClientPrize> pageList = new PageResult();
        QClientPrize qClientPrize = QClientPrize.clientPrize;
        JPQLQuery<ClientPrize> query = new JPAQuery(getEntityManager());
        query.from(qClientPrize).where(qClientPrize.deleteStatus.eq(Constants.CLIENT_PRIZE_DELETE_YES));
        if (whereRequest.getQueryObj() != null) {
            JSONObject map = JSON.parseObject(whereRequest.getQueryObj());
            Object optionName = map.get(Constants.SEARCH_OPTION_NAME);
            Object optionType = map.get(Constants.SEARCH_OPTION_TYPE);
            Object phonePrizeType = map.get(Constants.SEARCH_OPTION_PHONE_PRIZE_TYPE);
            if (optionName != null && !StringUtil.isBlank(optionName.toString())) {
                query.where(qClientPrize.name.like("%" + optionName + "%"));
            }
            if (optionType != null && !StringUtil.isBlank(optionType.toString())) {
                query.where(qClientPrize.type.eq(Integer.valueOf(optionType.toString())));
            }
            if (phonePrizeType != null && !StringUtil.isBlank(phonePrizeType.toString())) {
                query.where(qClientPrize.phonePrizeType.eq(Integer.valueOf(phonePrizeType.toString())));
            }
        } else {
            if(!StringUtil.isBlank(whereRequest.getQueryName())){
                query.where(qClientPrize.name.like("%" + whereRequest.getQueryName() + "%"));
            }
        }
        if(storeId != null){
            query.where(qClientPrize.store.id.eq(storeId));
        }
        pageList.setRecordsTotal(query.fetchCount());
        query.orderBy(qClientPrize.name.asc());
        if( whereRequest.getCount() > Constants.COUNT_PAGING_MARK){
            query.offset(whereRequest.getOffset()).limit(whereRequest.getCount());
        }
        pageList.setList(query.fetch());
        return pageList;
    }

    @Override
    public List<ClientPrize> listActive(Long storeId) {
        QClientPrize qClientPrize = QClientPrize.clientPrize;
        JPQLQuery<ClientPrize> query = new JPAQuery(getEntityManager());
        query.from(qClientPrize).where(qClientPrize.store.id.eq(storeId).and(qClientPrize.activeStatus.eq(Constants.CLIENT_PRIZE_ACTIVE_YES).and(qClientPrize.deleteStatus.eq(Constants.CLIENT_PRIZE_DELETE_YES))));
        return query.fetch();
    }

    @Override
    public List<ClientPrize> listActiveAndAboveZero(Long storeId) {
        QClientPrize qClientPrize = QClientPrize.clientPrize;
        JPQLQuery<ClientPrize> query = new JPAQuery(getEntityManager());
        query.from(qClientPrize).where(qClientPrize.store.id.eq(storeId).and(qClientPrize.activeStatus.eq(Constants.CLIENT_PRIZE_ACTIVE_YES).and(qClientPrize.leftNum.gt(0)).and(qClientPrize.deleteStatus.eq(Constants.CLIENT_PRIZE_DELETE_YES))));
        return query.fetch();
    }

    @Override
    public ClientPrize getThankYouPrize(Long storeId) {
        QClientPrize qClientPrize = QClientPrize.clientPrize;
        JPQLQuery<ClientPrize> query = new JPAQuery(getEntityManager());
        query.from(qClientPrize).where(qClientPrize.store.id.eq(storeId).and(qClientPrize.activeStatus.eq(Constants.CLIENT_PRIZE_ACTIVE_YES).and(qClientPrize.type.eq(Constants.CLIENT_PRIZE_TYPE_THANKYOU).and(qClientPrize.deleteStatus.eq(Constants.CLIENT_PRIZE_DELETE_YES)))));
        return query.fetchFirst();
    }

    @Override
    public List<ClientPrize> listAllActive() {
        QClientPrize qClientPrize = QClientPrize.clientPrize;
        JPQLQuery<ClientPrize> query = new JPAQuery(getEntityManager());
        query.from(qClientPrize).where(qClientPrize.activeStatus.eq(Constants.CLIENT_PRIZE_ACTIVE_YES).and(qClientPrize.deleteStatus.eq(Constants.CLIENT_PRIZE_DELETE_YES)));
        return query.fetch();
    }
}
