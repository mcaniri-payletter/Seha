package com.sbank.admin.menuauthority.domain;

import com.sbank.admin.common.domain.BaseModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

/**--------------------------------------------------------------------
 * ■메뉴 역할 모델 ■payletter ■2018-09-28
 --------------------------------------------------------------------**/
@Data
@EqualsAndHashCode(callSuper=false)
public class MenuRole extends BaseModel {
    //------------------------------------------------------------------
    // 엔티티 베이스 멤버 변수(엔티티 컬럼 정렬 순서 순)
    //------------------------------------------------------------------
    @NotNull
    private Short   menuRoleNo;          // 메뉴 역할 번호
    @NotBlank
    private String  menuRoleName;        // 메뉴 역할 명
    private Integer adminNo;             // 관리자 번호
    @NotNull
    private Boolean useFlag;              // 사용 여부 (0:미사용, 1:사용)
    private String  regDate;              // 등록 일시
    private String  updDate;              // 수정 일시

    //------------------------------------------------------------------
    // 메뉴 역할 상세
    //------------------------------------------------------------------
    private Short   menuGroupNo;         // 메뉴 그룹 번호
    private Short   menuNo;              // 메뉴 번호
    private Byte    authCode;            // 권한 코드
    private Boolean ciReadFlag;           // 중요정보 열람 여부 (0:불가, 1:가능)
    private Boolean dnAvailFlag;          // 다운로드 가능 여부 (0:불가, 1:가능)

    //------------------------------------------------------------------
    // 엔티티 베이스 가공 멤버 변수
    //------------------------------------------------------------------
    private Short   menuGroupSortNo;     // 메뉴 그룹 정렬 순서
    private String  menuName;            // 메뉴 명
    private Short   menuSortNo;          // 메뉴 정렬 순서

    private Short   depth;               // 메뉴 Depth
    private String  adminID;             // 관리자 아이디
    private List<MenuRole> menuRoleList; // 메뉴 역할 리스트

    public List<MenuRole> getMenuRoleList() {

        if (menuRoleList == null) {
            menuRoleList = new ArrayList<>();
        }
        return menuRoleList;
    }
}
