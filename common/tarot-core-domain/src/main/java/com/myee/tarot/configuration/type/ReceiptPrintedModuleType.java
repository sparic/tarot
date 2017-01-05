package com.myee.tarot.configuration.type;

import com.myee.tarot.core.GenericEnumType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.*;

/**
 * Created by Ray.Fu on 2017/1/5.
 */
public class ReceiptPrintedModuleType implements GenericEnumType, Comparable<ReceiptPrintedModuleType>, Serializable {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReceiptPrintedModuleType.class);

    private static final Map<String, ReceiptPrintedModuleType> TYPES = new LinkedHashMap<String, ReceiptPrintedModuleType>();

    private static final ReceiptPrintedModuleType LOTTERY = new ReceiptPrintedModuleType("LOTTERY", "抽奖");
    private static final ReceiptPrintedModuleType WAITTOKEN = new ReceiptPrintedModuleType("WAITTOKEN", "排号");

    private String type;
    private String friendlyType;

    public ReceiptPrintedModuleType() {

    }

    public ReceiptPrintedModuleType(String type, String friendlyType) {
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
    public int compareTo(ReceiptPrintedModuleType o) {
        return 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ReceiptPrintedModuleType that = (ReceiptPrintedModuleType) o;

        return !(type != null ? !type.equals(that.type) : that.type != null);

    }

    @Override
    public int hashCode() {
        return type != null ? type.hashCode() : 0;
    }

    public List getReceiptPrintedModuleType4Select() {
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

    public String getReceiptPrintedModuleTypeName(String receiptPrintedModuleType) {
        try {
            String key = String.valueOf((TYPES.get(receiptPrintedModuleType)).getFriendlyType());
            return key == null || key.equals("null") ? "" : key;
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return "";
        }
    }
}
