package com.sbank.admin.login.domain;

import com.sbank.admin.common.domain.BaseModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**--------------------------------------------------------------------
 * ■로그인 모델 (메일에서의 진입) ■payletter ■2018-09-28
 --------------------------------------------------------------------**/
@Data
@EqualsAndHashCode(callSuper=false)
public class Login extends BaseModel implements Serializable{
    private static final long serialVersionUID = 1L;

    private int     adminNo;           // 관리자 번호
    @NotBlank
    private String  adminID;           // 관리자 아이디
    @NotBlank
    private String  adminPwd;          // 관리자 비밀번호
    private String  useLanguageTypeCode;   // 사용 언어 타입 코드

    private String  lastLoginDate;     // 마지막 로그인 일시 (YYYY.MM.DD 24H:MI:SS)
    private String  lastLoginIP;       // 마지막 로그인 아이피
    private String  sessionKey;        // Session Key
    private String  loginMsg;          // 로그인 메시지
    private String  ipAddr;            // 로그인 아이피

    private String  menuLink;          // 최초 접근 메뉴
    private Boolean firstLoginFlag;    // 최초 로그인 여부
    private String  returnUrl;         // 반환 URL
}