package com.sbank.admin.common.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.LinkedHashMap;

/**--------------------------------------------------------------------
 * ■관리자 메뉴 정보 모델 ■payletter ■2018-09-28
 --------------------------------------------------------------------**/
@Data
@EqualsAndHashCode(callSuper=false)
public class AdminMenu extends BaseModel {
    private Short   menuGroupNo;
    private String  menuGroupName;
    private String  menuGroupIcon;
    private Short   menuGroupSortNo;
    private Short   menuNo;
    private String  menuLink;
    private Short   depth;
    private Short   menuSortNo;
    private Boolean useFlag;
    private Byte    authCode;

    private String  menuURISgmnt;

    private LinkedHashMap<Integer, AdminMenu> subMenu;

    public LinkedHashMap<Integer, AdminMenu> getSubMenu() {
        if (subMenu == null) {
            subMenu = new LinkedHashMap<>();
        }
        return subMenu;
    }
}
