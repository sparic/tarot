package com.myee.tarot.fileUpload.service;

import com.myee.tarot.core.service.GenericEntityService;
import com.myee.tarot.fileUpload.domain.FileUpload;

/**
 * Created by Administrator on 2016/12/27.
 */
public interface FileUploadService extends GenericEntityService<Long, FileUpload> {
    FileUpload getByName(String name);
    FileUpload getByNameOrMD5(String name, String md5);
}
