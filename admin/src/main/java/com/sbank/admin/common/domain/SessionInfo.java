package com.sbank.admin.common.domain;

import lombok.Data;

/**--------------------------------------------------------------------
 * ■세션 정보 모델 ■payletter ■2018-09-28
 --------------------------------------------------------------------**/
@Data
public class SessionInfo {
    private Integer adminNo;
    private String  adminID;
    private Short   menuGroupNo;
    private String  menuGroupName;
    private Short   menuNo;

    private String  menuName;
    private Byte    authCode;
    private String  ipAddr;
    private Boolean ciReadFlag;
    private Boolean dnAvailFlag;
}
