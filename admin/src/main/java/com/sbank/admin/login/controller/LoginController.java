package com.sbank.admin.login.controller;

import com.sbank.admin.administrator.service.AdministratorService;
import com.sbank.admin.common.constants.GlobalConstants;
import com.sbank.admin.common.constants.PathConstants;
import com.sbank.admin.common.exception.GlobalException;
import com.sbank.admin.login.domain.Login;
import com.sbank.admin.common.domain.AdminMenu;
import com.sbank.admin.common.service.CommonService;
import com.sbank.admin.common.util.CommonUtil;
import com.sbank.admin.login.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.CannotCreateTransactionException;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.security.NoSuchAlgorithmException;

/**--------------------------------------------------------------------
 * ■로그인 컨트롤러 ■payletter ■2018-09-28
 --------------------------------------------------------------------**/
@Controller
@RequestMapping(value = {"", "/login"})
public class LoginController {
    @Autowired
    private LoginService loginService;

    @Autowired
    private AdministratorService administratorService;

    @Autowired
    private CommonService commonService;

    /**--------------------------------------------------------------------
     * ■로그인 페이지 진입 함수 ■payletter ■2018-09-28
     --------------------------------------------------------------------**/
    @RequestMapping(value = {"", "/", "/login"})
    public ModelAndView initPage(HttpSession httpSession, @RequestParam(value="inpAdminID", defaultValue="") String adminID) throws Exception {
        ModelAndView mav = new ModelAndView(new MappingJackson2JsonView());
        String strInitMenuLink = PathConstants.PATH_VIEW_LOGIN;
        Boolean redirectFlag = false;

        mav.addObject("HIDDEN_CSRF_ID", GlobalConstants.HIDDEN_CSRF_ID);
        mav.setViewName(strInitMenuLink);

        // 로그인 세션이 남아 있을 때 초기 페이지로 리다이렉트
        if(null != httpSession && null != httpSession.getAttribute(GlobalConstants.SESSION_LOGIN_KEY)) {
            ByteArrayInputStream bais = new ByteArrayInputStream((byte[]) httpSession.getAttribute(GlobalConstants.SESSION_LOGIN_KEY));
            ObjectInputStream    ois  = new ObjectInputStream(bais);

            // 세션 로그인 정보
            Login login = (Login) ois.readObject();
            if(login != null) {
                redirectFlag = true;

                // 비밀번호 초기화에서 이동했을 경우, 세션에 남아있는 아이디와 비교
                String strSessionAdminID = login.getAdminID();
                if(!CommonUtil.isNullOrEmpty(adminID)) {
                    if(!strSessionAdminID.equals(adminID)) {
                        redirectFlag = false;
                    }
                }
            }

            if(redirectFlag) {
                // 관리자 로그인 시 초기 페이지 셋팅 (접근 권한을 가지고 있는 메뉴 1개)
                AdminMenu objAdminMenu = commonService.selectMenuLinkByLoginAction(login);
                if(objAdminMenu != null) {
                    RedirectView redirectView = new RedirectView();
                    redirectView.setUrl(objAdminMenu.getMenuLink());
                    redirectView.setExposeModelAttributes(false);

                    mav.setView(redirectView);
                }
            }
        }

        return mav;
    }

    /**--------------------------------------------------------------------
     * ■로그아웃 처리 함수 ■payletter ■2018-09-28
     --------------------------------------------------------------------**/
    @RequestMapping(value = "/logout")
    public void logOut(HttpServletResponse httpServletResponse, HttpSession httpSession) throws Exception {
        httpSession.invalidate();
        httpServletResponse.sendRedirect("/");
    }

    /**--------------------------------------------------------------------
     * ■CSRF Token 체크 함수 ■payletter ■2018-09-28
     --------------------------------------------------------------------**/
    @RequestMapping(value = "/getCSRFToken")
    public ModelAndView getCSRFToken(HttpSession httpSession) {
        ModelAndView mav = new ModelAndView(new MappingJackson2JsonView());
        mav.addObject("CSRFToken", null != httpSession.getAttribute(GlobalConstants.SESSION_CSRF_KEY) ? httpSession.getAttribute(GlobalConstants.SESSION_CSRF_KEY) : "");
        return mav;
    }

    /**--------------------------------------------------------------------
     * ■로그인 처리 함수 ■payletter ■2018-09-28
     --------------------------------------------------------------------**/
    @PostMapping(value = "/loginProc")
    public ModelAndView loginProc(HttpServletRequest httpServletRequest, HttpSession httpSession,
                                  @Validated @RequestBody Login login) throws IOException, NoSuchAlgorithmException {
        String  strResultMessage  = "";
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream    oos  = new ObjectOutputStream(baos);
        login.setIpAddr(CommonUtil.getIpAddr(httpServletRequest));

        // 로그인 처리
        Login objRetLogin = new Login();

        try {
            objRetLogin = loginService.loginProc(login);
        } catch (CannotCreateTransactionException e) {
            throw new GlobalException("login.msgFailFoundAdminInfo", true);
        }

        oos.writeObject(objRetLogin);
        httpSession.setAttribute(GlobalConstants.SESSION_LOGIN_KEY, baos.toByteArray());
        return CommonUtil.getResJsonView(objRetLogin, GlobalConstants.COMMON_SUCCEED_CODE, strResultMessage);
    }

    /**--------------------------------------------------------------------
     * ■비밀번호 초기화 페이지 진입 함수 (메일에서의 진입) ■payletter ■2018-09-28
     --------------------------------------------------------------------**/
    @RequestMapping(value = "/resetPwdRequest")
    public String resetPwdRequest(Model model, @RequestParam(value="token", defaultValue = "") String token) {
        String strReturnUrl;

        try {
            String adminID = administratorService.checkResetPwdRequest(token);
            model.addAttribute("token",   token);
            model.addAttribute("adminID", adminID);
            strReturnUrl = GlobalConstants.PATH_RESET_PWD;
        } catch(GlobalException ex) {
            model.addAttribute("strErrorMsg", ex.getMessage());
            strReturnUrl = PathConstants.PATH_CONTROLLER_LOGIN;
        }

        return strReturnUrl;
    }
}