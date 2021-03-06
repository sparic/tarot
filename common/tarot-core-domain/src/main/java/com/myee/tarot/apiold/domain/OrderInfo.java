package com.myee.tarot.apiold.domain;

import com.myee.tarot.core.GenericEntity;
import com.myee.tarot.merchant.domain.MerchantStore;

import javax.persistence.*;
import java.util.Date;

/**
 * Info: clever
 * User: Gary.zhang@clever-m.com
 * Date: 2016-01-27
 * Time: 09:17
 * Version: 1.0
 * History: <p>如果有修改过程，请记录</P>
 */
@Entity
//20160823 看代码暂时没用到，先不生成数据库
@javax.persistence.Table(name = "CA_ORDER")
public class OrderInfo extends GenericEntity<Long, OrderInfo> {

    @Id
    @Column(name = "ID", unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.AUTO)//AUTO：主键由程序控制。IDENTITY：主键由数据库自动生成（主要是自动增长型） ； SEQUENCE：根据底层数据库的序列来生成主键，条件是数据库支持序列（类似oracle）；TABLE：使用一个特定的数据库表格来保存主键。
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "STORE_ID")
    private MerchantStore store;//商户编号

    @Column(name = "MENU_ID")
    private long menuId;//菜品编号
    @Column(name = "ORDER_ID",length = 60)
    private String orderId;//订单编号
    @Column(name = "PRICE",length = 11)
    private int price;//价格，单位元
    @Column(name = "NUMBER",length = 11)
    private int number;//菜品个数
    @Column(name = "AMOUNT",length = 50)
    private int amount;//总价
    @Column(name = "OUT_ORDER_ID",length = 60)
    private String outOrderId;//微信支付宝订单号
    @Column(name = "PAY_TYPE",columnDefinition = "TINYINT")
    private int payType;//支付类型 1支付宝 2微信 3百度
    @Column(name = "PAY_STATUS",columnDefinition = "TINYINT")
    private int payStatus;//状态 0未支付 1已支付
    @Column(name = "CREATED")
    @Temporal(TemporalType.TIMESTAMP)
    private Date created;
    @Column(name = "UPDATED")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updated;
    @Column(name = "ACTIVE",columnDefinition = "BIT")
    private Boolean active = Boolean.TRUE;//状态 0启用 1停用

    //作为老接口的输入参数保留字段，不用持久化到数据库
    @Transient
    private long shopId;

    public long getMenuId() {
        return menuId;
    }

    public void setMenuId(long menuId) {
        this.menuId = menuId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getOutOrderId() {
        return outOrderId;
    }

    public void setOutOrderId(String outOrderId) {
        this.outOrderId = outOrderId;
    }

    public int getPayType() {
        return payType;
    }

    public void setPayType(int payType) {
        this.payType = payType;
    }

    public int getPayStatus() {
        return payStatus;
    }

    public void setPayStatus(int payStatus) {
        this.payStatus = payStatus;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public MerchantStore getStore() {
        return store;
    }

    public void setStore(MerchantStore store) {
        this.store = store;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getUpdated() {
        return updated;
    }

    public void setUpdated(Date updated) {
        this.updated = updated;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public long getShopId() {
        return shopId;
    }

    public void setShopId(long shopId) {
        this.shopId = shopId;
    }
}
