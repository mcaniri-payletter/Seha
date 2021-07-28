package com.sbank.admin.common.security;

import com.sbank.admin.common.constants.GlobalConstants;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class LoginCheckInterceptor extends HandlerInterceptorAdapter {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception{
        HttpSession session = request.getSession();
        Object loginSession = session.getAttribute(GlobalConstants.SESSION_LOGIN_KEY);
        if(loginSession == null) {
            response.sendRedirect("/");
            return false;
        }
        return true;
    }
}
