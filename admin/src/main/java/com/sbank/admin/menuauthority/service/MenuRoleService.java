package com.sbank.admin.menuauthority.service;

import com.sbank.admin.common.exception.GlobalException;
import com.sbank.admin.common.repository.GenericDAO;
import com.sbank.admin.common.service.CommonService;
import com.sbank.admin.menuauthority.domain.MenuRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.NoSuchMessageException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**--------------------------------------------------------------------
 * ■메뉴 역할 인터페이스 구현부 ■payletter ■2018-09-28
 --------------------------------------------------------------------**/
@Service
public class MenuRoleService {
    @Autowired
    @Qualifier("mainDB")
    private GenericDAO<Object, Object> dao;

    @Autowired
    private CommonService commonService;

    /**--------------------------------------------------------------------
     * ■메뉴 역할 목록 조회 ■payletter ■2018-09-28
     --------------------------------------------------------------------**/
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public List<MenuRole> selectMenuRoleList() throws GlobalException {
        return (List) dao.selectList("menuRole.selectMenuRoleList");
    }

    /**--------------------------------------------------------------------
     * ■메뉴 역할 상세 목록 조회 ■payletter ■2018-09-28
     --------------------------------------------------------------------**/
    @SuppressWarnings("unchecked")
    public List<MenuRole> selectMenuRoleDtlList(MenuRole menuRole) throws GlobalException {
        List<MenuRole> menuRoleList = (List)dao.selectList("menuRole.selectMenuRoleDtl", menuRole);

        for (MenuRole obj : menuRoleList) {
            try {
                obj.setMenuName(commonService.getMessage(obj.getMenuName()));
            } catch (NoSuchMessageException ignore) {}
        }

        return menuRoleList;
    }

    /**--------------------------------------------------------------------
     * ■메뉴 역할 등록/수정 ■payletter ■2018-09-28
     --------------------------------------------------------------------**/
    @Transactional
    public void mergeMenuRole(MenuRole menuRole) throws GlobalException {
        // 메뉴 역할 상세 삭제
        dao.delete("menuRole.deleteMenuRoleDtl", menuRole);

        // 메뉴 역할 Merge
        dao.update("menuRole.mergeMenuRole", menuRole);

        // 메뉴 역할 상세 등록
        for (MenuRole obj : menuRole.getMenuRoleList()) {
            if (obj.getMenuRoleNo() == 0) {
                obj.setMenuRoleNo(menuRole.getMenuRoleNo());
            }

            dao.insert("menuRole.insertMenuRoleDtl", obj);
        }
    }
}
