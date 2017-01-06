package com.myee.tarot.fileUpload.dao;


import com.myee.tarot.core.dao.GenericEntityDao;
import com.myee.tarot.fileUpload.domain.FileUpload;

/**
 * Created by Administrator on 2016/12/27.
 */
public interface FileUploadDao extends GenericEntityDao<Long, FileUpload> {

    FileUpload getByName(String name);
    FileUpload getByNameOrMD5(String name, String md5);
}
