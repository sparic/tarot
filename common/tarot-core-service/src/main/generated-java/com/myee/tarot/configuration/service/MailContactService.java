package com.myee.tarot.configuration.service;

import com.myee.tarot.configuration.domain.MailContact;
import com.myee.tarot.core.service.GenericEntityService;
import com.myee.tarot.core.util.PageResult;
import com.myee.tarot.core.util.WhereRequest;

import java.util.List;

/**
 * Created by Ray.Fu on 2016/12/30.
 */
public interface MailContactService extends GenericEntityService<Long, MailContact> {

    PageResult<MailContact> listAllContact(WhereRequest whereRequest);

    MailContact getByName(String name);

}
