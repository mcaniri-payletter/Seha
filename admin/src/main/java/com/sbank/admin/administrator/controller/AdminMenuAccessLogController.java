package com.sbank.admin.administrator.controller;

import com.sbank.admin.common.annotation.AuthMethod;
import com.sbank.admin.common.constants.GlobalConstants;
import com.sbank.admin.common.constants.PathConstants;
import com.sbank.admin.common.exception.GlobalException;
import com.sbank.admin.administrator.domain.AdminMenuAccessLog;
import com.sbank.admin.administrator.service.AdminMenuAccessLogService;
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
 * ■관리자 로그 컨트롤러 ■payletter ■2018-09-28
 --------------------------------------------------------------------**/
@Controller
@RequestMapping(value = PathConstants.PATH_CONTROLLER_ADMIN_MENU_ACCESS_LOG)
public class AdminMenuAccessLogController {
    @Autowired
    private AdminMenuAccessLogService adminMenuAccessLogService;

    @Autowired
    private ExcelService excelService;

    /**--------------------------------------------------------------------
     * ■관리자 로그 페이지 진입 함수 ■payletter ■2018-09-28
     --------------------------------------------------------------------**/
    @AuthMethod(hasAuth = CommonUtil.UserAuth.READONLY)
    @RequestMapping(value = "/adminMenuAccessLog")
    public String initPage() {
        return PathConstants.PATH_VIEW_ADMIN_MENU_ACCESS_LOG;
    }

    /**--------------------------------------------------------------------
     * ■관리자 로그 목록 조회 함수 ■payletter ■2018-09-28
     --------------------------------------------------------------------**/
    @AuthMethod(hasAuth = CommonUtil.UserAuth.READONLY)
    @PostMapping(value = "/selectAdminMenuAccessLogList")
    public ModelAndView selectAdminMenuAccessLogList(@RequestBody AdminMenuAccessLog adminMenuAccessLog) throws GlobalException {
        List<AdminMenuAccessLog> lstAdminMenuAccessLog = adminMenuAccessLogService.selectAdminMenuAccessLogList(adminMenuAccessLog);
        return CommonUtil.getResJsonView(lstAdminMenuAccessLog, adminMenuAccessLog);
    }

    /**--------------------------------------------------------------------
     * ■관리자 로그 목록 엑셀 다운로드 ■payletter ■2018-09-28
     --------------------------------------------------------------------**/
    @AuthMethod(hasAuth = CommonUtil.UserAuth.READONLY)
    @RequestMapping(value = "/excelDownload")
    public void excelDownload(HttpServletResponse response, AdminMenuAccessLog adminMenuAccessLog) throws Exception {
        adminMenuAccessLog.setExcelFlag(true);
        List<AdminMenuAccessLog> lstAdminMenuAccessLog = adminMenuAccessLogService.selectAdminMenuAccessLogList(adminMenuAccessLog);
        Map<String, Object> infos = new HashMap<String, Object>();
        infos.put(GlobalConstants.EXCELDOWNLOAD_DATA_LIST, lstAdminMenuAccessLog);
        infos.put(GlobalConstants.EXCELDOWNLOAD_TEMPLATE_FILE, GlobalConstants.EXCEL_TEMPLATE_ADMIN_MENU_ACCESS_LOG);
        excelService.excelDownload(response, infos);
    }
}
