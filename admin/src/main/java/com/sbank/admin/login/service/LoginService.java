package com.sbank.admin.login.service;

import com.sbank.admin.administrator.domain.AdminLoginFailHist;
import com.sbank.admin.administrator.domain.Administrator;
import com.sbank.admin.common.constants.GlobalConstants;
import com.sbank.admin.common.domain.AdminMenu;
import com.sbank.admin.common.exception.GlobalException;
import com.sbank.admin.common.exception.NoRollbackException;
import com.sbank.admin.common.repository.GenericDAO;
import com.sbank.admin.common.service.CommonService;
import com.sbank.admin.common.util.CommonUtil;
import com.sbank.admin.login.domain.Login;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Date;

/**--------------------------------------------------------------------
 * ■로그인 인터페이스 구현부 ■payletter ■2018-09-28
 --------------------------------------------------------------------**/
@Service
public class LoginService {
    public static final int MENU_NO       = 0;
    public static final int MENU_GROUP_NO = 0;

    @Autowired
    @Qualifier("mainDB")
    private GenericDAO<Object, Object> dao;

    @Autowired
    private CommonService commonService;

    /**--------------------------------------------------------------------
     * ■일반 로그인 서비스 처리 로직 ■payletter ■2018-09-28
     --------------------------------------------------------------------**/
    @Transactional(noRollbackFor= NoRollbackException.class)
    public Login loginProc(Login login) throws GlobalException, NoSuchAlgorithmException {
        Administrator objAdministrator = (Administrator) dao.selectOne("administrator.selectAdministratorInfo", login.getAdminID());

        AdminLoginFailHist objAdminLoginFailHist = new AdminLoginFailHist();
        objAdminLoginFailHist.setAdminID(login.getAdminID());
        objAdminLoginFailHist.setIpAddr(login.getIpAddr());

        if(objAdministrator == null) {
            objAdminLoginFailHist.setErrCode(9001);
            objAdminLoginFailHist.setErrMsg("login.msgNotFoundID");
        } else {
            checkAdmin(login, objAdministrator, objAdminLoginFailHist);
        }

        if(objAdministrator == null || !objAdministrator.getChkAdminFlag()) {
            // 로그인 실패 이력
            dao.insert("adminLoginFailHist.insertAdminLoginFailHist", objAdminLoginFailHist);

            throw new NoRollbackException(objAdminLoginFailHist.getErrCode(), objAdminLoginFailHist.getErrMsg(), true, false);
        }

        // 로그인 성공 정보 업데이트
        dao.update("login.updateLoginInfo", login);

        return createResLogin(objAdministrator, login);
    }

    /**--------------------------------------------------------------------
     * ■관리자 정보 체크 모듈 ■payletter ■2018-09-28
     --------------------------------------------------------------------**/
    private void checkAdmin(Login login, Administrator objAdministrator, AdminLoginFailHist objAdminLoginFailHist) throws NoSuchAlgorithmException {
        Boolean isChkAdminFlag = true;

        // 비밀번호 확인
        BCryptPasswordEncoder objPasswordEncoder = new BCryptPasswordEncoder(10, SecureRandom.getInstance(GlobalConstants.BCRYPT_ALGORITHM));
        Boolean isAdminPwdChkFlag = objPasswordEncoder.matches(login.getAdminPwd(), objAdministrator.getAdminPwd());

        if(StringUtils.isEmpty(objAdministrator.getAdminPwd()) || !isAdminPwdChkFlag) {
            objAdminLoginFailHist.setErrCode(9002);
            objAdminLoginFailHist.setErrMsg("common.msg.wrongPassword");

            isChkAdminFlag = false;
        }

        // 사용 여부 확인
        if(isChkAdminFlag && !objAdministrator.getUseFlag()) {
            objAdminLoginFailHist.setErrCode(9003);
            objAdminLoginFailHist.setErrMsg("login.msgStoppedUsingID");

            isChkAdminFlag = false;
        }

        // 접근 가능 IP 확인
        if(isChkAdminFlag && objAdministrator.getAccessIPRestrictFlag() && !objAdministrator.getAccessIP1().equals(login.getIpAddr())
                && !objAdministrator.getAccessIP2().equals(login.getIpAddr()) && !objAdministrator.getAccessIP3().equals(login.getIpAddr())) {
            objAdminLoginFailHist.setErrCode(9004);
            objAdminLoginFailHist.setErrMsg("login.msgUnableToLoginIP");

            isChkAdminFlag = false;
        }

        // 최근 비밀번호 변경 일자 확인
        if(isChkAdminFlag && GlobalConstants.LAST_PWD_CHG_DAYS_LIMIT < CommonUtil.getDateTimeDiff(objAdministrator.getDtPwdUpdDate().getTime(), new Date().getTime())) {
            objAdminLoginFailHist.setErrCode(9005);
            objAdminLoginFailHist.setErrMsg("login.msgPwdChangeDateOver");

            isChkAdminFlag = false;
        }

        objAdministrator.setChkAdminFlag(isChkAdminFlag);
    }

    /**--------------------------------------------------------------------
     * ■로그인 응답 모델 값 주입 모듈 ■payletter ■2018-09-28
     --------------------------------------------------------------------**/
    private Login createResLogin(Administrator objAdministrator, Login login) {
        login.setSessionKey("");
        login.setAdminNo(objAdministrator.getAdminNo());
        login.setAdminID(objAdministrator.getAdminID());
        login.setUseLanguageTypeCode(objAdministrator.getUseLanguageTypeCode());
        login.setLastLoginDate(objAdministrator.getLastLoginDate());
        login.setLastLoginIP(objAdministrator.getLastLoginIP());
        login.setFirstLoginFlag("First Login".equalsIgnoreCase(login.getLastLoginIP()));

        // 관리자 로그인 시 초기 페이지 셋팅 (접근 권한을 가지고 있는 메뉴 1개)
        AdminMenu objAdminMenu = commonService.selectMenuLinkByLoginAction(login);
        String menuLink = objAdminMenu.getMenuLink();
        login.setMenuLink(menuLink);

        return login;
    }
}