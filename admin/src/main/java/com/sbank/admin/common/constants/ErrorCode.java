package com.sbank.admin.common.constants;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**--------------------------------------------------------------------
 * ■에러 코드 관련 Enum 클래스 ■payletter ■2019-08-21
 --------------------------------------------------------------------**/
@Getter
public enum ErrorCode {
    INTERNAL_ERR  (9999, "An internal error occurred.", HttpStatus.INTERNAL_SERVER_ERROR),

    AUTH_BLANK_CLIENT_ID       (1000, "Client ID cannot be blank.",                               HttpStatus.UNAUTHORIZED),
    AUTH_NOT_FOUND_CLIENT_ID   (1001, "Client ID is not registered in system.",                   HttpStatus.UNAUTHORIZED),
    AUTH_NOT_ALLOWED_IP        (1002, "Your IP is not allowed.",                                  HttpStatus.UNAUTHORIZED),
    AUTH_NOT_UNSUPPORTEDJWT    (1003, "claimsJws argument does not represent an Claims JWS",      HttpStatus.UNAUTHORIZED),
    AUTH_NOT_MALFORMEDJWT      (1004, "claimsJws string is not a valid JWS",                      HttpStatus.UNAUTHORIZED),
    AUTH_NOT_SIGNATURE         (1005, "claimsJws JWS signature validation fails",                 HttpStatus.UNAUTHORIZED),
    AUTH_NOT_EXPIREDJWT        (1006, "expiration time is time over",                             HttpStatus.UNAUTHORIZED),
    AUTH_NOT_ILLEGALARGUMENT   (1007, "the claimsJws string is null or empty or only whitespace", HttpStatus.UNAUTHORIZED),
    AUTH_NOT_INVALID_TOKEN     (1008, "etc Exception",                                            HttpStatus.UNAUTHORIZED),

    INVALID_ARGUMENT           (2000, "Parameters not valid.",                                    HttpStatus.BAD_REQUEST),
    RESPONSE_NOT_DATA          (3000, "Response Not Data",                                        HttpStatus.UNAUTHORIZED),

    DAESA_API_INVALID_STATUS   (5001, "PG Daesa API returns invalid HttpStatus",                  HttpStatus.MULTI_STATUS),
    DAESA_API_NOT_MACHED_COUNT (5002, "PG Daesa API returns invalid HttpStatus",                  HttpStatus.INTERNAL_SERVER_ERROR),
    DAESA_API_ILLEGAL_URI      (5003, "Check PG Daesa API URI.",                                  HttpStatus.INTERNAL_SERVER_ERROR),
    DAESA_NOT_SUCCESS_DB_INSERT(5004, "PGDaesa SP returns invalid code",                          HttpStatus.INTERNAL_SERVER_ERROR),
    DAESA_NOT_VALID_DATEFORMAT (5006, "Check DateFormat of PG Daesa",                             HttpStatus.INTERNAL_SERVER_ERROR),
    DAESA_GENERAL_ERROR        (5010, "Exception is appeared in PG Daesa.",                       HttpStatus.INTERNAL_SERVER_ERROR),
    ;

    private final int        code;
    private final String     message;
    private final HttpStatus httpStatus;

    ErrorCode(int code, String message, HttpStatus httpStatus) {
        this.code       = code;
        this.message    = message;
        this.httpStatus = httpStatus;
    }
}