package com.sbank.admin.customer.contoller;

import com.sbank.admin.common.annotation.AuthMethod;
import com.sbank.admin.common.constants.PathConstants;
import com.sbank.admin.common.domain.TypeCode;
import com.sbank.admin.common.service.CommonService;
import com.sbank.admin.common.service.ExcelService;
import com.sbank.admin.common.util.CommonUtil;
import com.sbank.admin.menuauthority.domain.Menu;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

/**--------------------------------------------------------------------
 * ■고객사 컨트롤러 ■yjyoo ■2021-07-26
 --------------------------------------------------------------------**/
@Controller
@RequestMapping(value = PathConstants.PATH_CONTROLLER_CUSTOMER)
public class CustomerController {
    @Autowired
    private CommonService commonService;

    @Autowired
    private ExcelService excelService;

    /**--------------------------------------------------------------------
     * ■고객사 페이지 진입 함수 ■yjyoo ■2021-07-26
     --------------------------------------------------------------------**/
    @AuthMethod(hasAuth = CommonUtil.UserAuth.READONLY)
    @RequestMapping(value = "/customer")
    public ModelAndView initPage() {
        ModelAndView mav = new ModelAndView(PathConstants.PATH_VIEW_CUSTOMER);

        List<TypeCode> searchTypeList = commonService.selectCodeList("ddlb.selectLanguageCodeList");
        mav.addObject("searchTypeList", searchTypeList);

        return mav;
    }

    /**--------------------------------------------------------------------
     * ■고객사 편집 페이지 진입 함수 ■yjyoo ■2021-07-26
     --------------------------------------------------------------------**/
    @AuthMethod(hasAuth = CommonUtil.UserAuth.READONLY)
    @RequestMapping(value = "/customerEdit")
    public ModelAndView initPageEdit(Menu menu) {
        ModelAndView mav = new ModelAndView(PathConstants.PATH_VIEW_CUSTOMER_EDIT);

        mav.addObject("EDIT_MODE", menu.getEditMode());

        return mav;
    }
}
