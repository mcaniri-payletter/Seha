package com.sbank.admin.menuauthority.service;

import com.sbank.admin.common.exception.GlobalException;
import com.sbank.admin.common.repository.GenericDAO;
import com.sbank.admin.common.service.CommonService;
import com.sbank.admin.menuauthority.domain.MenuGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.NoSuchMessageException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**--------------------------------------------------------------------
 * ■메뉴 그룹 인터페이스 구현부 ■payletter ■2018-09-28
 --------------------------------------------------------------------**/
@Service
public class MenuGroupService {
    @Autowired
    @Qualifier("mainDB")
    private GenericDAO<Object, Object> dao;

    @Autowired
    private CommonService commonService;

    /**--------------------------------------------------------------------
     * ■메뉴 그룹 목록 조회 ■payletter ■2018-09-28
     --------------------------------------------------------------------**/
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public List<MenuGroup> selectMenuGroupList(MenuGroup menuGroup) throws GlobalException {
        List<MenuGroup> menuGroupList = (List) dao.selectList("menuGroup.selectMenuGroupList", menuGroup);

        for (MenuGroup obj : menuGroupList) {
            try {
                obj.setMenuGroupNameTrans(commonService.getMessage(obj.getMenuGroupName()));
            } catch (NoSuchMessageException ignore) {}
        }

        return menuGroupList;
    }

    /**--------------------------------------------------------------------
     * ■메뉴 그룹 등록/수정 ■payletter ■2018-09-28
     --------------------------------------------------------------------**/
    @Transactional
    public void mergeMenuGroupList(MenuGroup menuGroup) throws GlobalException {
        dao.update("menuGroup.mergeMenuGroup", menuGroup);
    }

    /**--------------------------------------------------------------------
     * ■메뉴 그룹 목록 수정 ■payletter ■2018-09-28
     --------------------------------------------------------------------**/
    @Transactional
    public void updateMenuGroupList(MenuGroup menuGroup) throws GlobalException {
        for (MenuGroup obj : menuGroup.getMenuGroupList()) {
            dao.batchUpdate("menuGroup.updateMenuGroup", obj);
        }
    }
}
