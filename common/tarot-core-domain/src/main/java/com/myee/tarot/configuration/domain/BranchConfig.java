package com.myee.tarot.configuration.domain;

import com.myee.tarot.catalog.domain.ProductUsed;
import com.myee.tarot.core.GenericEntity;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Chay on 2017/01/03.
 */
@Entity
@Table(name = "C_BRANCH_CONFIG")
public class BranchConfig extends GenericEntity<Long, BranchConfig> {

    @Id
    @Column(name = "CONFIG_ID", unique = true, nullable = false)
    @TableGenerator(name = "TABLE_GEN", table = "C_SEQUENCER", pkColumnName = "SEQ_NAME", valueColumnName = "SEQ_COUNT", pkColumnValue = "BRANCH_CONFIG_SEQ_NEXT_VAL",allocationSize=1)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "TABLE_GEN")
    private Long id;

    @Column(name = "NAME", nullable = false,length = 100)
    protected String name;//主标题

    @Column(name = "SUB_NAME", nullable = false,length = 100)
    protected String subName;//子标题

    @Column(name = "MANAGER", nullable = false,length = 100)
    protected String manager;//分支负责人

    @Column(name = "DESCRIPTION",length = 255)
    protected String description;

    @Column(name = "CREATE_TIME")
    private Date createTime;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSubName() {
        return subName;
    }

    public void setSubName(String subName) {
        this.subName = subName;
    }

    public String getManager() {
        return manager;
    }

    public void setManager(String manager) {
        this.manager = manager;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
