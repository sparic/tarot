package com.myee.tarot.admin.service;

import com.myee.tarot.core.service.GenericEntityService;
import com.myee.tarot.profile.domain.Role;

import java.util.List;

/**
 * Created by Martin on 2016/4/11.
 */
public interface RoleService extends GenericEntityService<Long, Role> {

    List<Role> listAll();
}
