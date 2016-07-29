package com.myee.tarot.datacenter.dao.impl;

import com.myee.tarot.core.dao.GenericEntityDaoImpl;
import com.myee.tarot.datacenter.dao.ModuleLogDao;
import com.myee.tarot.datacenter.domain.EventModule;
import com.myee.tarot.datacenter.domain.QEventModule;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQuery;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by Ray.Fu on 2016/7/19.
 */
@Repository
public class ModuleLogDaoImpl extends GenericEntityDaoImpl<Long, EventModule> implements ModuleLogDao {

    @Override
    public List getModuleList() {
        QEventModule qEventModule = QEventModule.eventModule;
        JPQLQuery<EventModule> query = new JPAQuery(getEntityManager());
        query.from(qEventModule).groupBy(qEventModule.moduleId);
        return query.fetch();
    }

    @Override
    public List<EventModule> getFunctionListByModule(Integer moduleId) {
        QEventModule qEventModule = QEventModule.eventModule;
        JPQLQuery<EventModule> query = new JPAQuery(getEntityManager());
        query.from(qEventModule);
        if(moduleId != null) {
            query.where(qEventModule.moduleId.eq(moduleId));
        }
        return query.fetch();
    }
}
