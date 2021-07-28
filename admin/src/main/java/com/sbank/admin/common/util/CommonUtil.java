package com.sbank.admin.common.util;

import com.payletter.crypto.aesgcm.PLAesGcmCipher;
import com.sbank.admin.common.annotation.CIField;
import com.sbank.admin.common.constants.GlobalConstants;
import com.sbank.admin.common.domain.BaseModel;
import com.sbank.admin.common.domain.BaseRes;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommonUtil {
    private static final PLAesGcmCipher plAesGcmCipher = new PLAesGcmCipher(GlobalConstants.SYSTEM_AESGCM_KEY);
    private static final Logger logger = LoggerFactory.getLogger(CommonUtil.class);

    private static final String RET_CODE = "intRetCode";
    private static final String RET_MSG  = "strRetMsg";
    private static final int SALE_MONTH_START = 1;
    private static final int SALE_MONTH_END  = 3;

    /**--------------------------------------------------------------------
     * ■IP 주소 조회 ■payletter ■2018-09-28
     --------------------------------------------------------------------**/
    public static String getIpAddr(HttpServletRequest req){
        String strIP = req.getHeader("X-FORWARDED-FOR");

        if(strIP == null) {
            strIP = req.getRemoteAddr();
        }

        return strIP;
    }

    /**--------------------------------------------------------------------
     * ■UUID ■payletter ■2018-09-28
     --------------------------------------------------------------------**/
    public static String getUuid() {
        return UUID.randomUUID().toString().replaceAll("-",  "");
    }

    /**--------------------------------------------------------------------
     * ■부분 조회 여부 ■payletter ■2018-09-28
     --------------------------------------------------------------------**/
    private static boolean isPartialSearch(int intLength) {
        return (intLength > 0);
    }

    /**--------------------------------------------------------------------
     * ■권한 설정 ■payletter ■2018-09-28
     --------------------------------------------------------------------**/
    public enum UserAuth {
        ALL(1),
        READONLY(2);

        private int intVal;

        UserAuth(int intVal) {
            this.intVal = intVal;
        }
        public int getIntVal() {
            return intVal;
        }
    }

    public enum CIFieldType {
        Name(1),
        Account(2),
        Email(3),
        PhoneNo(4),
        CouponNo(5);

        private int intValue;
        CIFieldType(int intValue) { this.intValue = intValue; }
        public int getIntValue() { return this.intValue; }
    }

    /**--------------------------------------------------------------------
     * ■입력 파리미터 Object 정보 출력(CIField 마스킹 처리) ■payletter ■2019-02-25
     --------------------------------------------------------------------**/
    public static String getObjectInfo(Object object) throws Throwable {
        StringBuilder objSB = new StringBuilder();

        Class<?> orgin = object.getClass();
        Field[] fieldsArr = orgin.getDeclaredFields();
        List<Field> allFields = new ArrayList<>(Arrays.asList(fieldsArr));

        objSB.append(String.format("%s (", orgin.getSimpleName()));

        for (Field field : allFields) {
            field.setAccessible(true);
            if (CommonUtil.isCIField(field)) {
                objSB.append(String.format("CIField %s=%s, ", field.getName(), CommonUtil.getCIMaskedValue(field, object)));
            } else {
                objSB.append(String.format("%s=%s, ", field.getName(), field.get(object)));
            }
        }
        objSB.append(")");

        return objSB.toString();
    }

    /**--------------------------------------------------------------------
     * ■CI Field 여부 확인 ■payletter ■2018-09-28
     --------------------------------------------------------------------**/
    public static boolean isCIField(Field field) {
        return field.getAnnotation(CIField.class) != null;
    }

    /**--------------------------------------------------------------------
     * ■입력 파리미터 CI Field 값 마스킹 처리 ■payletter ■2018-09-28
     --------------------------------------------------------------------**/
    public static String getCIMaskedValue(Field field, Object parameter) throws Throwable {
        String      maskedValue     = null;
        CIFieldType enumCIFiledType = field.getAnnotation(CIField.class).type();

        field.setAccessible(true);

        if(field.get(parameter) != null) {
            String parameterValue  = field.get(parameter).toString();

            switch (enumCIFiledType) {
                case Name:
                case Account:
                case CouponNo:
                    maskedValue = getMaskedName(parameterValue);
                    break;
                case Email:
                    maskedValue = getMaskedEmail(parameterValue);
                    break;
                case PhoneNo:
                    maskedValue = getMaskedPhoneNumber(parameterValue);
                    break;
            }
        }
        return maskedValue;
    }

    /**--------------------------------------------------------------------
     * ■출력 파리미터 CI Field 값 마스킹 설정 ■payletter ■2018-09-28
     --------------------------------------------------------------------**/
    public static void setCIMaskedValue(Object parameter) {
        try {
            String maskedValue = null;
            Class<?> orgin = parameter.getClass();
            Field[] fieldsArr = orgin.getDeclaredFields();
            List<Field> allFields = new ArrayList<>(Arrays.asList(fieldsArr));

            for (Field field : allFields) {
                if (CommonUtil.isCIField(field)) {
                    CIFieldType enumCIFiledType = field.getAnnotation(CIField.class).type();

                    field.setAccessible(true);

                    if (field.get(parameter) != null) {
                        String parameterValue = field.get(parameter).toString();

                        switch (enumCIFiledType) {
                            case Name:
                            case Account:
                            case CouponNo:
                                maskedValue = getMaskedName(parameterValue);
                                break;
                            case Email:
                                maskedValue = getMaskedEmail(parameterValue);
                                break;
                            case PhoneNo:
                                maskedValue = getMaskedPhoneNumber(parameterValue);
                                break;
                        }
                    }

                    field.set(parameter, maskedValue);
                }
            }
        }
        catch (Exception ex) { }
    }

    private static String getMaskedName(String input) {
        String regex = (input.length() == 2) ? GlobalConstants.REGEXP_MASKING_NAME_TWO_CHAR : GlobalConstants.REGEXP_MASKING_NAME;
        String maskedName = "";
        Matcher matcher = Pattern.compile(regex).matcher(input);
        if(matcher.matches()) {
            for (int i = 1; i <= matcher.groupCount(); i++) {
                if (i == 2) {
                    char[] charArr = new char[matcher.group(i).length()];
                    Arrays.fill(charArr, '*');
                    maskedName = maskedName + String.valueOf(charArr);
                } else {
                    maskedName = maskedName + matcher.group(i);
                }
            }
        }
        return maskedName;
    }

    private static String getMaskedPhoneNumber(String input) {
        String regex = GlobalConstants.REGEXP_MASKING_PHONE_NO;
        Matcher matcher = Pattern.compile(regex).matcher(input);
        if (matcher.find()) {
            String replaceTarget = matcher.group(2);
            char[] charArr = new char[replaceTarget.length()];
            Arrays.fill(charArr, '*');
            return input.replace(replaceTarget, String.valueOf(charArr));
        }
        return input;
    }

    private static String getMaskedEmail(String input) {
        String regex = GlobalConstants.REGEXP_MASKING_EMAIL;
        Matcher matcher = Pattern.compile(regex).matcher(input);
        if (matcher.find()) {
            String emailId = matcher.group(1);
            int length = emailId.length();
            if (length < 3) {
                char[] charArr = new char[length];
                Arrays.fill(charArr, '*');
                return input.replace(emailId, String.valueOf(charArr));    //세글자 미만의 이메일 아이디는 모두 마스킹 처리
            } else if (length == 3) {
                regex = GlobalConstants.REGEXP_MASKING_EMAIL_THREE_CHAR;
                return input.replaceAll(regex, "$1**@$2");      //이메일 아이디중 뒤 두글자만 마스킹
            } else {
                regex = GlobalConstants.REGEXP_MASKING_EMAIL_ELSE_CHAR;
                return input.replaceAll(regex, "$1***@$2");     //이메일 아이디중 뒤 세자리를 마스킹 처리
            }
        }
        return input;
    }

    /**--------------------------------------------------------------------
     * ■모델 유효성 검사 ■payletter ■2018-09-28
     --------------------------------------------------------------------**/
    public static boolean isVallidCheckModel(BindingResult objBindingResult, Logger logger){
        if(objBindingResult.hasErrors()){
            List<FieldError> objFieldErrorlist = objBindingResult.getFieldErrors();

            logger.error("################################ Model Valid Error Information Start ################################");

            for (FieldError e : objFieldErrorlist)
                logger.error("Error Messsage : {}",e.getDefaultMessage());

            logger.error("################################ Model Valid Error Information End ################################");

            return false;
        }

        return true;
     }

    /**--------------------------------------------------------------------
     * ■익셉션 로깅 처리 ■payletter ■2018-09-28
     --------------------------------------------------------------------**/
    public static void globalExceptionHandle(Exception ex, Logger logger){
        logger.error("################################ Exception Information Start ################################");
        logger.error("Exception Message     : {}", ex.getMessage());
        logger.error("Exception Stack Trace : {}", (Object[]) ex.getStackTrace());
        logger.error("################################ Exception Information End   ################################");
    }

    /**--------------------------------------------------------------------
     * ■modelAndView 생성 함수 ■payletter ■2018-09-28
     --------------------------------------------------------------------**/
    public static ModelAndView getResJsonView() {
        BaseRes baseRes = new BaseRes();

        return getResJsonView(baseRes);
    }

    public static ModelAndView getResJsonView(BaseRes baseRes) {
        return getResJsonView(null, baseRes);
    }

    public static ModelAndView getResJsonView(Object objData) {
        BaseRes baseModelRes = new BaseRes();

        return getResJsonView(objData, baseModelRes);
    }

    public static ModelAndView getResJsonView(Object object, BaseRes baseRes) {
        return getResJsonView(object, baseRes.getIntRetCode(), baseRes.getStrRetMsg());
    }

    public static ModelAndView getResJsonView(int intRetCode, String strRetMsg) {
        return getResJsonView(null, intRetCode, strRetMsg);
    }

    public static ModelAndView getResJsonView(Object object, int intRetCode, String strRetMsg) {
        ModelAndView mav = new ModelAndView(new MappingJackson2JsonView());
        Object maskedObject = object;
        mav.addObject(RET_CODE, intRetCode);
        mav.addObject(RET_MSG,  strRetMsg);

        if(object != null)
            mav.addObject("data", maskedObject);

        return mav;
    }

    public static ModelAndView getResJsonView(ModelAndView mav, Object object, int intRetCode, String strRetMsg) {
        mav.addObject(RET_CODE, intRetCode);
        mav.addObject(RET_MSG,  strRetMsg);

        if(object != null)
            mav.addObject("data", object);

        return mav;
    }

    public static ModelAndView getResJsonView(List<?> lstDataTable) {
        ModelAndView mav = new ModelAndView(new MappingJackson2JsonView());

        mav.addObject("data",   lstDataTable);
        mav.addObject(RET_CODE, GlobalConstants.COMMON_SUCCEED_CODE);
        mav.addObject(RET_MSG,  GlobalConstants.COMMON_SUCCEED_MSG);

        return mav;
    }

    public static ModelAndView getResJsonView(List<?> objDataTable, BaseModel baseModel) {
        ModelAndView mav = new ModelAndView(new MappingJackson2JsonView());

        mav.addObject("data",   objDataTable);
        mav.addObject(RET_CODE, GlobalConstants.COMMON_SUCCEED_CODE);
        mav.addObject(RET_MSG,  GlobalConstants.COMMON_SUCCEED_MSG);
        mav.addObject("baseModel",   baseModel);

        mav.addObject("draw",            baseModel.getDraw());
        mav.addObject("recordsTotal",    baseModel.getRecordsTotal());
        mav.addObject("recordsFiltered", baseModel.getRecordsFiltered());

        return mav;
    }

    public static boolean isJson(String strJsonData) {
        boolean blnResult = true;

        JSONParser objParser = new JSONParser();

        try {
            objParser.parse(strJsonData);
        }catch(ParseException ex) {
            blnResult = false;
        }

        return blnResult;
    }

    public static JSONObject getJson(String strJsonData){
        JSONParser objParser = new JSONParser();
        JSONObject objReturn = null;

        try {
            objReturn = (JSONObject) objParser.parse(strJsonData);
        }catch(ParseException ex) {
            objReturn = new JSONObject();
        }

        return objReturn;
    }

    /**--------------------------------------------------------------------
     * ■시간차 계산 함수 ■payletter ■2018-09-28
     --------------------------------------------------------------------**/
    public static long getDateTimeDiff(long intStartTime, long intEndTime) {
         long intDiffTime = intEndTime - intStartTime;

         return intDiffTime / (1000 * 60 * 60 * 24);
    }

    /**--------------------------------------------------------------------
     * ■날짜 형 선언 ■payletter ■2018-09-28
     --------------------------------------------------------------------**/
    public enum DateFormat{
        YYYYMMDD,
        YYYYMM,
        YYYY,
        YYYYMMDDHH24MISS,
        YYYYMMDDHH24MISS_NOSPACE,
        HH24MISS,
        HH24,
        MI,
        SS,
        YYYYMMDDHH24MISSMS,
        YYYY_MM_DD
    }

    /**--------------------------------------------------------------------
     * ■날짜 형 문자열로 변경 함수 ■payletter ■2018-09-28
     --------------------------------------------------------------------**/
    public static String convertDate(DateFormat format, Date date) {
        String strRetValue = "";

        SimpleDateFormat simpleDateFormatV1 = new SimpleDateFormat("yyyyMMdd");
        SimpleDateFormat simpleDateFormatV2 = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
        SimpleDateFormat simpleDateFormatV3 = new SimpleDateFormat("yyyyMMddHHmmss");
        SimpleDateFormat simpleDateFormatV4 = new SimpleDateFormat("HH:mm:ss");
        SimpleDateFormat simpleDateFormatV5 = new SimpleDateFormat("yyyyMM");
        SimpleDateFormat simpleDateFormatV6 = new SimpleDateFormat("yyyy");
        SimpleDateFormat simpleDateFormatV7 = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        SimpleDateFormat simpleDateFormatV8 = new SimpleDateFormat("yyyy-MM-dd");

        switch(format) {
            case YYYYMMDDHH24MISS :
                strRetValue = simpleDateFormatV2.format(date);
                break;

            case YYYYMMDDHH24MISS_NOSPACE :
                strRetValue = simpleDateFormatV3.format(date);
                break;

            case HH24MISS :
                strRetValue = simpleDateFormatV4.format(date);
                break;

            case YYYYMM :
                strRetValue = simpleDateFormatV5.format(date);
                break;

            case YYYY :
                strRetValue = simpleDateFormatV6.format(date);
                break;

            case YYYYMMDDHH24MISSMS :
                strRetValue = simpleDateFormatV7.format(date);
                break;

            case YYYY_MM_DD :
                strRetValue = simpleDateFormatV8.format(date);
                break;

            case YYYYMMDD :
            default :
                strRetValue = simpleDateFormatV1.format(date);
                break;
        }

        return strRetValue;
    }

    public static String convertDate(DateFormat format) {
        return convertDate(format, new Date());
    }

    // 수익년월 (현재-1 ~ 현재 -3, 총 3개월)
    public static List<String> getSaleYearMonthList() {

        List<String> saleYearMonthList = new ArrayList<>();

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMM");

        for (int inx=SALE_MONTH_START;inx<=SALE_MONTH_END;inx++) {
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.MONTH, -inx);
            String strSaleYearMonth = dateFormat.format(cal.getTime()).substring(0,6);
            saleYearMonthList.add(strSaleYearMonth);

        }

        return saleYearMonthList;
    }

    private static Date add(final Date date, final int calendarField, final int amount) {
        if (date == null) {
            throw new IllegalArgumentException("The date must not be null");
        }
        final Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(calendarField, amount);
        return c.getTime();
    }

    public static Date addMonths(final Date date, final int amount) {
        return add(date, Calendar.MONTH, amount);
    }

    /**--------------------------------------------------------------------
     * ■디렉토리 확인 함수 ■payletter ■2018-09-28
     --------------------------------------------------------------------**/
    public static File checkDirByPath(String strPath) {
        StringBuilder strFullPath = new StringBuilder();
        String[]      arrPath     = strPath.split("\\/");
        File          filePath = new File(strPath);

        for(int i = 1; i < arrPath.length; i++) {
            strFullPath.append("/" + arrPath[i]);

            filePath = new File(strFullPath.toString());

              if (!filePath.isDirectory())
                  filePath.mkdir();
        }

        return filePath;
    }

    //----------------------------------------------------------------------
    // ■Object 빈 값 or NULL 체크 ■payletter ■2018. 3. 19.
    //----------------------------------------------------------------------
    public static boolean isNullOrEmpty(Object objValue) {
        if (objValue == null) {
            return true;
        }

        if ((objValue instanceof String) && (((String) objValue).trim().length() == 0)) {
            return true;
        }

        if (objValue instanceof Map) {
            return ((Map<?, ?>) objValue).isEmpty();
        }

        if (objValue instanceof List) {
            return ((List<?>) objValue).isEmpty();
        }

        if (objValue instanceof Object[]) {
            return (((Object[]) objValue).length == 0);
        }

        return false;
    }

    //----------------------------------------------------------------------
    // ■하루 지난 날짜로 변경 ■payletter ■2018. 7. 13.
    //----------------------------------------------------------------------
    public static String addOneDay(String strOriginalYMD) {
        String strNextYMD = "";

        try {
            java.text.DateFormat objDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date dtToOriginalYMD = objDateFormat.parse(strOriginalYMD);
            LocalDate ldtOriginalYMD = dtToOriginalYMD.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            strNextYMD = ldtOriginalYMD.plusDays(1).toString();

        } catch(Exception objEx) { }

        return strNextYMD;
    }

    //----------------------------------------------------------------------
    // ■AES GCM 암호화 ■payletter ■2018-12-07
    //----------------------------------------------------------------------
    public static String getEncrypt(String strEnc, Boolean blnVariableFlag) {
        // blnVariableFlag true = 가변 암호화, false = 고정 암호화
        try{
            if(blnVariableFlag) {
                strEnc = plAesGcmCipher.encrypt(strEnc, plAesGcmCipher.ENCODE_HEXA);
            } else {
                strEnc = plAesGcmCipher.encrypt(strEnc, GlobalConstants.SYSTEM_AESGCM_IV, GlobalConstants.SYSTEM_AESGCM_AAD,  plAesGcmCipher.ENCODE_HEXA);
            }
        } catch(Exception ex) {
            logger.error("Error Messsage : {}", ex);
            strEnc = "";
        }

        return strEnc;
    }

    //----------------------------------------------------------------------
    // ■AES GCM 복호화 ■payletter ■2018-12-07
    //----------------------------------------------------------------------
    public static String getDecrypt(String strDec) {
        try{
            strDec = plAesGcmCipher.decrypt(strDec, plAesGcmCipher.ENCODE_HEXA);
        } catch(Exception ex) {
            logger.error("Error Messsage : {}", ex);
            strDec = "";
        }

        return strDec;
    }

    //----------------------------------------------------------------------
    // ■엑셀 다운로드 Row 수 셋팅 ■payletter ■2018. 12. 4.
    //----------------------------------------------------------------------
    public static void setExcelDownload(BaseModel baseModel) {
        baseModel.setPageFechNo(0);
        baseModel.setPageNo(1);
        baseModel.setPageSize(GlobalConstants.EXCEL_MAX_COUNT);
    }

    /**-------------------------------------------------------------
     ■ 랜덤 문자열 생성 ■ justny21 ■ 2020-04-28
     -------------------------------------------------------------**/
    public static String getRandomString(Integer stringLength) {
        StringBuffer sb = new StringBuffer();
        Random rn       = new Random();
        char[] charaters = {'a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z','0','1','2','3','4','5','6','7','8','9'};

        for( Integer i = 0 ; i < stringLength ; i++ ) {
            sb.append( charaters[ rn.nextInt( charaters.length ) ] );
        }

        return sb.toString().toLowerCase();
    }
}
