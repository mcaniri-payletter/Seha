package com.sbank.admin.menuauthority.service;

import com.sbank.admin.common.exception.GlobalException;
import com.sbank.admin.common.repository.GenericDAO;
import com.sbank.admin.common.service.CommonService;
import com.sbank.admin.menuauthority.domain.Menu;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.NoSuchMessageException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**--------------------------------------------------------------------
 * ■메뉴 인터페이스 구현부 ■payletter ■2018-09-28
 --------------------------------------------------------------------**/
@Service
public class MenuService {
    @Autowired
    @Qualifier("mainDB")
    private GenericDAO<Object, Object> dao;

    @Autowired
    private CommonService commonService;

    /**--------------------------------------------------------------------
     * ■메뉴 목록 조회 서비스 ■payletter ■2018-09-28
     --------------------------------------------------------------------**/
    @SuppressWarnings("unchecked")
    public List<Menu> selectMenuList(Menu menu) throws GlobalException {
        List<Menu> menuList = (List) dao.selectList("menu.selectMenuList", menu);

        for (Menu obj : menuList) {
            try {
                obj.setMenuNameTrans(commonService.getMessage(obj.getMenuName()));
            } catch (NoSuchMessageException ignore) {}
        }

        return menuList;
    }

    /**--------------------------------------------------------------------
     * ■메뉴 등록/수정 서비스 ■payletter ■2018-09-28
     --------------------------------------------------------------------**/
    public void mergeMenu(Menu menu) throws GlobalException {
        dao.update("menu.mergeMenu", menu);
    }

    /**--------------------------------------------------------------------
     * ■메뉴 목록 수정 서비스 ■payletter ■2018-09-28
     --------------------------------------------------------------------**/
    @Transactional
    public void updateMenuList(Menu menu) throws GlobalException {
        for (Menu obj : menu.getMenuList()) {
            dao.batchUpdate("menu.updateMenuList", obj);
        }
    }
}
