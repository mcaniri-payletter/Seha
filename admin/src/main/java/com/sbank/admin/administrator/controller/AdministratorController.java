package com.sbank.admin.administrator.controller;

import com.sbank.admin.administrator.service.AdministratorService;
import com.sbank.admin.common.annotation.AuthMethod;
import com.sbank.admin.common.constants.GlobalConstants;
import com.sbank.admin.common.constants.PathConstants;
import com.sbank.admin.administrator.domain.Administrator;
import com.sbank.admin.common.domain.SessionInfo;
import com.sbank.admin.common.domain.ValidationGroups;
import com.sbank.admin.common.service.ExcelService;
import com.sbank.admin.common.util.CommonUtil;
import com.sbank.admin.menuauthority.domain.MenuRole;
import com.sbank.admin.menuauthority.service.MenuRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**--------------------------------------------------------------------
 * ■관리자 컨트롤러 ■payletter ■2018-09-28
 --------------------------------------------------------------------**/
@Controller
@RequestMapping(value = PathConstants.PATH_CONTROLLER_ADMINISTRATOR)
public class AdministratorController {
    @Autowired
    private AdministratorService administratorService;

    @Autowired
    private ExcelService excelService;

    @Autowired
    private MenuRoleService menuRoleService;

    /**--------------------------------------------------------------------
     * ■관리자 페이지 진입 함수 ■payletter ■2018-09-28
     --------------------------------------------------------------------**/
    @AuthMethod(hasAuth = CommonUtil.UserAuth.READONLY)
    @RequestMapping(value = "/administrator")
    public ModelAndView initPage() {
        ModelAndView mav = new ModelAndView(PathConstants.PATH_VIEW_ADMINISTRATOR);
        List<MenuRole> menuRoleList =  menuRoleService.selectMenuRoleList();
        mav.addObject("menuRoleList", menuRoleList);
        return mav;
    }

    /**--------------------------------------------------------------------
     * ■관리자 목록 조회 함수 ■payletter ■2018-09-28
     --------------------------------------------------------------------**/
    @AuthMethod(hasAuth = CommonUtil.UserAuth.READONLY)
    @PostMapping(value = "/selectAdministratorList")
    public ModelAndView selectAdministratorList(@RequestBody Administrator administrator) {
        List<Administrator> lstAdministrator =  administratorService.selectAdministratorList(administrator);
        return CommonUtil.getResJsonView(lstAdministrator, administrator);
    }

    /**--------------------------------------------------------------------
     * ■관리자 등록 함수 ■payletter ■2018-09-28
     --------------------------------------------------------------------**/
    @AuthMethod(hasAuth = CommonUtil.UserAuth.ALL)
    @PostMapping(value = "/insertAdministrator")
    public ModelAndView insertAdministrator(@Validated(value = ValidationGroups.Insert.class) @RequestBody Administrator administrator, SessionInfo sessionInfo) throws Exception {
        administrator.setRegAdminNo(sessionInfo.getAdminNo());
        administratorService.insertAdministrator(administrator);
        return CommonUtil.getResJsonView();
    }

    /**--------------------------------------------------------------------
     * ■관리자 수정 함수 ■payletter ■2018-09-28
     --------------------------------------------------------------------**/
    @AuthMethod(hasAuth = CommonUtil.UserAuth.ALL)
    @PostMapping(value = "/updateAdministrator")
    public ModelAndView updateAdministrator(@Validated(value = ValidationGroups.Update.class) @RequestBody Administrator administrator, SessionInfo sessionInfo) throws Exception {
        administrator.setRegAdminNo(sessionInfo.getAdminNo());
        administratorService.updateAdministrator(administrator);
        return CommonUtil.getResJsonView();
    }

    /**--------------------------------------------------------------------
     * ■관리자 목록 엑셀 다운로드 ■payletter ■2018-09-28
     --------------------------------------------------------------------**/
    @AuthMethod(hasAuth = CommonUtil.UserAuth.READONLY)
    @RequestMapping(value = "/excelDownload")
    public void excelDownload(HttpServletResponse response, Administrator administrator) throws Exception {
        administrator.setExcelFlag(true);
        List<Administrator> lstAdministrator = administratorService.selectAdministratorList(administrator);
        Map<String, Object> infos = new HashMap<>();
        infos.put(GlobalConstants.EXCELDOWNLOAD_DATA_LIST, lstAdministrator);
        infos.put(GlobalConstants.EXCELDOWNLOAD_TEMPLATE_FILE, GlobalConstants.EXCEL_TEMPLATE_ADMINISTRATOR);
        excelService.excelDownload(response, infos, new String[]{"관리자번호"});
    }

    /**--------------------------------------------------------------------
     * ■관리자 비밀번호 초기화 메일 전송 함수 (관리자 계정 관리 메뉴) ■payletter ■2018-09-28
     --------------------------------------------------------------------**/
    @AuthMethod(hasAuth = CommonUtil.UserAuth.ALL)
    @PostMapping(value = "/resetAdministratorPwd")
    public ModelAndView resetAdministratorPwd(@Validated(value = ValidationGroups.ResetMail.class) @RequestBody Administrator administrator) {
        administratorService.resetAdministratorPwd(administrator);
        return CommonUtil.getResJsonView();
    }

    /**--------------------------------------------------------------------
     * ■관리자 비밀번호 초기화 처리 함수 ■payletter ■2018-09-28
     --------------------------------------------------------------------**/
    @PostMapping(value = "/resetPwd")
    public ModelAndView resetPwd(@Validated(value = ValidationGroups.Reset.class) @RequestBody Administrator administrator, SessionInfo sessionInfo) throws Exception {
        administrator.setAdminNo(sessionInfo.getAdminNo());
        administrator.setRegAdminNo(administrator.getAdminNo());
        administratorService.resetPwd(administrator);
        return CommonUtil.getResJsonView();
    }

    /**--------------------------------------------------------------------
     * ■비밀번호 변경 처리 함수 ■payletter ■2018-09-28
     --------------------------------------------------------------------**/
    @PostMapping(value = "/changePwd")
    public ModelAndView changePwd(@RequestBody Administrator administrator) throws Exception {
        int     intResultCode    = GlobalConstants.COMMON_SUCCEED_CODE;
        String  strResultMessage = "";
        administratorService.changePwd(administrator);
        return CommonUtil.getResJsonView(intResultCode, strResultMessage);
    }
}
