package com.sbank.admin.common.domain;

import com.sbank.admin.common.constants.ErrorCode;
import com.sbank.admin.common.constants.GlobalConstants;
import lombok.Data;

/**--------------------------------------------------------------------
 * ■처리 결과 응답 모델 ■payletter ■2018-09-28
 --------------------------------------------------------------------**/
@Data
public class BaseRes {
    private String strRetMsg;
    private int    intRetCode;

    public BaseRes() {
        this.intRetCode = GlobalConstants.COMMON_SUCCEED_CODE;
        this.strRetMsg  = GlobalConstants.COMMON_SUCCEED_MSG;
    }

    public BaseRes(ErrorCode errorCode) {
        this.intRetCode    = errorCode.getCode();
        this.strRetMsg = errorCode.getMessage();
    }
}
