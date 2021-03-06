package com.myee.tarot.configuration.domain;

import com.myee.tarot.core.GenericEntity;
import javax.persistence.*;

/**
 * Created by Ray.Fu on 2016/12/19.
 */
@Entity
@Table(name = "C_RECEIPT_PRINTED_ITEM")
public class ReceiptPrintedItem extends GenericEntity<Long, ReceiptPrintedItem> {

    @Id
    @Column(name = "RECEIPT_ITEM_ID", unique = true, nullable = false)
    @TableGenerator(name = "TABLE_GEN", table = "C_SEQUENCER", pkColumnName = "SEQ_NAME", valueColumnName = "SEQ_COUNT", pkColumnValue = "RECEIPT_PRINTED_ITEM_SEQ_NEXT_VAL",allocationSize=1)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "TABLE_GEN")
    protected Long id;

    @Column(name= "ITEM_TYPE")
    private String itemType; //栏目类型(变量，常量)

    @Column(name= "IS_NEWLINE")
    private boolean isNewline; //是否换行 0为换行 1为不换行

    @Column(name= "CONTENT")
    private String content; //内容

    @Column(name= "FONT")
    private String font; //字体

    @Column(name= "IS_BOLD")
    private boolean isBold; //是否粗体

    @Column(name= "SIZE")
    private String size; //大小

    @Column(name= "QRSIZE")
    private String qrsize; //二维码大小

    @Column(name= "ALIGN")
    private String align; //对齐方式(1.左对齐，2，居中，3.右对齐)

    @Column(name= "IS_UNDERLINE")
    private boolean isUnderline; //是否有下划线

    @ManyToOne(targetEntity = ReceiptPrinted.class, cascade = { CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH }, fetch = FetchType.LAZY)
    @JoinColumn(name = "RECEIPT_ID")
    @org.hibernate.annotations.Index(name = "RECEIPT_ITEM_INDEX", columnNames = {"RECEIPT_ID"})
    protected ReceiptPrinted receiptPrinted;

    public String getItemType() {
        return itemType;
    }

    public void setItemType(String itemType) {
        this.itemType = itemType;
    }

    public boolean isNewline() {
        return isNewline;
    }

    public void setNewline(boolean newline) {
        isNewline = newline;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getFont() {
        return font;
    }

    public void setFont(String font) {
        this.font = font;
    }

    public boolean isBold() {
        return isBold;
    }

    public void setBold(boolean bold) {
        isBold = bold;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getAlign() {
        return align;
    }

    public void setAlign(String align) {
        this.align = align;
    }

    public boolean isUnderline() {
        return isUnderline;
    }

    public void setUnderline(boolean underline) {
        isUnderline = underline;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public ReceiptPrinted getReceiptPrinted() {
        return receiptPrinted;
    }

    public void setReceiptPrinted(ReceiptPrinted receiptPrinted) {
        this.receiptPrinted = receiptPrinted;
    }

    public String getQrsize() {
        return qrsize;
    }

    public void setQrsize(String qrsize) {
        this.qrsize = qrsize;
    }
}
