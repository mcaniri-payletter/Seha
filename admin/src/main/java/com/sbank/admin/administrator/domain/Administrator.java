package com.sbank.admin.administrator.domain;

import com.sbank.admin.common.domain.BaseModel;
import com.sbank.admin.common.domain.ValidationGroups;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;
import java.util.Date;

/**--------------------------------------------------------------------
 * ■관리자 페이지 모델 ■payletter ■2018-09-28
 --------------------------------------------------------------------**/
@Data
@EqualsAndHashCode(callSuper=false)
public class Administrator extends BaseModel {
    //------------------------------------------------------------------
    // 엔티티 베이스 멤버 변수(엔티티 컬럼 정렬 순서 순)
    //------------------------------------------------------------------
    @NotNull(groups = ValidationGroups.ResetMail.class)
    private Integer adminNo;               // 관리자 번호
    @NotBlank
    private String  adminID;               // 관리자 아이디
    private String  adminPwd;              // 관리자 비밀번호
    @NotBlank(groups = {ValidationGroups.Insert.class, ValidationGroups.Update.class})
    private String  adminName;             // 관리자 명
    @NotNull(groups = {ValidationGroups.Insert.class, ValidationGroups.Update.class})
    private Short   menuRoleNo;            // 메뉴 역할 번호

    @NotBlank(groups = {ValidationGroups.Insert.class, ValidationGroups.Update.class})
    private String  celNo;                 // 휴대폰 번호
    @NotBlank(groups = {ValidationGroups.Insert.class, ValidationGroups.Update.class, ValidationGroups.ResetMail.class})
    private String  email;                 // 이메일
    private String  useLanguageTypeCode;   // 사용 언어 타입 코드
    @NotNull(groups = {ValidationGroups.Insert.class, ValidationGroups.Update.class})
    private Boolean accessIPRestrictFlag;  // 접근 아이피 제한 여부 (0:미제한, 1:제한)
    private String  accessIP1;             // 접근 아이피1

    private String  accessIP2;             // 접근 아이피2
    private String  accessIP3;             // 접근 아이피3
    private String  lastLoginDate;         // 마지막 로그인 일시
    private String  lastLoginIP;           // 마지막 로그인 아이피
    private String  pwdUpdDate;            // 최근 비밀번호 변경 일시

    private Timestamp dtPwdUpdDate;        // 최근 비밀번호 변경 일시
    private Integer regAdminNo;            // 등록 관리자 번호
    @NotNull(groups = {ValidationGroups.Insert.class, ValidationGroups.Update.class})
    private Boolean useFlag;               // 사용 여부 (0:미사용, 1:사용)
    private String  regDate;               // 등록 일시
    private String  updDate;               // 수정 일시

    private String  menuRoleName;          // 메뉴 역할 명
    private String  regAdminName;          // 등록 관리자 명

    //------------------------------------------------------------------
    // 비밀번호 초기화 관련
    //------------------------------------------------------------------
    @NotBlank(groups = ValidationGroups.Reset.class)
    private String  token;                 // 토큰
    private Boolean authFlag;              // 인증 여부 (0:미인증, 1:인증)
    private Date    authExpDate;           // 인증 만료 일시
    @NotBlank(groups = {ValidationGroups.Reset.class, ValidationGroups.ChangePwd.class})
    private String  newPwd;                // 신규 비밀번호
    @NotBlank(groups = {ValidationGroups.Reset.class, ValidationGroups.ChangePwd.class})
    private String  reNewPwd;              // 신규 비밀번호 재입력
    @NotBlank(groups = ValidationGroups.ChangePwd.class)
    private String  currPwd;               // 현재 비밀번호

    //------------------------------------------------------------------
    // 관리자 정보 확인 (로그인 시)
    //------------------------------------------------------------------
    private Boolean chkAdminFlag;          // 관리자 로그인 확인 여부 (0:미확인, 1:확인)
}