package com.myee.tarot.web.configuration.controller;

import com.google.common.collect.Maps;
import com.myee.tarot.configuration.domain.MailContact;
import com.myee.tarot.configuration.service.MailContactService;
import com.myee.tarot.core.Constants;
import com.myee.tarot.core.util.PageResult;
import com.myee.tarot.core.util.EmailSenderUtil;
import com.myee.tarot.core.util.WhereRequest;
import com.myee.tarot.core.util.ajax.AjaxPageableResponse;
import com.myee.tarot.core.util.ajax.AjaxResponse;
import com.myee.tarot.merchant.domain.MerchantStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * Created by Ray.Fu on 2016/12/30.
 */
@Controller
public class MailContactController {

    private static final Logger LOGGER = LoggerFactory.getLogger(MailContactController.class);

    @Autowired
    private MailContactService mailContactService;

    @Autowired
    private EmailSenderUtil emailSenderUtil;

    @RequestMapping(value = {"admin/configuration/mailcontact/paging"}, method = RequestMethod.GET)
    @ResponseBody
    public AjaxPageableResponse listAllContact (WhereRequest whereRequest, HttpServletRequest request) {
        AjaxPageableResponse ajaxPageableResponse = new AjaxPageableResponse();
        PageResult<MailContact> list = mailContactService.listAllContact(whereRequest);
        for (MailContact mailContact : list.getList()) {
            ajaxPageableResponse.addDataEntry(objectToEntry(mailContact));
        }
        return ajaxPageableResponse;
    }

    @RequestMapping(value = "admin/configuration/mailcontact/delete", method = RequestMethod.POST)
    @ResponseBody
    public AjaxResponse deleteMailContact(@Valid @RequestBody MailContact mailContact, HttpServletRequest request) throws Exception {
        AjaxResponse resp = new AjaxResponse();
        MerchantStore thisSwitchMerchantStore = (MerchantStore) request.getSession().getAttribute(Constants.ADMIN_STORE);
        if (thisSwitchMerchantStore == null) {
            resp = AjaxResponse.failed(AjaxResponse.RESPONSE_STATUS_FAIURE);
            resp.setErrorString("请先切换门店");
            return resp;
        }
        MailContact mailContactDB = mailContactService.findById(mailContact.getId());
        if (mailContactDB == null) {
            resp = AjaxResponse.failed(AjaxResponse.RESPONSE_STATUS_FAIURE);
            resp.setErrorString("错误:该用户不存在，无法被删除");
            return resp;
        }
        try {
            mailContactService.delete(mailContactDB);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(),e);
            resp = AjaxResponse.failed(AjaxResponse.RESPONSE_STATUS_FAIURE);
            resp.setErrorString("删除错误!");
        }
        return resp;
    }

    @RequestMapping(value = "admin/configuration/mailcontact/save", method = RequestMethod.POST)
    @ResponseBody
    public AjaxResponse addMailContact(@RequestBody MailContact mailContact, HttpServletRequest request) throws Exception {
        AjaxResponse resp;
        MailContact mailContactDB = mailContactService.getByName(mailContact.getName());
        if (mailContactDB != null && !mailContactDB.getId().equals(mailContact.getId())) {
            return AjaxResponse.failed(AjaxResponse.RESPONSE_STATUS_FAIURE, "已有该联系人");
        }
        if (null != mailContact.getId()) {
            MailContact mailContact1 = mailContactService.findById(mailContact.getId());
            mailContact1.setName(mailContact.getName());
            mailContact1.setMailAddress(mailContact.getMailAddress());
            mailContact = mailContact1;
        } else {
            Object o = request.getSession().getAttribute(Constants.ADMIN_STORE);
            //新建账号将绑定切换的门店
            if (o == null) {
                return AjaxResponse.failed(AjaxResponse.RESPONSE_STATUS_FAIURE, "请先切换门店");
            }
            mailContact.setName(mailContact.getName());
            mailContact.setMailAddress(mailContact.getMailAddress());
        }
        mailContact = mailContactService.update(mailContact);
        resp = AjaxResponse.success();
        resp.addEntry(Constants.RESPONSE_UPDATE_RESULT, objectToEntry(mailContact));
        return resp;
    }

    private Map<String, Object> objectToEntry(MailContact mailContact) {
        Map map = Maps.newHashMap();
        map.put("id", mailContact.getId());
        map.put("name", mailContact.getName());
        map.put("mailAddress", mailContact.getMailAddress());
        return map;
    }


//    @RequestMapping(value = "admin/configuration/mailcontact/sendmail", method = RequestMethod.POST)
//    @ResponseBody
//    public AjaxResponse sendEmail (@RequestParam(value = "context") String context, @RequestParam(value = "subject") String subject) {
//        List<MailContact> contactList = mailContactService.list();
//        int size = contactList.size();
//        String[] contactArr = new String[size];
//        for (int i = 0; i < size; i++) {
//            contactArr[i] = contactList.get(i).getMailAddress();
//        }
//        return emailSenderUtil.send(Constants.MAIL_SENDER_ACCOUNT, contactArr, subject, context);
//    }

}
