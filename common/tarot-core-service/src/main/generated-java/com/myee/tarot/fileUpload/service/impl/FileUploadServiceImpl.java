package com.myee.tarot.fileUpload.service.impl;

import com.myee.tarot.core.service.GenericEntityServiceImpl;
import com.myee.tarot.fileUpload.dao.FileUploadDao;
import com.myee.tarot.fileUpload.domain.FileUpload;
import com.myee.tarot.fileUpload.service.FileUploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by Administrator on 2016/12/27.
 */
@Service
public class FileUploadServiceImpl extends GenericEntityServiceImpl<Long, FileUpload> implements FileUploadService {

    protected FileUploadDao fileUploadDao;

    @Autowired
    public FileUploadServiceImpl(FileUploadDao fileUploadDao) {
        super(fileUploadDao);
        this.fileUploadDao = fileUploadDao;
    }

    @Override
    public FileUpload getByName(String name) {
        return fileUploadDao.getByName(name);
    }

    @Override
    public FileUpload getByNameOrMD5(String name, String md5) {
        return fileUploadDao.getByNameOrMD5(name,md5);
    }
}
