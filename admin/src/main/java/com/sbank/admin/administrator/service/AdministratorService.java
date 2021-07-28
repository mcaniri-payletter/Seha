package com.sbank.admin.administrator.service;

import com.sbank.admin.administrator.domain.Administrator;
import com.sbank.admin.common.constants.GlobalConstants;
import com.sbank.admin.common.exception.GlobalException;
import com.sbank.admin.common.repository.GenericDAO;
import com.sbank.admin.common.service.SendMailService;
import com.sbank.admin.common.util.CommonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.context.Context;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Date;
import java.util.List;

/**--------------------------------------------------------------------
 * ■관리자 인터페이스 구현부 ■payletter ■2018-09-28
 --------------------------------------------------------------------**/
@Service
public class AdministratorService {
    @Autowired
    @Qualifier("mainDB")
    private GenericDAO<Object, Object> dao;

    @Autowired
    private SendMailService sendMailService;

    @Value("${spring.profiles.active}")
    private String strServerEnvironment;

    @Value("${server.web.host}")
    private String strServerWebHost;

    /**--------------------------------------------------------------------
     * ■관리자 목록 조회 서비스 ■payletter ■2018-09-28
     --------------------------------------------------------------------**/
    @SuppressWarnings("unchecked")
    public List<Administrator> selectAdministratorList(Administrator administrator) {
        if(administrator.getExcelFlag()) {
            CommonUtil.setExcelDownload(administrator);
        } else {
            int intTotalCnt = (int)dao.selectOne("administrator.selectAdministratorCnt", administrator);

            administrator.setRecordsFiltered(intTotalCnt);
            administrator.setRecordsTotal(intTotalCnt);
        }

        return (List) dao.selectList("administrator.selectAdministratorList", administrator);
    }

    /**--------------------------------------------------------------------
     * ■관리자 등록 서비스 ■payletter ■2018-09-28
     --------------------------------------------------------------------**/
    public void insertAdministrator(Administrator administrator) throws Exception {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10, SecureRandom.getInstance(GlobalConstants.BCRYPT_ALGORITHM));

        administrator.setAdminPwd(passwordEncoder.encode(administrator.getAdminPwd()));

        // 관리자 등록
        if(1 != dao.insert("administrator.insertAdministrator", administrator)) {
            throw new GlobalException("administratorMgmt.msgInsAdminFail");
        }

        // 관리자 등록 후 비밀번호 초기화 관련 로직 실행
        resetAdministratorPwd(administrator);
    }

    /**--------------------------------------------------------------------
     * ■관리자 수정 서비스 ■payletter ■2018-09-28
     --------------------------------------------------------------------**/
    public void updateAdministrator(Administrator administrator) throws GlobalException, NoSuchAlgorithmException {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10, SecureRandom.getInstance(GlobalConstants.BCRYPT_ALGORITHM));

        administrator.setAdminPwd(passwordEncoder.encode(administrator.getAdminPwd()));

        // 관리자 수정
        if(1 != dao.update("administrator.updateAdministrator", administrator)) {
            throw new GlobalException("administratorMgmt.msgUpdAdminFail");
        }
    }

    /**--------------------------------------------------------------------
     * ■관리자 비밀번호 초기화 서비스 ■payletter ■2018-09-28
     --------------------------------------------------------------------**/
    public void resetAdministratorPwd(Administrator administrator) {
        String strDomain = strServerWebHost;
        String strToken  = CommonUtil.getUuid();
        administrator.setToken(strToken);

        // 관리자 비밀번호 초기화 정보 입력
        if(1 != dao.insert("administrator.insertAdministratorPwdReset", administrator)) {
            throw new GlobalException("administratorMgmt.msgUpdAdminPwdFail");
        }

        // 메일 발송 예제
        Context mailBodyContext = new Context();
        mailBodyContext.setVariable("adminID", administrator.getAdminID());
        mailBodyContext.setVariable("resetPasswordURL", strDomain + GlobalConstants.RESET_PWD_URI);
        mailBodyContext.setVariable("token", administrator.getToken());

        // 관리자 비밀번호 초기화 메일 전송
        sendMailService.sendMail(administrator.getEmail()
                                 ,GlobalConstants.FROM_MAIL_ADDRESS
                                 ,GlobalConstants.ADMIN_RESET_PWD_MAIL_SUBJECT
                                 ,"documents/mail/resetPassword.html"
                                 ,mailBodyContext);
    }

    /**--------------------------------------------------------------------
     * ■비밀번호 초기화 요청 처리 로직 ■payletter ■2018-09-28
     --------------------------------------------------------------------**/
    public String checkResetPwdRequest(String strToken) throws GlobalException {
        // 관리자 비밀번호 초기화 요청 정보 조회
        Administrator resetPwdAdminInfo = (Administrator) dao.selectOne("administrator.selectResetPwdAdminInfo", strToken);

        // 요청 정보 확인
        if(resetPwdAdminInfo == null) {
            throw new GlobalException("administratorMgmt.msgInfoInvalid", true);
        }

        // 기 인증 여부 확인
        if(resetPwdAdminInfo.getAuthFlag()) {
            throw new GlobalException("administratorMgmt.msgAlreadyAuthInfo", true);
        }

        // 인증 만료 시간 확인
        if(GlobalConstants.TOKEN_EXPIRE_VALID_TIME < new Date().getTime() - resetPwdAdminInfo.getAuthExpDate().getTime()) {
            throw new GlobalException("administratorMgmt.msgTokenTimeExpired", true);
        }

        return resetPwdAdminInfo.getAdminID();
    }

    /**--------------------------------------------------------------------
     * ■비밀번호 초기화 처리 로직 ■payletter ■2018-09-28
     --------------------------------------------------------------------**/
    @Transactional
    public void resetPwd(Administrator administrator) throws GlobalException, NoSuchAlgorithmException {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10, SecureRandom.getInstance(GlobalConstants.BCRYPT_ALGORITHM));

        administrator.setNewPwd(passwordEncoder.encode(administrator.getNewPwd()));

        // 비밀번호 변경 이력 등록
        if(1 != dao.insert("administrator.insertAdminPwdChangeHist", administrator)) {
            throw new GlobalException("administratorMgmt.msgInsPwdChangeHistFail");
        }

        // 신규 비밀번호 저장
        if(1 != dao.update("administrator.updateAdministratorByItem", administrator)) {
            throw new GlobalException("administratorMgmt.msgUpdAdminPwdFail");
        }

        // 비밀번호 초기화 정보 수정 (인증 정보)
        if(1 != dao.update("administrator.updateAdminPwdReset", administrator)) {
            throw new GlobalException("administratorMgmt.msgUpdPwdResetFail");
        }
    }

    /**--------------------------------------------------------------------
     * ■비밀번호 변경 처리 로직 ■payletter ■2018-09-28
     --------------------------------------------------------------------**/
    @Transactional
    public void changePwd(Administrator administrator) throws NoSuchAlgorithmException {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10, SecureRandom.getInstance(GlobalConstants.BCRYPT_ALGORITHM));

        administrator.setNewPwd(passwordEncoder.encode(administrator.getNewPwd()));

        String strAdminPwd = (String) dao.selectOne("administrator.selectAdminPwd", administrator.getAdminID());

        // 비밀번호 일치 여부 확인
        if(!passwordEncoder.matches(administrator.getCurrPwd(), strAdminPwd)) {
            throw new GlobalException("common.msg.wrongPassword", true);
        }

        // 기존 비밀번호와 동일 여부 확인
        if(passwordEncoder.matches(administrator.getReNewPwd(), strAdminPwd)) {
            throw new GlobalException("administratorMgmt.msgSameNewPwd", true);
        }

        // 비밀번호 변경 이력 등록
        if(1 != dao.insert("administrator.insertAdminPwdChangeHist", administrator)) {
            throw new GlobalException("administratorMgmt.msgInsPwdChangeHistFail");
        }

        // 신규 비밀번호, 비밀번호 변경 일자 저장
        if(1 != dao.update("administrator.updateAdministratorByItem", administrator)) {
            throw new GlobalException("administratorMgmt.msgUpdAdminPwdFail");
        }
    }
}
