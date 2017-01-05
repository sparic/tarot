package com.myee.tarot.configuration.type;

import com.myee.tarot.core.GenericEnumType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.*;

/**
 * Created by Ray.Fu on 2017/1/5.
 */
public class ReceiptPrintedItemSizeType implements GenericEnumType, Comparable<ReceiptPrintedItemSizeType>, Serializable {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReceiptPrintedItemSizeType.class);

    private static final Map<String, ReceiptPrintedItemSizeType> TYPES = new LinkedHashMap<String, ReceiptPrintedItemSizeType>();

    private static final ReceiptPrintedItemSizeType NORMAL = new ReceiptPrintedItemSizeType("NORMAL", "标准");
    private static final ReceiptPrintedItemSizeType DOUBLE = new ReceiptPrintedItemSizeType("DOUBLE", "两倍");
    private static final ReceiptPrintedItemSizeType TRIBLE = new ReceiptPrintedItemSizeType("TRIBLE", "三倍");

    private String type;
    private String friendlyType;

    public ReceiptPrintedItemSizeType() {

    }

    public ReceiptPrintedItemSizeType(String type, String friendlyType) {
        this.friendlyType = friendlyType;
        setType(type);
    }

    @Override
    public String getType() {
        return type;
    }

    public void setType(final String type) {
        this.type = type;
        if (!TYPES.containsKey(type)) {
            TYPES.put(type, this);
        }
    }

    @Override
    public String getFriendlyType() {
        return friendlyType;
    }

    @Override
    public int compareTo(ReceiptPrintedItemSizeType o) {
        return 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ReceiptPrintedItemSizeType that = (ReceiptPrintedItemSizeType) o;

        return !(type != null ? !type.equals(that.type) : that.type != null);

    }

    @Override
    public int hashCode() {
        return type != null ? type.hashCode() : 0;
    }

    public List getReceiptPrintedItemSizeType4Select() {
        List resp = new ArrayList();
        Set keySet = TYPES.keySet();
        for (Object keyName : keySet) {
            Map entry = new HashMap();
            entry.put("name", (TYPES.get(keyName)).getFriendlyType());
            entry.put("value", keyName);
            resp.add(entry);
        }
        return resp;
    }

    public String getReceiptPrintedItemSizeTypeName(String receiptPrintedItemSizeType) {
        try {
            String key = String.valueOf((TYPES.get(receiptPrintedItemSizeType)).getFriendlyType());
            return key == null || key.equals("null") ? "" : key;
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return "";
        }
    }
}
