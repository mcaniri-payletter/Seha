package com.sbank.admin.menuauthority.domain;

import com.sbank.admin.common.domain.BaseModel;
import com.sbank.admin.common.domain.ValidationGroups;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**--------------------------------------------------------------------
 * ■메뉴 모델 ■payletter ■2018-09-28
 --------------------------------------------------------------------**/
@Data
@EqualsAndHashCode(callSuper=false)
public class Menu extends BaseModel {
    //------------------------------------------------------------------
    // 엔티티 베이스 멤버 변수(엔티티 컬럼 정렬 순서 순)
    //------------------------------------------------------------------
    @NotNull(groups = {ValidationGroups.Insert.class, ValidationGroups.Delete.class})
    private Short   menuNo;          // 메뉴 번호
    @NotNull(groups = {ValidationGroups.Insert.class, ValidationGroups.Delete.class})
    private Short   menuGroupNo;     // 메뉴 그룹 번호
    @NotBlank(groups = ValidationGroups.Insert.class)
    private String  menuName;        // 메뉴 명
    @NotBlank(groups = ValidationGroups.Insert.class)
    private String  menuDivSgmnt;    // 메뉴 구분 세그먼트
    @NotBlank(groups = ValidationGroups.Insert.class)
    private String  menuLink;        // 메뉴 링크

    @NotBlank(groups = ValidationGroups.Insert.class)
    private String  menuDesc;        // 메뉴 설명
    @NotNull(groups = ValidationGroups.Delete.class)
    private Short   sortNo;          // 정렬 순서
    @NotNull(groups = ValidationGroups.Insert.class)
    private Boolean useFlag;         // 사용 여부 (0:미사용, 1:사용)
    private Integer adminNo;         // 관리자 번호
    private String  regDate;         // 등록 일시
    private String  updDate;         // 수정 일시

    //------------------------------------------------------------------
    // 엔티티 베이스 가공 멤버 변수
    //------------------------------------------------------------------
    private String  adminID;         // 관리자 아이디
    private String  menuNameTrans;   // 메뉴 명(다국어 처리)
    @NotEmpty(groups = ValidationGroups.Update.class)
    private List<Menu> menuList;     // 메뉴 리스트
    private Short createSortNo;      // 정렬 순서 조정 관련
}
