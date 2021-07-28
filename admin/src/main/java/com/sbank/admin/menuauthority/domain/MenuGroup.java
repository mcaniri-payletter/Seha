package com.sbank.admin.menuauthority.domain;

import com.sbank.admin.common.domain.BaseModel;
import com.sbank.admin.common.domain.ValidationGroups;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**--------------------------------------------------------------------
 * ■메뉴 그룹 모델 ■payletter ■2018-09-28
 --------------------------------------------------------------------**/
@Data
@EqualsAndHashCode(callSuper=false)
public class MenuGroup extends BaseModel {
    //------------------------------------------------------------------
    // 엔티티 베이스 멤버 변수(엔티티 컬럼 정렬 순서 순)
    //------------------------------------------------------------------
    @NotNull(groups = ValidationGroups.Delete.class)
    private Short   menuGroupNo;           // 메뉴 그룹 번호
    private String  menuGroupName;         // 메뉴 그룹 명
    private String  menuGroupIcon;         // 메뉴 그룹 아이콘
    @NotNull(groups = ValidationGroups.Delete.class)
    private Short   sortNo;                // 정렬 순서
    private Integer adminNo;               // 관리자 번호

    private Boolean useFlag;               // 사용 여부 (0:미사용, 1:사용)
    private String  regDate;               // 등록 일시
    private String  updDate;               // 수정 일시

    //------------------------------------------------------------------
    // 엔티티 베이스 가공 멤버 변수
    //------------------------------------------------------------------
    private String  adminID;               // 관리자 아이디
    private String  menuGroupNameTrans;    // 메뉴 그룹 명(다국어 처리)
    @NotEmpty(groups = ValidationGroups.Update.class)
    private List<MenuGroup> menuGroupList; // 메뉴 그룹 리스트
    private Short createSortNo;            // 정렬 순서 조정 관련
}