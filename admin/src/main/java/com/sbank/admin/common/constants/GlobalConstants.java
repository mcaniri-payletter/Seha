package com.sbank.admin.common.constants;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**--------------------------------------------------------------------
 * ■전역 상수 설정 ■payletter ■2018-09-28
 --------------------------------------------------------------------**/
public class GlobalConstants {
    private GlobalConstants() {}

    // 서비스 명
    public static final String  SERVICE_NAME            = "SBANK";

    // 로그인 정보
    public static final int     LAST_PWD_CHG_DAYS_LIMIT = 90;
    public static final String  SESSION_CSRF_KEY        = "47DCP4QUB3N5EF8U";
    public static final String  SESSION_LOGIN_KEY       = "SESSION_LOGIN_KEY_P";
    public static final String  SESSION_LANG_INFO       = "SESSION_LANG_INFO_P";
    public static final String  COOKIE_LANG_INFO        = "COOKIE_LANG_INFO_P";
    public static final String  SESSION_CI_FIELD_FLAG   = "CIFieldFlag";

    // 시스템 관련
    public static final String  SYSTEM_AES_KEY       = "kk9w1eank8q9lidj";                 // AES 암복호화키(외부용)
    public static final String  SYSTEM_ENCODING      = "UTF-8";                            // 기본 인코딩
    public static final String  HIDDEN_CSRF_ID       = "RequestVerificationToken";
    public static final String  BCRYPT_ALGORITHM     = "SHA1PRNG";
    public static final String  COMMON_SUCCEED_MSG   = "OK";

    public static final int     COMMON_SUCCEED_CODE  = 0;     // 정상 응답 코드
    public static final int     COMMON_FAILED_CODE   = 9999;  // 비정상 응답 코드

    public static final String  SYSTEM_AESGCM_KEY    = "vkl20vjfbjd6dklz9cdvghv23nv8k9w3"; // AES-GCM 암복호화 키(내부용 32Byte - 가변 타입 사용)
    public static final String  SYSTEM_AESGCM_IV     = "fd2c7skvbnh3";                     // AES-GCM 암복호화 IV(내부용)
    public static final String  SYSTEM_AESGCM_AAD    = "dr2gbyu2a9m4o2";                   // AES-GCM 암복호화 AAD(내부용)

    public static final String  MODE_INS             = "Ins";
    public static final String  MODE_UPD             = "Upd";
    public static final String  MODE_DTL             = "Dtl";

    public static final Boolean USEFLAG_Y            = true;
    public static final Boolean USEFLAG_N            = false;

    // PG URL
    // POQ
    public static final String  POQ_API_CANCEL_PAYMENT = "/v1.0/payments/cancel"; // POQ 결제 취소

    // 비밀번호 초기화 관련
    public static final String  RESET_PWD_URI                 = "/login/resetPwdRequest";
    public static final String  PATH_RESET_PWD                = "login/adminInitPwd";
    public static final String  ADMIN_RESET_PWD_MAIL_SUBJECT  = "[SBANK_Admin] Password Reset Mail";
    public static final int     TOKEN_EXPIRE_VALID_TIME       = 1000 * 60 * 60;  // 비밀번호 초기화 유효 시간 (60분)

    // 메일 전송 관련
    public static final String FROM_MAIL_ADDRESS           = "admin@payletter.com";
    public static final String SYSTEM_ERROR_MAIL_SUBJECT   = "[SBANK_Admin-%s] An error has occurred.";
    public static final String SYSTEM_ERROR_MAIL_TEMPLATE  = "documents/mail/SystemErrorMail.html";

    // 엑셀 다운로드 관련
    public static final int    EXCEL_MAX_COUNT                        = 50000;                               // 엑셀 최대 다운로드
    public static final String EXCELDOWNLOAD_DATA_LIST                = "dataList";
    public static final String EXCELDOWNLOAD_TEMPLATE_FILE            = "templateName";

    public static final String EXCEL_TEMPLATE_ADMINISTRATOR           = "administrator.xlsx";
    public static final String EXCEL_TEMPLATE_ADMIN_LOGIN_FAIL_HIST   = "adminLoginFailHist.xlsx";
    public static final String EXCEL_TEMPLATE_ADMIN_MENU_ACCESS_LOG   = "adminMenuAccessLog.xlsx";

    public static Map<String, String> EXCEL_DOWNLOAD_FILE_NAME_MAP() {
        final Map<String, String> objMap = new HashMap<>();

        objMap.put(EXCEL_TEMPLATE_ADMINISTRATOR,           "Administrator_List_%s.xlsx");
        objMap.put(EXCEL_TEMPLATE_ADMIN_LOGIN_FAIL_HIST,   "Admin_Login_Fail_Hist_%s.xlsx");
        objMap.put(EXCEL_TEMPLATE_ADMIN_MENU_ACCESS_LOG,   "Admin_Menu_Access_Log_%s.xlsx");

        return Collections.unmodifiableMap(objMap);
    }

    // 언어 코드
    public static final String LANGUAGE_CODE_KO = "ko";
    public static final String LANGUAGE_CODE_EN = "en";

    //region 정규식 관련 정보
    public static final String REGEXP_MASKING_NAME             = "^(.)(.+)(.)$";
    public static final String REGEXP_MASKING_NAME_TWO_CHAR    = "^(.)(.+)$";
    public static final String REGEXP_MASKING_EMAIL            = "\\b(\\S+)+@(\\S+.\\S+)";
    public static final String REGEXP_MASKING_EMAIL_THREE_CHAR = "\\b(\\S+)[^@][^@]+@(\\S+)";
    public static final String REGEXP_MASKING_EMAIL_ELSE_CHAR  = "\\b(\\S+)[^@][^@][^@]+@(\\S+)";
    public static final String REGEXP_MASKING_PHONE_NO         = "(01[016789])-(\\d{3,4})-\\d{4}$";
    //endregion

    // 고객사 구분 코드
    public static final String C_CODE = "C";
    public static final String S_CODE = "S";
}
