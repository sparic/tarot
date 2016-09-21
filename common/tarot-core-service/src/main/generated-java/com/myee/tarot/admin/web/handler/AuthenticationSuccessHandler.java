package com.myee.tarot.admin.web.handler;

import com.myee.tarot.admin.domain.AdminUser;
import com.myee.tarot.admin.service.AdminUserService;
import com.myee.tarot.core.Constants;
import com.myee.tarot.core.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.ProviderNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

/**
 * Created by Martin on 2016/4/18.
 */
public class AuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationSuccessHandler.class);

    @Autowired
    private AdminUserService userService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        // last access timestamp
        String userName = authentication.getName();
        try {
            AdminUser user = userService.getByLogin(userName);

            user.setLoginIP(request.getRemoteAddr());
            user.setLastLogin(new Date());

            userService.update(user);
            String securityCode = request.getParameter(Constants.REQUEST_SECURITY_CODE);
            String sessionCode = (String) request.getSession().getAttribute(Constants.SESSION_SECURITY_CODE);
            if(StringUtil.isNullOrEmpty(sessionCode) || !sessionCode.equalsIgnoreCase(securityCode)){
                authentication.setAuthenticated(false);
            }
            response.sendRedirect(request.getContextPath() + "/admin/home.html");
        } catch (Exception e) {
            LOGGER.error("User authenticationSuccess", e);
        }
    }

}
