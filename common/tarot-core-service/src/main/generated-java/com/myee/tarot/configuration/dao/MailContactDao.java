package com.myee.tarot.configuration.dao;

import com.myee.tarot.configuration.domain.MailContact;
import com.myee.tarot.core.dao.GenericEntityDao;
import com.myee.tarot.core.util.PageResult;
import com.myee.tarot.core.util.WhereRequest;

import java.util.List;

/**
 * Created by Ray.Fu on 2016/12/30.
 */
public interface MailContactDao extends GenericEntityDao<Long, MailContact> {

    PageResult<MailContact> listAllContact(WhereRequest whereRequest);

    MailContact getByName(String name);

}
