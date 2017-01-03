package com.myee.tarot.configuration.dao.impl;

import com.myee.tarot.configuration.dao.MailContactDao;
import com.myee.tarot.configuration.domain.MailContact;
import com.myee.tarot.configuration.domain.QMailContact;
import com.myee.tarot.core.dao.GenericEntityDaoImpl;
import com.myee.tarot.core.util.PageResult;
import com.myee.tarot.core.util.WhereRequest;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQuery;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by Ray.Fu on 2016/12/30.
 */
@Repository
public class MailContactDaoImpl extends GenericEntityDaoImpl<Long, MailContact> implements MailContactDao {

    @Override
    public PageResult<MailContact> listAllContact(WhereRequest whereRequest) {
        PageResult<MailContact> pageList = new PageResult<MailContact>();
        QMailContact qMailContact = QMailContact.mailContact;
        JPQLQuery<MailContact> query = new JPAQuery(getEntityManager());
        query.from(qMailContact);
        if (whereRequest.getQueryName() != null) {
            query.where(qMailContact.name.like("%" + whereRequest.getQueryName() + "%"));
        }
        pageList.setRecordsTotal(query.fetchCount());
        pageList.setList(query.fetch());
        return pageList;
    }

    @Override
    public MailContact getByName(String name) {
        QMailContact qMailContact = QMailContact.mailContact;
        JPQLQuery<MailContact> query = new JPAQuery<MailContact>(getEntityManager());
        query.from(qMailContact);
        query.where(qMailContact.name.eq(name));

        MailContact mailContact = query.fetchFirst();
        return mailContact;
    }

}
