package com.myee.tarot.configuration.dao;

import com.myee.tarot.catalog.domain.ProductUsed;
import com.myee.tarot.configuration.domain.ReceiptPrinted;
import com.myee.tarot.core.dao.GenericEntityDao;
import com.myee.tarot.core.util.PageResult;
import com.myee.tarot.core.util.WhereRequest;

import java.util.List;

public interface ReceiptPrintedDao extends GenericEntityDao<Long, ReceiptPrinted> {

    PageResult<ReceiptPrinted> listByProductUsed(List<ProductUsed> productUsedList);

    PageResult<ReceiptPrinted> listByMerchantStoreId(WhereRequest whereRequest, Long id);
}