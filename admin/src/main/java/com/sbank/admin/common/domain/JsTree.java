package com.sbank.admin.common.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**--------------------------------------------------------------------
 * ■JsTree 정보 모델 ■payletter ■2018-09-28
 --------------------------------------------------------------------**/
@Data
@EqualsAndHashCode(callSuper=false)
public class JsTree extends Object {

    //------------------------------------------------------------------
    // JsTree 맵핑 멤버 변수
    //------------------------------------------------------------------
    private String id;        // 유니크 번호
    private String text;      // 트리 명
    private String parent;    // 부모의 유니크 번호
    private String icon;      // 아이콘 클래스
    private String sortNo;    // 순서 번호
}
