package com.sbank.admin.common.exception;

/**--------------------------------------------------------------------
* ■롤백 실행 방지 Exception 클래스 ■payletter ■2019-05-27
--------------------------------------------------------------------**/
public class NoRollbackException extends GlobalException {
    public NoRollbackException(String message) {
        super(message);
    }

    public NoRollbackException(String message, boolean messageFlag) {
        super(message, messageFlag);
    }

    public NoRollbackException(String message, boolean messageFlag, boolean sendMailFlag) {
        super(message, messageFlag, sendMailFlag);
    }

    public NoRollbackException(int retCode, String message, boolean messageFlag, boolean sendMailFlag) {
        super(retCode, message, messageFlag, sendMailFlag);
    }

    public NoRollbackException(Throwable objCause) {
        super(objCause);
    }
}
