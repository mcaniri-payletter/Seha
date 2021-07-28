package com.sbank.admin.administrator.controller;

import com.sbank.admin.administrator.service.AdminLoginFailHistService;
import com.sbank.admin.common.annotation.AuthMethod;
import com.sbank.admin.common.constants.GlobalConstants;
import com.sbank.admin.common.constants.PathConstants;
import com.sbank.admin.common.exception.GlobalException;
import com.sbank.admin.administrator.domain.AdminLoginFailHist;
import com.sbank.admin.common.service.ExcelService;
import com.sbank.admin.common.util.CommonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**--------------------------------------------------------------------
 * ■관리자 로그인 실패 이력 컨트롤러 ■payletter ■2018-09-28
 --------------------------------------------------------------------**/
@Controller
@RequestMapping(value = PathConstants.PATH_CONTROLLER_ADMIN_LOGIN_FAIL_HIST)
public class AdminLoginFailHistController {
    @Autowired
    private AdminLoginFailHistService adminLoginFailHistService;

    @Autowired
    private ExcelService excelService;

    /**--------------------------------------------------------------------
     * ■로그인 실패 이력 페이지 진입 함수 ■payletter ■2018-09-28
     --------------------------------------------------------------------**/
    @AuthMethod(hasAuth = CommonUtil.UserAuth.READONLY)
    @RequestMapping(value = "/adminLoginFailHist")
    public String initPage() {
        return PathConstants.PATH_VIEW_ADMIN_LOGIN_FAIL_HIST;
    }

    /**--------------------------------------------------------------------
     * ■로그인 실패 이력 목록 조회 함수 ■payletter ■2018-09-28
     --------------------------------------------------------------------**/
    @AuthMethod(hasAuth = CommonUtil.UserAuth.READONLY)
    @PostMapping(value = "/selectAdminLoginFailHistList")
    public ModelAndView selectAdminLoginFailHistList(@RequestBody AdminLoginFailHist adminLoginFailHist) throws GlobalException {
        List<AdminLoginFailHist> lstAdminLoginFailHist = adminLoginFailHistService.selectAdminLoginFailHistList(adminLoginFailHist);
        return CommonUtil.getResJsonView(lstAdminLoginFailHist, adminLoginFailHist);
    }

    /**--------------------------------------------------------------------
     * ■관리자 로그인 실패 이력 목록 엑셀 다운로드 ■payletter ■2018-09-28
     --------------------------------------------------------------------**/
    @AuthMethod(hasAuth = CommonUtil.UserAuth.READONLY)
    @RequestMapping(value = "/excelDownload")
    public void excelDownload(HttpServletResponse response, AdminLoginFailHist adminLoginFailHist) throws Exception {
        adminLoginFailHist.setExcelFlag(true);
        List<AdminLoginFailHist> lstAdminLoginFailHist = adminLoginFailHistService.selectAdminLoginFailHistList(adminLoginFailHist);
        Map<String, Object> infos = new HashMap<String, Object>();
        infos.put(GlobalConstants.EXCELDOWNLOAD_DATA_LIST, lstAdminLoginFailHist);
        infos.put(GlobalConstants.EXCELDOWNLOAD_TEMPLATE_FILE, GlobalConstants.EXCEL_TEMPLATE_ADMIN_LOGIN_FAIL_HIST);
        excelService.excelDownload(response, infos);
    }
}
