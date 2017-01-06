package com.myee.tarot.fileUpload.domain;

/**
 * Created by Administrator on 2016/12/27.
 */

import com.myee.tarot.core.GenericEntity;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "C_FILEUPLOAD")
public class FileUpload extends GenericEntity<Long, FileUpload> {

    @Id
    @Column(name = "FILEUPLOAD_ID", unique = true, nullable = false)
    @TableGenerator(name = "TABLE_GEN", table = "C_SEQUENCER", pkColumnName = "SEQ_NAME", valueColumnName = "SEQ_COUNT", pkColumnValue = "FILEUPLOAD_SEQ_NEXT_VAL",allocationSize=1)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "TABLE_GEN")
    private Long id;

    @Column(name = "NAME",unique = true)
    private String name;

    @Column(name = "LENGTH" )
    private long length;

    @Column(name = "PATH")
    private String path;

    @Column(name = "MD5",unique = true)
    private String md5;

    @Column(name = "STATUS")
    private Integer status;  //状态 0传输成功 1传输失败

    @Column(name = "CREATE_TIME")
    private Date createTime;

    @Column(name = "UPDATE_TIME")
    private Date updateTime;

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public long getLength() {
        return length;
    }

    public void setLength(long length) {
        this.length = length;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }


}

