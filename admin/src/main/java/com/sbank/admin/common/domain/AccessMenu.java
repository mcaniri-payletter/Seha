package com.sbank.admin.common.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**--------------------------------------------------------------------
 * ■접근 메뉴 ■payletter ■2018-09-28
 --------------------------------------------------------------------**/
@Data
@EqualsAndHashCode(callSuper=false)
public class AccessMenu extends BaseModel {
    // 요청
    private String  adminID;
    private String  menuDivSgmnt;

    // 응답
    private Short   menuGroupNo;
    private String  menuGroupName;
    private Boolean menuGroupUseFlag;
    private Short   menuNo;
    private String  menuName;

    private Boolean menuUseFlag;
    private Boolean menuRoleUseFlag;
    private Byte    authCode;
    private Boolean ciReadFlag;
    private Boolean dnAvailFlag;
}
