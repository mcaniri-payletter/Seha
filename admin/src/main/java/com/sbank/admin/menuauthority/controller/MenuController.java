package com.sbank.admin.menuauthority.controller;

import com.sbank.admin.common.annotation.AuthMethod;
import com.sbank.admin.common.constants.PathConstants;
import com.sbank.admin.common.exception.GlobalException;
import com.sbank.admin.common.domain.SessionInfo;
import com.sbank.admin.common.domain.TypeCode;
import com.sbank.admin.common.domain.ValidationGroups;
import com.sbank.admin.common.service.CommonService;
import com.sbank.admin.common.util.CommonUtil;
import com.sbank.admin.menuauthority.domain.Menu;
import com.sbank.admin.menuauthority.service.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.NoSuchMessageException;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

/**--------------------------------------------------------------------
 * ■메뉴 컨트롤러 ■payletter ■2018-09-28
 --------------------------------------------------------------------**/
@Controller
@RequestMapping(value = PathConstants.PATH_CONTROLLER_MENU)
public class MenuController {

    @Autowired
    private MenuService menuService;

    @Autowired
    CommonService commonService;

    /**--------------------------------------------------------------------
     * ■메뉴 페이지 진입 함수 ■payletter ■2018-09-28
     --------------------------------------------------------------------**/
    @AuthMethod(hasAuth = CommonUtil.UserAuth.READONLY)
    @RequestMapping(value = "/menu")
    public ModelAndView initPage() {
        ModelAndView mav = new ModelAndView(PathConstants.PATH_VIEW_MENU);
        List<TypeCode> menuGroupList = commonService.selectCodeList("ddlb.selectMenuGroupList");

        for (TypeCode obj : menuGroupList) {
            try {
                obj.setCodeName(commonService.getMessage(obj.getMessageCode()));
            } catch (NoSuchMessageException ignore) {}
        }

        mav.addObject("menuGroupList", menuGroupList);
        return mav;
    }

    /**--------------------------------------------------------------------
     * ■메뉴 목록 조회 함수 ■payletter ■2018-09-28
     --------------------------------------------------------------------**/
    @AuthMethod(hasAuth = CommonUtil.UserAuth.READONLY)
    @PostMapping(value = "/selectMenuList")
    public ModelAndView selectMenuList(@RequestBody Menu menu) throws GlobalException {
        return CommonUtil.getResJsonView(menuService.selectMenuList(menu));
    }

    /**--------------------------------------------------------------------
     * ■메뉴 등록/수정 함수 ■payletter ■2018-09-28
     --------------------------------------------------------------------**/
    @AuthMethod(hasAuth = CommonUtil.UserAuth.ALL)
    @PostMapping(value = "/mergeMenu")
    public ModelAndView mergeMenu(@Validated(value = ValidationGroups.Insert.class) @RequestBody Menu menu, SessionInfo sessionInfo) throws GlobalException {
        menu.setAdminNo(sessionInfo.getAdminNo());
        menuService.mergeMenu(menu);
        return CommonUtil.getResJsonView();
    }

    /**--------------------------------------------------------------------
     * ■메뉴 목록 수정 함수 ■payletter ■2018-09-28
     --------------------------------------------------------------------**/
    @AuthMethod(hasAuth = CommonUtil.UserAuth.ALL)
    @PostMapping(value = "/updateMenuList")
    public ModelAndView updateMenuList(@Validated(value = ValidationGroups.Update.class) @RequestBody Menu menu) throws GlobalException {
        menuService.updateMenuList(menu);
        return CommonUtil.getResJsonView();
    }
}
