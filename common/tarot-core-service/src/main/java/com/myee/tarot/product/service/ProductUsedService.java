package com.myee.tarot.product.service;

import com.myee.tarot.catalog.domain.ProductUsed;
import com.myee.tarot.core.service.GenericEntityService;
import com.myee.tarot.core.util.PageRequest;
import com.myee.tarot.core.util.PageResult;

/**
 * Created by Enva on 2016/6/1.
 */
public interface ProductUsedService extends GenericEntityService<Long, ProductUsed> {

    public PageResult<ProductUsed> pageList(PageRequest pageRequest);

}
