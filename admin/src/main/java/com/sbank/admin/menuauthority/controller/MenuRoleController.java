package com.sbank.admin.menuauthority.controller;

import com.sbank.admin.common.annotation.AuthMethod;
import com.sbank.admin.common.constants.PathConstants;
import com.sbank.admin.common.exception.GlobalException;
import com.sbank.admin.menuauthority.domain.MenuRole;
import com.sbank.admin.menuauthority.service.MenuRoleService;
import com.sbank.admin.common.domain.SessionInfo;
import com.sbank.admin.common.util.CommonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**--------------------------------------------------------------------
 * ■메뉴 역할 컨트롤러 ■payletter ■2018-09-28
 --------------------------------------------------------------------**/
@Controller
@RequestMapping(value = PathConstants.PATH_CONTROLLER_MENU_ROLE)
public class MenuRoleController {
    @Autowired
    private MenuRoleService meneRoleService;

    /**--------------------------------------------------------------------
     * ■메뉴 역할 페이지 진입 ■payletter ■2018-09-28
     --------------------------------------------------------------------**/
    @AuthMethod(hasAuth = CommonUtil.UserAuth.READONLY)
    @RequestMapping(value = "/menuRole")
    public String initPage() {
        return PathConstants.PATH_VIEW_MENU_ROLE;
    }

    /**--------------------------------------------------------------------
     * ■메뉴 역할 목록 조회 ■payletter ■2018-09-28
     --------------------------------------------------------------------**/
    @AuthMethod(hasAuth = CommonUtil.UserAuth.READONLY)
    @PostMapping(value = "/selectMenuRoleList")
    public ModelAndView selectMenuRoleList() throws GlobalException {
        return CommonUtil.getResJsonView(meneRoleService.selectMenuRoleList());
    }

    /**--------------------------------------------------------------------
     * ■메뉴 역할 상세 목록 조회 ■payletter ■2018-09-28
     --------------------------------------------------------------------**/
    @AuthMethod(hasAuth = CommonUtil.UserAuth.READONLY)
    @PostMapping(value = "/selectMenuRoleDtlList")
    public ModelAndView selectMenuRoleDtlList(@RequestBody MenuRole menuRole) throws GlobalException {
        return CommonUtil.getResJsonView(meneRoleService.selectMenuRoleDtlList(menuRole));
    }

    /**--------------------------------------------------------------------
     * ■메뉴 역할 등록/수정 ■payletter ■2018-09-28
     --------------------------------------------------------------------**/
    @AuthMethod(hasAuth = CommonUtil.UserAuth.ALL)
    @PostMapping(value = "/mergeMenuRole")
    public ModelAndView mergeMenuRole(@Validated @RequestBody MenuRole menuRole, SessionInfo sessionInfo) throws GlobalException {
    	menuRole.setAdminNo(sessionInfo.getAdminNo());
        meneRoleService.mergeMenuRole(menuRole);
        return CommonUtil.getResJsonView();
    }
}
