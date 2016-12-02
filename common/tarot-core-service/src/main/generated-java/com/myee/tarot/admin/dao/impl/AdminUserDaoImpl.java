package com.myee.tarot.admin.dao.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.myee.tarot.admin.dao.AdminUserDao;
import com.myee.tarot.admin.domain.AdminUser;
import com.myee.tarot.admin.domain.QAdminUser;
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
import java.util.Set;

/**
 * Created by Martin on 2016/4/21.
 */
@Repository
public class AdminUserDaoImpl extends GenericEntityDaoImpl<Long, AdminUser> implements AdminUserDao {

    @Override
    public AdminUser getByUserName(String userName) {
        QAdminUser qUser = QAdminUser.adminUser;

        JPQLQuery<AdminUser> query = new JPAQuery(getEntityManager());

        query.from(qUser)
                .leftJoin(qUser.merchantStore)
                .fetchJoin();
        query.where(qUser.name.eq(userName));

        AdminUser user = query.fetchFirst();

        return user;
    }

    @Override
    public List<AdminUser> listByEmail(String emailAddress) {
        return null;
    }

    @Override
    public List<AdminUser> listByIds(Set<Long> ids) {
        return null;
    }

    @Override
    public AdminUser getByLogin(String login) {
        QAdminUser qUser = QAdminUser.adminUser;

        JPQLQuery<AdminUser> query = new JPAQuery(getEntityManager());

        query.from(qUser)
                .leftJoin(qUser.merchantStore)
                .fetchJoin();
        query.where(qUser.login.eq(login));

        AdminUser user = query.fetchFirst();

        return user;
    }

    @Override
    public PageResult<AdminUser> pageList(WhereRequest whereRequest){
        PageResult<AdminUser> pageList = new PageResult<AdminUser>();
        QAdminUser qAdminUser = QAdminUser.adminUser;
        JPQLQuery<AdminUser> query = new JPAQuery(getEntityManager());
        query.from(qAdminUser);
        if (whereRequest.getQueryObj() != null) {
            JSONObject map = JSON.parseObject(whereRequest.getQueryObj());
            if (map.get(Constants.SEARCH_OPTION_NAME) != null && !StringUtil.isBlank(map.get(Constants.SEARCH_OPTION_NAME).toString())) {
                query.where(qAdminUser.name.like("%" + map.get(Constants.SEARCH_OPTION_NAME) + "%").or(qAdminUser.login.like("%" + map.get(Constants.SEARCH_OPTION_NAME) + "%")));
            }
            if (map.get(Constants.SEARCH_OPTION_LOGIN) != null && !StringUtil.isBlank(map.get(Constants.SEARCH_OPTION_LOGIN).toString())) {
                query.where(qAdminUser.login.like("%" + map.get(Constants.SEARCH_OPTION_LOGIN) + "%"));
            }
            if (map.get(Constants.SEARCH_OPTION_PHONE_NUMBER) != null && !StringUtil.isBlank(map.get(Constants.SEARCH_OPTION_PHONE_NUMBER).toString())) {
                query.where(qAdminUser.phoneNumber.like("%" + map.get(Constants.SEARCH_OPTION_PHONE_NUMBER) + "%"));
            }
            if (map.get(Constants.SEARCH_OPTION_EMAIL) != null && !StringUtil.isBlank(map.get(Constants.SEARCH_OPTION_EMAIL).toString())) {
                query.where(qAdminUser.email.like("%" + map.get(Constants.SEARCH_OPTION_EMAIL) + "%"));
            }
        } else if(!StringUtil.isBlank(whereRequest.getQueryName())){
            query.where(qAdminUser.name.like("%" + whereRequest.getQueryName() + "%")
                    .or(qAdminUser.login.like("%" + whereRequest.getQueryName() + "%")));
        }
        pageList.setRecordsTotal(query.fetchCount());
        query.orderBy(qAdminUser.login.asc());
        if( whereRequest.getCount() > Constants.COUNT_PAGING_MARK){
            query.offset(whereRequest.getOffset()).limit(whereRequest.getCount());
        }
        pageList.setList(query.fetch());
        return pageList;
    }
}
