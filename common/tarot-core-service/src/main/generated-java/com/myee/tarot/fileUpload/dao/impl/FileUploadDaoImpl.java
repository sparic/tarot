package com.myee.tarot.fileUpload.dao.impl;

import com.myee.tarot.core.dao.GenericEntityDaoImpl;
import com.myee.tarot.fileUpload.dao.FileUploadDao;
import com.myee.tarot.fileUpload.domain.FileUpload;
import com.myee.tarot.fileUpload.domain.QFileUpload;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQuery;
import org.springframework.stereotype.Repository;

/**
 * Created by Administrator on 2016/12/27.
 */
@Repository
public class FileUploadDaoImpl extends GenericEntityDaoImpl<Long, FileUpload> implements FileUploadDao {
    @Override
    public FileUpload getByName(String name) {
        QFileUpload qFileUpload = QFileUpload.fileUpload;
        JPQLQuery<FileUpload> query = new JPAQuery(getEntityManager());
        FileUpload result = query.from(qFileUpload).where(qFileUpload.name.eq(name)).fetchOne();
        return result;
    }

    @Override
    public FileUpload getByNameOrMD5(String name, String md5) {
        QFileUpload qFileUpload = QFileUpload.fileUpload;
        JPQLQuery<FileUpload> query = new JPAQuery(getEntityManager());
        FileUpload result = query.from(qFileUpload).where(qFileUpload.name.eq(name).or(qFileUpload.md5.eq(md5))).fetchOne();
        return result;
    }
}
