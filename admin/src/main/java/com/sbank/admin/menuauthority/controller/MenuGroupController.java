package com.sbank.admin.menuauthority.controller;

import com.sbank.admin.common.annotation.AuthMethod;
import com.sbank.admin.common.constants.PathConstants;
import com.sbank.admin.common.exception.GlobalException;
import com.sbank.admin.menuauthority.domain.MenuGroup;
import com.sbank.admin.menuauthority.service.MenuGroupService;
import com.sbank.admin.common.domain.ValidationGroups;
import com.sbank.admin.common.util.CommonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**--------------------------------------------------------------------
 * ■메뉴 그룹 컨트롤러 ■payletter ■2018-09-28
 --------------------------------------------------------------------**/
@Controller
@RequestMapping(value = PathConstants.PATH_CONTROLLER_MENU_GROUP)
public class MenuGroupController {
    @Autowired
    private MenuGroupService menuGroupService;

    /**--------------------------------------------------------------------
     * ■메뉴 그룹 페이지 진입 함수 ■payletter ■2018-09-28
     --------------------------------------------------------------------**/
    @AuthMethod(hasAuth = CommonUtil.UserAuth.READONLY)
    @RequestMapping(value = "/menuGroup")
    public String initPage() {
        return PathConstants.PATH_VIEW_MENU_GROUP;
    }

    /**--------------------------------------------------------------------
     * ■메뉴 그룹 조회 함수 ■payletter ■2018-09-28
     --------------------------------------------------------------------**/
    @AuthMethod(hasAuth = CommonUtil.UserAuth.READONLY)
    @PostMapping(value = "/selectMenuGroupList")
    public ModelAndView selectMenuGroupList(@RequestBody MenuGroup menuGroup) throws GlobalException {
        return CommonUtil.getResJsonView(menuGroupService.selectMenuGroupList(menuGroup));
    }

    /**--------------------------------------------------------------------
     * ■메뉴 그룹 등록/수정 ■payletter ■2018-09-28
     --------------------------------------------------------------------**/
    @AuthMethod(hasAuth = CommonUtil.UserAuth.ALL)
    @PostMapping(value = "/mergeMenuGroupList")
    public ModelAndView mergeMenuGroupList(@Validated(value = ValidationGroups.Insert.class) @RequestBody MenuGroup menuGroup) throws GlobalException {
        menuGroupService.mergeMenuGroupList(menuGroup);
        return CommonUtil.getResJsonView();
    }

    /**--------------------------------------------------------------------
     * ■메뉴 그룹 목록 수정 ■payletter ■2018-09-28
     --------------------------------------------------------------------**/
    @AuthMethod(hasAuth = CommonUtil.UserAuth.ALL)
    @PostMapping(value = "/updateMenuGroupList")
    public ModelAndView updateMenuGroupList(@Validated(value = ValidationGroups.Update.class) @RequestBody MenuGroup menuGroup) throws GlobalException {
        menuGroupService.updateMenuGroupList(menuGroup);
        return CommonUtil.getResJsonView();
    }
}
