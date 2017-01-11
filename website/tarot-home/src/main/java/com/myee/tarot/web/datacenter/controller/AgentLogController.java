package com.myee.tarot.web.datacenter.controller;

import com.myee.tarot.core.util.ajax.AjaxResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by Ray.Fu on 2017/1/4.
 */
@Controller
public class AgentLogController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AgentLogController.class);

//    @Autowired
//    private AgentLogService agentLogService;

    @RequestMapping(value = "agentLog/pullLog", method = RequestMethod.GET)
    @ResponseBody
    @PreAuthorize("hasAuthority('datacenter_agentlog_r')")
    public AjaxResponse pullLog(String boardNo) {
        AjaxResponse resp = new AjaxResponse();
        try {
//            agentLogService.pullLog(boardNo);
            AjaxResponse.success("拉取成功");
        } catch (Exception e) {
            AjaxResponse.failed(-1, "拉取失败");
            LOGGER.error(e.getMessage(), e);
        }
        return resp;
    }
}
