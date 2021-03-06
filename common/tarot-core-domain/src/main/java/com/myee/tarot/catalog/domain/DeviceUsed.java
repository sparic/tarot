package com.myee.tarot.catalog.domain;

import com.myee.tarot.core.GenericEntity;
import com.myee.tarot.merchant.domain.MerchantStore;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Martin on 2016/4/18.
 */
@Entity
@Table(name = "C_DEVICE_USED")
public class DeviceUsed extends GenericEntity<Long, DeviceUsed> {

    @Id
    @Column(name = "DEVICE_USED_ID", unique = true, nullable = false)
    @TableGenerator(name = "TABLE_GEN", table = "C_SEQUENCER", pkColumnName = "SEQ_NAME", valueColumnName = "SEQ_COUNT", pkColumnValue = "DEVICE_USED_SEQ_NEXT_VAL",allocationSize=1)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "TABLE_GEN")
    protected Long id;

    @Column(name = "NAME", length=60)
    protected String name;

    @Column(name = "BOARD_NO", unique = true,length=60)
    protected String boardNo;

    @Column(name = "DEVICE_NUM", length=60)
    protected String deviceNum;

    @Column(name = "DESCRIPTION", length=255)
    protected String description;

    @Column(name = "PHONE", length=11)
    private String phone;

    @ManyToOne(targetEntity = MerchantStore.class, optional = false)
    @JoinColumn(name = "STORE_ID")
    protected MerchantStore store;

    @ManyToOne(targetEntity = Device.class, optional = false)
    @JoinColumn(name = "DEVICE_ID")
    protected Device device;

    @ManyToMany(targetEntity = ProductUsed.class, fetch = FetchType.LAZY/*cascade = CascadeType.REFRESH*/)
    @JoinTable(name = "C_PRODUCT_USED_DEV_XREF",
            joinColumns = {@JoinColumn(name = "DEVICE_USED_ID", nullable = false)},
            inverseJoinColumns = {@JoinColumn(name = "PRODUCT_USED_ID", nullable = false)}
    )
    protected List<ProductUsed> productUsed = new ArrayList<ProductUsed>();

    @OneToMany(mappedBy = "deviceUsed", targetEntity = DeviceUsedAttribute.class, fetch = FetchType.LAZY)
    protected List<DeviceUsedAttribute> attributes = new ArrayList<DeviceUsedAttribute>();

//    @OneToMany(mappedBy = "deviceUsed", targetEntity = DeviceUsedAttribute.class, cascade = {CascadeType.ALL}, orphanRemoval = true, fetch = FetchType.LAZY)
//    @MapKey(name = "name")
//    protected Map<String, DeviceUsedAttribute> deviceUsedAttribute = new HashMap<String, DeviceUsedAttribute>();

    @Transient
    private String rtmpUrl;

    public String getRtmpUrl() {
        return rtmpUrl;
    }

    public void setRtmpUrl(String rtmpUrl) {
        this.rtmpUrl = rtmpUrl;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

//    public ProductUsed getProductUsed() {
//        return productUsed;
//    }
//
//    public void setProductUsed(ProductUsed productUsed) {
//        this.productUsed = productUsed;
//    }

    public List<ProductUsed> getProductUsed() {
        return productUsed;
    }

    public void setProductUsed(List<ProductUsed> productUsed) {
        this.productUsed = productUsed;
    }

    public MerchantStore getStore() {
        return store;
    }

    public void setStore(MerchantStore store) {
        this.store = store;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBoardNo() {
        return boardNo;
    }

    public void setBoardNo(String boardNo) {
        this.boardNo = boardNo;
    }

    public String getDeviceNum() {
        return deviceNum;
    }

    public void setDeviceNum(String deviceNum) {
        this.deviceNum = deviceNum;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Device getDevice() {
        return device;
    }

    public void setDevice(Device device) {
        this.device = device;
    }
//    public Map<String, DeviceUsedAttribute> getDeviceUsedAttribute() {
//        return deviceUsedAttribute;
//    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public List<DeviceUsedAttribute> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<DeviceUsedAttribute> attributes) {
        this.attributes = attributes;
    }
}
