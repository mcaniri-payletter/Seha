package com.sbank.admin.administrator.domain;

import com.sbank.admin.common.constants.Enums;
import com.sbank.admin.common.domain.BaseModel;
import com.sbank.admin.common.util.CommonUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**--------------------------------------------------------------------
 * ■로그 등록 요청 모델 ■payletter ■2018-09-28
 --------------------------------------------------------------------**/
@Data
@EqualsAndHashCode(callSuper=false)
public class AdminMenuAccessLog extends BaseModel {
    //------------------------------------------------------------------
    // 엔티티 베이스 멤버 변수(엔티티 컬럼 정렬 순서 순)
    //------------------------------------------------------------------
    private Integer seqNo;         // 로그 번호
    private Integer adminNo;       // 관리자 번호
    private Short   menuNo;        // 메뉴 번호
    private String  menuLink;      // 메뉴 링크
    private String  methodName;    // 메소드 명

    private String  logDesc;       // 로그 설명
    private String  ipAddr;        // 아이피 주소
    private String  regDate;       // 등록 일시
    private String  menuName;      // 메뉴 명
    private String  adminID;       // 관리자 아이디

    private byte    workType;      // 작업 유형(1:조회, 2:등록, 3:수정, 4:삭제, 5:엑셀다운로드)
    private String  workTypeName;  // 작업 유형 명

    //------------------------------------------------------------------
    // 엔티티 외 파라메터 변수
    //------------------------------------------------------------------
    private String  strFromYMD;    // 로그 검색용 (시작 일자)
    private String  strToYMD;      // 로그 검색용 (종료 일자)

    //----------------------------------------------------------------------
    // ■하루 지난 날짜로 변경 ■payletter ■2018. 7. 13.
    //----------------------------------------------------------------------
    public void changeLastYMD() {
        if(!CommonUtil.isNullOrEmpty(strToYMD)) {
            this.strToYMD = CommonUtil.addOneDay(strToYMD);
        }
    }

    public void convertWorkType() {
        if(!CommonUtil.isNullOrEmpty(methodName)) {
            if(methodName.contains("select")) {
                this.workType = Enums.WorkTypeCode.SELECT.getValue();
            }else if(methodName.contains("insert")) {
                this.workType = Enums.WorkTypeCode.INSERT.getValue();
            }else if(methodName.contains("update") || methodName.equals("resetAdministratorPwd") || methodName.contains("execute")) {
                this.workType = Enums.WorkTypeCode.UPDATE.getValue();
            }else if(methodName.contains("delete")) {
                this.workType = Enums.WorkTypeCode.DELETE.getValue();
            }else if(methodName.equals("excelDownload")) {
                this.workType = Enums.WorkTypeCode.EXCEL_DOWNLOAD.getValue();
            }else if(methodName.equals("initPage") || methodName.equals("loginProc")) {
                this.workType = Enums.WorkTypeCode.ETC.getValue();
            }else {
                this.workType = Enums.WorkTypeCode.ETC.getValue();
            }
        }
    }
}
