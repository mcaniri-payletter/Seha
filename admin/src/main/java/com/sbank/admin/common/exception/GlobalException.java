package com.sbank.admin.common.exception;

import com.sbank.admin.common.constants.ErrorCode;
import com.sbank.admin.common.constants.GlobalConstants;

/**--------------------------------------------------------------------
* ■사용자 정의 전역 오류 클래스 ■payletter ■2019-05-21
--------------------------------------------------------------------**/
public class GlobalException extends RuntimeException {
    private int     retCode;
    private boolean customMessage;
    private boolean sendMail;

    public GlobalException(ErrorCode errorCode) {
        this(errorCode, false);
    }

    public GlobalException(ErrorCode errorCode, boolean messageFlag) {
        this(errorCode, messageFlag, false);
    }

    public GlobalException(ErrorCode errorCode, boolean messageFlag, boolean sendMailFlag) {
        super(errorCode.getMessage());
        this.retCode       = errorCode.getCode();
        this.customMessage = messageFlag;
        this.sendMail      = sendMailFlag;
    }

    public GlobalException(String message) {
        this(message, false);
    }

    public GlobalException(String message, boolean messageFlag) {
        this(message, messageFlag, false);
    }

    public GlobalException(String message, boolean messageFlag, boolean sendMailFlag) {
        this(GlobalConstants.COMMON_FAILED_CODE, message, messageFlag, sendMailFlag);
    }

    public GlobalException(int retCode, String message, boolean messageFlag, boolean sendMailFlag) {
        super(message);
        this.retCode       = retCode;
        this.customMessage = messageFlag;
        this.sendMail      = sendMailFlag;
    }

    public GlobalException(Throwable throwable) {
        super(throwable);
        this.retCode  = GlobalConstants.COMMON_FAILED_CODE;
        this.sendMail = true;
    }

    public boolean isCustomMessage() {
        return customMessage;
    }

    public boolean isSendMail() {
        return sendMail;
    }

    public int getRetCode() {
        return retCode;
    }
}