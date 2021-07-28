package com.sbank.admin.common.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**-------------------------------------------------------------
 # FileName : BaseGenericRes.java
 # Author   : payletter
 # Desc     : BaseRes + 제네릭 추가 응답 모델
 # Date     : 2020-03-12
/**----------------------------------------------------------**/
@EqualsAndHashCode(callSuper=false)
@Data
public class BaseGenericRes<T> extends BaseRes {
    private T data;

    public BaseGenericRes() {}
}