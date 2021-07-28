package com.sbank.admin.dashboard.controller;

import com.sbank.admin.common.annotation.AuthMethod;
import com.sbank.admin.common.constants.PathConstants;
import com.sbank.admin.common.service.CommonService;
import com.sbank.admin.common.util.CommonUtil;
import com.sbank.admin.dashboard.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**--------------------------------------------------------------------
 * ■대시보드 컨트롤러 ■payletter ■2020-04-13
 --------------------------------------------------------------------**/
@Controller
@RequestMapping(value = PathConstants.PATH_CONTROLLER_DASHBOARD)
public class DashboardController {
    @Autowired
    CommonService commonService;

    @Autowired
    DashboardService dashboardService;

    /**--------------------------------------------------------------------
     * ■대시보드 페이지 진입 함수 ■payletter ■2020-04-13
     --------------------------------------------------------------------**/
    @AuthMethod(hasAuth = CommonUtil.UserAuth.READONLY)
    @RequestMapping(value = "/dashboard")
    public ModelAndView initPage() {
        ModelAndView mav = new ModelAndView(PathConstants.PATH_VIEW_DASHBOARD);
        mav.addObject("dashboard", dashboardService.selectDashboard());
        return mav;
    }
}
