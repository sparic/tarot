package com.myee.tarot.configuration.type;

import com.myee.tarot.core.GenericEnumType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.*;

/**
 * Created by Ray.Fu on 2017/1/5.
 */
public class ReceiptPrintedItemQrSizeType implements GenericEnumType, Comparable<ReceiptPrintedItemQrSizeType>, Serializable {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReceiptPrintedItemQrSizeType.class);

    private static final Map<String, ReceiptPrintedItemQrSizeType> TYPES = new LinkedHashMap<String, ReceiptPrintedItemQrSizeType>();

    private static final ReceiptPrintedItemQrSizeType SIZE_1 = new ReceiptPrintedItemQrSizeType("1", "1");
    private static final ReceiptPrintedItemQrSizeType SIZE_2 = new ReceiptPrintedItemQrSizeType("2", "2");
    private static final ReceiptPrintedItemQrSizeType SIZE_3 = new ReceiptPrintedItemQrSizeType("3", "3");
    private static final ReceiptPrintedItemQrSizeType SIZE_4 = new ReceiptPrintedItemQrSizeType("4", "4");
    private static final ReceiptPrintedItemQrSizeType SIZE_5 = new ReceiptPrintedItemQrSizeType("5", "5");
    private static final ReceiptPrintedItemQrSizeType SIZE_6 = new ReceiptPrintedItemQrSizeType("6", "6");
    private static final ReceiptPrintedItemQrSizeType SIZE_7 = new ReceiptPrintedItemQrSizeType("7", "7");
    private static final ReceiptPrintedItemQrSizeType SIZE_8 = new ReceiptPrintedItemQrSizeType("8", "8");
    private static final ReceiptPrintedItemQrSizeType SIZE_9 = new ReceiptPrintedItemQrSizeType("9", "9");
    private static final ReceiptPrintedItemQrSizeType SIZE_10 = new ReceiptPrintedItemQrSizeType("10", "10");
    private static final ReceiptPrintedItemQrSizeType SIZE_11 = new ReceiptPrintedItemQrSizeType("11", "11");
    private static final ReceiptPrintedItemQrSizeType SIZE_12 = new ReceiptPrintedItemQrSizeType("12", "12");
    private static final ReceiptPrintedItemQrSizeType SIZE_13 = new ReceiptPrintedItemQrSizeType("13", "13");
    private static final ReceiptPrintedItemQrSizeType SIZE_14 = new ReceiptPrintedItemQrSizeType("14", "14");
    private static final ReceiptPrintedItemQrSizeType SIZE_15 = new ReceiptPrintedItemQrSizeType("15", "15");
    private static final ReceiptPrintedItemQrSizeType SIZE_16 = new ReceiptPrintedItemQrSizeType("16", "16");
    private static final ReceiptPrintedItemQrSizeType SIZE_17 = new ReceiptPrintedItemQrSizeType("17", "17");
    private static final ReceiptPrintedItemQrSizeType SIZE_18 = new ReceiptPrintedItemQrSizeType("18", "18");
    private static final ReceiptPrintedItemQrSizeType SIZE_19 = new ReceiptPrintedItemQrSizeType("19", "19");
    private static final ReceiptPrintedItemQrSizeType SIZE_20 = new ReceiptPrintedItemQrSizeType("20", "20");

    private String type;
    private String friendlyType;

    public ReceiptPrintedItemQrSizeType() {

    }

    public ReceiptPrintedItemQrSizeType(String type, String friendlyType) {
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
    public int compareTo(ReceiptPrintedItemQrSizeType o) {
        return 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ReceiptPrintedItemQrSizeType that = (ReceiptPrintedItemQrSizeType) o;

        return !(type != null ? !type.equals(that.type) : that.type != null);

    }

    @Override
    public int hashCode() {
        return type != null ? type.hashCode() : 0;
    }

    public List getReceiptPrintedItemQrSizeType4Select() {
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

    public String getReceiptPrintedItemQrSizeTypeName(String receiptPrintedItemQrSizeType) {
        try {
            String key = String.valueOf((TYPES.get(receiptPrintedItemQrSizeType)).getFriendlyType());
            return key == null || key.equals("null") ? "" : key;
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return "";
        }
    }
}
