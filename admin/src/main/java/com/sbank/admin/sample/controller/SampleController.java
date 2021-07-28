package com.sbank.admin.sample.controller;

import com.sbank.admin.common.annotation.AuthMethod;
import com.sbank.admin.common.constants.GlobalConstants;
import com.sbank.admin.common.constants.PathConstants;
import com.sbank.admin.common.domain.TypeCode;
import com.sbank.admin.common.service.CommonService;
import com.sbank.admin.common.util.CommonUtil;
import com.sbank.admin.menuauthority.domain.Menu;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

/**--------------------------------------------------------------------
 * ■샘플 컨트롤러 ■yjyoo ■2021-07-14
 --------------------------------------------------------------------**/
@Controller
/*
   PathConstants에서 Controller 접근 URI 및 templates 경로, 파일명 일괄 관리
 */
@RequestMapping(value = PathConstants.PATH_CONTROLLER_SAMPLE)
public class SampleController {

    @Autowired
    CommonService commonService;

    /**--------------------------------------------------------------------
     * ■샘플 페이지 진입 함수 ■yjyoo ■2021-07-14
     --------------------------------------------------------------------**/
    /*
       권한 설정부
       1. 초기 페이지 진입, 목록 조회, 상세 조회 : UserAuth.READONLY
       2. 등록, 수정, 삭제 : UserAuth.ALL
     */
    @AuthMethod(hasAuth = CommonUtil.UserAuth.READONLY)
    @RequestMapping(value = "/sample")
    /*
       초기 페이지 진입 함수명 : initPage()
       하나의 Controller에 여러 초기 페이지 진입 함수가 필요할 경우 initPage 뒤에 기능 명시 : initPageDetail() ...
     */
    public ModelAndView initPage() {
        ModelAndView mav = new ModelAndView(PathConstants.PATH_VIEW_SAMPLE);

        // DDLB의 경우 DDLB.xml 파일에서 일괄 관리
        // DDLB 예시1 - 조건 없음
        List<TypeCode> list1 = commonService.selectCodeList("ddlb.selectLanguageCodeList");
        // DDLB 예시2 - 조건 있음 (사용 여부 : '사용')
        List<TypeCode> list2 = commonService.selectCodeList("ddlb.selectLanguageCodeList", new TypeCode(GlobalConstants.USEFLAG_Y));
        // DDLB 예시3 - 조건 있음 (사용 여부 : '사용', 추가 검색 조건 : 'ko' 만)
        List<TypeCode> list3 = commonService.selectCodeList("ddlb.selectLanguageCodeList", new TypeCode(GlobalConstants.USEFLAG_Y, GlobalConstants.LANGUAGE_CODE_KO));

        mav.addObject("list1", list1);
        mav.addObject("list2", list2);
        mav.addObject("list3", list3);

        return mav;
    }

    /**--------------------------------------------------------------------
     * ■샘플 상세 페이지 진입 함수 ■yjyoo ■2021-07-15
     --------------------------------------------------------------------**/
    @AuthMethod(hasAuth = CommonUtil.UserAuth.READONLY)
    @RequestMapping(value = "/sampleDetail")
    public ModelAndView initPageDetail() {
        ModelAndView mav = new ModelAndView(PathConstants.PATH_VIEW_SAMPLE_DETAIL);

        mav.addObject("EDIT_MODE", GlobalConstants.MODE_DTL);

        return mav;
    }

    /**--------------------------------------------------------------------
     * ■샘플 편집 페이지 진입 함수 ■yjyoo ■2021-07-16
     --------------------------------------------------------------------**/
    @AuthMethod(hasAuth = CommonUtil.UserAuth.READONLY)
    @RequestMapping(value = "/sampleEdit")
    public ModelAndView initPageEdit(Menu menu) {
        ModelAndView mav = new ModelAndView(PathConstants.PATH_VIEW_SAMPLE_EDIT);

        mav.addObject("EDIT_MODE", menu.getEditMode());

        return mav;
    }
}
