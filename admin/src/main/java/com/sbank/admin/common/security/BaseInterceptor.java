package com.sbank.admin.common.security;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sbank.admin.administrator.domain.AdminMenuAccessLog;
import com.sbank.admin.administrator.service.AdminMenuAccessLogService;
import com.sbank.admin.common.annotation.AuthMethod;
import com.sbank.admin.common.constants.GlobalConstants;
import com.sbank.admin.common.domain.AccessMenu;
import com.sbank.admin.common.domain.AdminMenu;
import com.sbank.admin.common.service.CommonService;
import com.sbank.admin.common.util.CommonUtil;
import com.sbank.admin.login.domain.Login;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

/**--------------------------------------------------------------------
 * ■JWT 인터셉터 map의 Setter ■payletter ■2018-09-28
 --------------------------------------------------------------------**/
@Component
public class BaseInterceptor extends HandlerInterceptorAdapter {
    private static final Logger logger = LoggerFactory.getLogger(BaseInterceptor.class);

    private static final String LOGIN_URI = "/login";

    @Autowired
    private CommonService commonService;

    @Autowired
    private AdminMenuAccessLogService adminMenuAccessLogService;

    @Value("${server.host}")
    private String strServerHost;

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object object, Exception ex) throws Exception {
        // Nothing To Do
    }

    /**--------------------------------------------------------------------
     * ■PreHandle (주 처리 부) ■payletter ■2018-09-28
     --------------------------------------------------------------------**/
    @Override
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object object) throws Exception {
        Login      login      = null;
        AccessMenu accessMenu = null;
        LinkedHashMap<Integer, AdminMenu> menuList = null;

        String strRequestURI = httpServletRequest.getRequestURI();

        // 로그인 시도하는 경우, 로그를 남김
        if("/login/loginProc".equalsIgnoreCase(strRequestURI)) {
            insertLog(accessMenu, httpServletRequest, object, login);
            return true;
        }

        try {
            ByteArrayInputStream bais = new ByteArrayInputStream((byte[]) httpServletRequest.getSession().getAttribute(GlobalConstants.SESSION_LOGIN_KEY));
            ObjectInputStream    ois  = new ObjectInputStream(bais);

            login = (Login) ois.readObject();
        } catch(Exception ex) {
            CommonUtil.globalExceptionHandle(ex, logger);

            return redirectResponse(httpServletResponse, LOGIN_URI, false);
        }

        if(ObjectUtils.isEmpty(login)) {
            return redirectResponse(httpServletResponse, LOGIN_URI, false);
        }

        // 접속 메뉴 정보 획득
        accessMenu = getAccessMenu(login, strRequestURI);

        if(accessMenu == null) {
            return redirectResponse(httpServletResponse, "/", false);
        }

        // 메뉴 리스트 조회
        menuList = commonService.selectMenuList(login);

        // 접근 메뉴 권한 확인
        if(!checkAuth(object, accessMenu)) {
            httpServletResponse.setStatus(401);
            return false;
        }

        httpServletRequest.getSession().setAttribute(GlobalConstants.SESSION_CI_FIELD_FLAG, accessMenu.getCiReadFlag());

        // 메뉴 사용 여부 확인
        if(!accessMenu.getMenuGroupUseFlag() || !accessMenu.getMenuRoleUseFlag() || !accessMenu.getMenuUseFlag()) {
            return redirectResponse(httpServletResponse, LOGIN_URI, false);
        }

        // 관리자 메뉴 접근 로그 등록
        insertLog(accessMenu, httpServletRequest, object, login);

        // Request 정보 주입
        setRequestAttribute(httpServletRequest, accessMenu, login, menuList);

        try {
            return super.preHandle(httpServletRequest, httpServletResponse, object);
        } catch(Exception ex) {
            CommonUtil.globalExceptionHandle(ex, logger);
            return false;
        }
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object object, ModelAndView objModalAndView) throws Exception {
        // Nothing To Do
    }

    /**--------------------------------------------------------------------
     * ■응답 경로 리다이렉션 모듈 ■payletter ■2018-09-28
     --------------------------------------------------------------------**/
    private boolean redirectResponse(HttpServletResponse httpServletResponse, String strRedirectURI, boolean blnReturn) {
        try {
            httpServletResponse.sendRedirect(strRedirectURI);
        } catch(IOException ex) {
            CommonUtil.globalExceptionHandle(ex, logger);
        }

        return blnReturn;
    }

    /**--------------------------------------------------------------------
     * ■유효 권한 확인 모듈 ■payletter ■2018-09-28
     --------------------------------------------------------------------**/
    private boolean checkAuth(Object object, AccessMenu accessMenu) {
        AuthMethod authMethod = null;

        if(object instanceof HandlerMethod) {
            authMethod = ((HandlerMethod) object).getMethodAnnotation(AuthMethod.class);
        }

        return (authMethod == null || (authMethod.hasAuth()).getIntVal() >= accessMenu.getAuthCode());
    }

    /**--------------------------------------------------------------------
     * ■접속 메뉴 정보 획득 모듈 ■payletter ■2018-09-28
     --------------------------------------------------------------------**/
    private AccessMenu getAccessMenu(Login login, String strRequestURI) {
        AccessMenu accessMenu = new AccessMenu();

        accessMenu.setAdminID(login.getAdminID());
        accessMenu.setMenuDivSgmnt(getMenuSgmt(strRequestURI));

        return commonService.selectAccessMenu(accessMenu);
    }

    /**--------------------------------------------------------------------
     * ■호출 함수 정보 획득 모듈 ■payletter ■2018-09-28
     --------------------------------------------------------------------**/
    private String getCallMethod(Object obj) {
        String strMethodName = null;

        if(obj instanceof HandlerMethod) {
            strMethodName = ((HandlerMethod)obj).getMethod().getName();
        }
        return strMethodName;
    }

    /**--------------------------------------------------------------------
     * ■메뉴 Segment 정보 획득 모듈 ■payletter ■2018-09-28
     --------------------------------------------------------------------**/
    private String getMenuSgmt(String strRequestURI) {
        String[] arrMenuSgmt = strRequestURI.split("/");
        String strRet = null;

        if(arrMenuSgmt.length == 2) {
            strRet = String.format("/%s", arrMenuSgmt[1]);
        } else if(arrMenuSgmt.length > 2) {
            strRet = String.format("/%s/%s", arrMenuSgmt[1], arrMenuSgmt[2]);
        }
        return strRet;
    }

    /**--------------------------------------------------------------------
     * ■파라미터 정보 획득 모듈 ■payletter ■2018-09-28
     --------------------------------------------------------------------**/
    private String getParameters(HttpServletRequest httpServletRequest) {
        String strParams  = "";
        String strRequest = "";

        StringBuilder strTemp = new StringBuilder();

        try {
            strRequest = IOUtils.toString(httpServletRequest.getInputStream(), "UTF-8");
        } catch(IOException ex) {
            CommonUtil.globalExceptionHandle(ex, logger);
            return strParams;
        }

        if(StringUtils.isNotEmpty(strRequest) && !(httpServletRequest instanceof MultipartHttpServletRequest)) {
            try {
                Gson gson = new Gson();
                Map<String, Object> map = gson.fromJson(strRequest, new TypeToken<Map<String, Object>>(){}.getType());

                for (Map.Entry<String, Object> entry : map.entrySet()) {
                    String strKey   = entry.getKey();
                    String strValue = entry.getValue().toString();

                    if(strKey.contains("pwd")||strKey.contains("Password")||strKey.contains("Pwd")) {
                        strValue = "******";
                    } else if(strValue.length() > 100) {
                        strValue = strValue.substring(0, 100) + "...";
                    }

                    if(StringUtils.isNotEmpty(strTemp)) {
                        strTemp.append("|");
                    }

                    strTemp.append(String.format("%s=%s", strKey, strValue));
                }
            } catch(Exception ex) {
                CommonUtil.globalExceptionHandle(ex, logger);

                strTemp.append(strRequest);
            }
        }
        return createParams(strTemp);
    }

    /**--------------------------------------------------------------------
     * ■파라메터 정보 생성 모듈 ■payletter ■2018-09-28
     --------------------------------------------------------------------**/
    private String createParams(StringBuilder strTemp) {
        if(strTemp.length() > 0 && strTemp.length() <= 1000) {
            return strTemp.substring(0, strTemp.length());
        } else if(strTemp.length() > 1000) {
            return strTemp.substring(0, 1000);
        } else {
            return "";
        }
    }

    /**--------------------------------------------------------------------
     * ■관리자 메뉴 접근 로그 등록 ■payletter ■2018-09-28
     --------------------------------------------------------------------**/
    private void insertLog(AccessMenu accessMenu, HttpServletRequest httpServletRequest, Object object, Login login) {
        String strMethodName = getCallMethod(object);

        if(strMethodName == null) {
            strMethodName = httpServletRequest.getRequestURI();
        }

        AdminMenuAccessLog adminMenuAccessLog = new AdminMenuAccessLog();

        if(strMethodName.contains("loginProc")) {
            adminMenuAccessLog.setMenuNo((short) 0);
            adminMenuAccessLog.setMenuLink(httpServletRequest.getRequestURI());
            adminMenuAccessLog.setMethodName(strMethodName);
            adminMenuAccessLog.setLogDesc(getParameters(httpServletRequest));
            adminMenuAccessLog.setAdminNo(0);
            adminMenuAccessLog.setIpAddr(CommonUtil.getIpAddr(httpServletRequest));
        } else {
            adminMenuAccessLog.setMenuNo(accessMenu.getMenuNo());
            adminMenuAccessLog.setMenuLink(httpServletRequest.getRequestURI());
            adminMenuAccessLog.setMethodName(strMethodName);
            adminMenuAccessLog.setLogDesc(getParameters(httpServletRequest));
            adminMenuAccessLog.setAdminNo(login.getAdminNo());
            adminMenuAccessLog.setIpAddr(login.getIpAddr());
        }

        adminMenuAccessLog.convertWorkType();
        adminMenuAccessLogService.insertAdminMenuAccessLog(adminMenuAccessLog);
    }

    /**--------------------------------------------------------------------
     * ■Request 객체 Attribute 정보 주입 모듈 ■payletter ■2018-09-28
     --------------------------------------------------------------------**/
    private void setRequestAttribute(HttpServletRequest httpServletRequest, AccessMenu accessMenu, Login login, LinkedHashMap<Integer, AdminMenu> menuList) {
        Date dtToYMD = new Date();

        Calendar calFromYMD   = Calendar.getInstance();
        Calendar calDefaultYM = Calendar.getInstance();
        Calendar calWeekYMD   = Calendar.getInstance();
        Calendar calYearYMD   = Calendar.getInstance();

        calFromYMD.setTime(dtToYMD);
        calFromYMD.add(Calendar.DATE, -7);

        calWeekYMD.setTime(dtToYMD);
        calWeekYMD.add(Calendar.DATE, +7);

        calDefaultYM.setTime(dtToYMD);
        calDefaultYM.add(Calendar.MONTH, -1);

        calYearYMD.setTime(dtToYMD);
        calYearYMD.add(Calendar.YEAR, +1);

        httpServletRequest.setAttribute("menuGroupNo",   accessMenu.getMenuGroupNo());
        httpServletRequest.setAttribute("menuGroupName", commonService.getMessage(accessMenu.getMenuGroupName()));
        httpServletRequest.setAttribute("menuNo",        accessMenu.getMenuNo());
        httpServletRequest.setAttribute("menuName",      commonService.getMessage(accessMenu.getMenuName()));
        httpServletRequest.setAttribute("authCode",      accessMenu.getAuthCode());
        httpServletRequest.setAttribute("ciReadFlag",    accessMenu.getCiReadFlag());
        httpServletRequest.setAttribute("dnAvailFlag",   accessMenu.getDnAvailFlag());

        httpServletRequest.setAttribute("adminNo",       login.getAdminNo());
        httpServletRequest.setAttribute("adminID",       login.getAdminID());
        httpServletRequest.setAttribute("lastLoginIP",   login.getLastLoginIP());
        httpServletRequest.setAttribute("lastLoginDate", login.getLastLoginDate());
        httpServletRequest.setAttribute("strIPAddr",     login.getIpAddr());

        httpServletRequest.setAttribute("menuList",      menuList);

        httpServletRequest.setAttribute("strFromYMD",    new SimpleDateFormat("yyyy-MM-dd").format(calFromYMD.getTime()));
        httpServletRequest.setAttribute("strToYMD",      new SimpleDateFormat("yyyy-MM-dd").format(dtToYMD));
        httpServletRequest.setAttribute("strWeekYMD",    new SimpleDateFormat("yyyy-MM-dd").format(calWeekYMD.getTime()));
        httpServletRequest.setAttribute("strYearYMD",    new SimpleDateFormat("yyyy-MM-dd").format(calYearYMD.getTime()));
        httpServletRequest.setAttribute("strDefaultYM",  new SimpleDateFormat("yyyyMM").format(calDefaultYM.getTime()));
        httpServletRequest.setAttribute("strFromYM",     new SimpleDateFormat("yyyy-MM").format(calDefaultYM.getTime()));
        httpServletRequest.setAttribute("strToYM",       new SimpleDateFormat("yyyy-MM").format(dtToYMD));
        httpServletRequest.setAttribute("strFromY",      new SimpleDateFormat("yyyy").format(calDefaultYM.getTime()));
        httpServletRequest.setAttribute("strToY",        new SimpleDateFormat("yyyy").format(dtToYMD));
        httpServletRequest.setAttribute("strToTime",     new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(dtToYMD));
        httpServletRequest.setAttribute("strWeekTime",   new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(calWeekYMD.getTime()));
    }
}
