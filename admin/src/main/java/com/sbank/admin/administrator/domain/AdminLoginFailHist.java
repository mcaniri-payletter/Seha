package com.sbank.admin.administrator.domain;

import com.sbank.admin.common.domain.BaseModel;
import com.sbank.admin.common.util.CommonUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**--------------------------------------------------------------------
 * ■로그인 실패 이력 페이지 모델 ■payletter ■2018-09-28
 --------------------------------------------------------------------**/
@Data
@EqualsAndHashCode(callSuper=false)
public class AdminLoginFailHist extends BaseModel {
    //------------------------------------------------------------------
    // 엔티티 베이스 멤버 변수(엔티티 컬럼 정렬 순서 순)
    //------------------------------------------------------------------
    private String  adminID;     // 관리자 아이디
    private Integer errCode;     // 오류 코드
    private String  errMsg;      // 오류 메시지
    private String  ipAddr;      // 아이피 주소
    private String  regDate;     // 등록 일시

    //------------------------------------------------------------------
    // 엔터티 외 파라메터 변수
    //------------------------------------------------------------------
    private String  strFromYMD;    // 로그 검색용 (시작 일자)
    private String  strToYMD;      // 로그 검색용 (종료 일자)
    private String  errMsgTrans;   // 오류 메시지 (다국어 처리)

    //----------------------------------------------------------------------
    // ■하루 지난 날짜로 변경 ■payletter ■2018. 7. 13.
    //----------------------------------------------------------------------
    public void changeLastYMD() {
        if(!CommonUtil.isNullOrEmpty(strToYMD)) {
            this.strToYMD = CommonUtil.addOneDay(strToYMD);
        }
    }
}
