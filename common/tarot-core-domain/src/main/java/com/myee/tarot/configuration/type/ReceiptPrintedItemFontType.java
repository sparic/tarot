package com.myee.tarot.configuration.type;

import com.myee.tarot.core.GenericEnumType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.Serializable;
import java.util.*;

/**
 * Created by Ray.Fu on 2017/1/5.
 */
public class ReceiptPrintedItemFontType implements GenericEnumType, Comparable<ReceiptPrintedItemFontType>, Serializable {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReceiptPrintedItemFontType.class);

    private static final Map<String, ReceiptPrintedItemFontType> TYPES = new LinkedHashMap<String, ReceiptPrintedItemFontType>();

    private static final ReceiptPrintedItemFontType FONT_A = new ReceiptPrintedItemFontType("FONT_A", "FONT_A");
    private static final ReceiptPrintedItemFontType SONGTI = new ReceiptPrintedItemFontType("SONGTI", "宋体");

    private String type;
    private String friendlyType;

    public ReceiptPrintedItemFontType() {

    }

    public ReceiptPrintedItemFontType(String type, String friendlyType) {
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
    public int compareTo(ReceiptPrintedItemFontType o) {
        return 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ReceiptPrintedItemFontType that = (ReceiptPrintedItemFontType) o;

        return !(type != null ? !type.equals(that.type) : that.type != null);

    }

    @Override
    public int hashCode() {
        return type != null ? type.hashCode() : 0;
    }

    public List getReceiptPrintedItemFontType4Select() {
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

    public String getReceiptPrintedItemFontTypeName(String receiptPrintedItemFontType) {
        try {
            String key = String.valueOf((TYPES.get(receiptPrintedItemFontType)).getFriendlyType());
            return key == null || key.equals("null") ? "" : key;
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return "";
        }
    }
}
