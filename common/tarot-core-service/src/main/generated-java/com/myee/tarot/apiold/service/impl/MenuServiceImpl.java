package com.myee.tarot.apiold.service.impl;

import com.myee.tarot.apiold.dao.MenuDao;
import com.myee.tarot.apiold.domain.MenuInfo;
import com.myee.tarot.apiold.service.MenuService;
import com.myee.tarot.apiold.view.MenuInfoView;
import com.myee.tarot.core.service.GenericEntityServiceImpl;
import com.myee.tarot.core.util.PageRequest;
import com.myee.tarot.core.util.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by Chay on 2016/8/10.
 */
@Service
public class MenuServiceImpl extends GenericEntityServiceImpl<Long, MenuInfo> implements MenuService {

    protected MenuDao menuDao;

    @Autowired
    public MenuServiceImpl(MenuDao menuDao) {
        super(menuDao);
        this.menuDao = menuDao;
    }

    public List<MenuInfo> listByStoreId(long id){
        return menuDao.listByStoreId(id);
    }

    public PageResult<MenuInfo> pageByStore(Long id, PageRequest pageRequest){
        return menuDao.pageByStore(id, pageRequest);
    }
}
