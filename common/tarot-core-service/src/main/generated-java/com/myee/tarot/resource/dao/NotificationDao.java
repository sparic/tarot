package com.myee.tarot.resource.dao;

import com.myee.tarot.core.dao.GenericEntityDao;
import com.myee.tarot.core.util.PageRequest;
import com.myee.tarot.core.util.PageResult;
import com.myee.tarot.resource.domain.Notification;

/**
 * Created by Ray.Fu on 2016/8/10.
 */
public interface NotificationDao extends GenericEntityDao<Long, Notification> {
    PageResult<Notification> pageByStore(Long id, PageRequest pageRequest);
}
