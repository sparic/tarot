package com.myee.tarot.configuration.service.impl;

import com.myee.tarot.configuration.dao.MailContactDao;
import com.myee.tarot.configuration.domain.MailContact;
import com.myee.tarot.configuration.service.MailContactService;
import com.myee.tarot.core.service.GenericEntityServiceImpl;
import com.myee.tarot.core.util.PageResult;
import com.myee.tarot.core.util.WhereRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by Ray.Fu on 2016/12/30.
 */
@Service
public class MailContactServiceImpl extends GenericEntityServiceImpl<Long, MailContact> implements MailContactService {

    protected MailContactDao mailContactDao;

    @Autowired
    public MailContactServiceImpl(MailContactDao mailContactDao) {
        super(mailContactDao);
        this.mailContactDao = mailContactDao;
    }


    @Override
    public PageResult<MailContact> listAllContact(WhereRequest whereRequest) {
        return mailContactDao.listAllContact(whereRequest);
    }

    @Override
    public MailContact getByName(String name) {
        return mailContactDao.getByName(name);
    }

}
