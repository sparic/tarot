package com.myee.tarot.web.apiold.controller;

import com.myee.tarot.core.util.ajax.AjaxResponse;
import com.myee.tarot.web.apiold.util.QiniuStoreClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by chay on 2016/9/5.
 */
@Controller
@RequestMapping(value = {"admin/superman/picture", "shop/superman/picture"})
public class PictureManageController extends BaseController{

    private Logger logger = LoggerFactory.getLogger(PictureManageController.class);

    @RequestMapping(value = "/tokenAndKey")
    @ResponseBody
    public AjaxResponse qiniuTokenAndKey(){
        AjaxResponse resp = AjaxResponse.success();
        resp.addEntry("uptoken",QiniuStoreClient.getUploadTokenAndKey(getCommConfig()).getUptoken());
        return resp;
//        return AjaxResult.success(QiniuStoreClient.getUploadTokenAndKey(user.getClientId(), user.getOrgId(), user.getUserId(), fileType));
    }
}
