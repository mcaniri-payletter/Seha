package com.sbank.admin.common.domain;

import lombok.Data;

/**--------------------------------------------------------------------
 * ■기본 DT 포함 모델 ■payletter ■2018-09-28
 --------------------------------------------------------------------**/
@Data
public class BaseModel {
    // DataTable 관련 변수 정의
    private int pageNo;
    private int pageSize;
    private int pageFechNo;
    private int draw;
    private int start;
    private int length;
    private int recordsFiltered;
    private int recordsTotal;
    private Boolean partialSearchFlag;
    private String strSortColumn;
    private String strSortType;
    private String langCode;
    private String editMode;
    private Boolean excelFlag = false;
}
