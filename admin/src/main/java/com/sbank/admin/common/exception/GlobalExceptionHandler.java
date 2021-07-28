package com.sbank.admin.common.exception;

import com.sbank.admin.common.constants.GlobalConstants;
import com.sbank.admin.common.service.CommonService;
import com.sbank.admin.common.service.SendMailService;
import com.sbank.admin.common.util.CommonUtil;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;
import org.thymeleaf.context.Context;

import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolationException;

/**--------------------------------------------------------------------
 * ■전역 예외처리 핸들러 ■payletter ■2018-09-28
 --------------------------------------------------------------------**/
@ControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger("EXCEPTION");
    private static final String INVALID_PARAMETERS = "유효하지 않은 파라미터 입니다.";

    @Autowired
    private CommonService commonService;

    @Autowired
    private SendMailService sendMailService;

    @Value("${system.mail.receiver}")
    private String systemMailReceiver;

    @Value("${location}")
    private String location;

    /**--------------------------------------------------------------------
    * ■요청 모델 유효성 예외 처리 ■payletter ■2019-06-03
    --------------------------------------------------------------------**/
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ModelAndView handleNotValidException(MethodArgumentNotValidException methodArgumentNotValidException) {
        logger.error("################################ Model Valid Error Information Start ################################");

        for(FieldError fieldError : methodArgumentNotValidException.getBindingResult().getFieldErrors()) {
            logger.error("Error Message : [{}] {}", fieldError.getField(), fieldError.getDefaultMessage());
        }

        logger.error("################################ Model Valid Error Information End   ################################");

        return CommonUtil.getResJsonView(GlobalConstants.COMMON_FAILED_CODE, INVALID_PARAMETERS);
    }

    /**--------------------------------------------------------------------
    * ■Path Variable 유효성 예러 처리 ■payletter ■2019-06-05
    --------------------------------------------------------------------**/
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ConstraintViolationException.class)
    public ModelAndView handleNotValidException(ConstraintViolationException constraintViolationException) {
        logger.error("################################ Model Valid Error Information Start ################################");
        logger.error("Error Message : {}", constraintViolationException.getMessage());
        logger.error("################################ Model Valid Error Information End   ################################");

        return CommonUtil.getResJsonView(GlobalConstants.COMMON_FAILED_CODE, INVALID_PARAMETERS);
    }

    /**--------------------------------------------------------------------------
     * ■GlobalException 처리 ■payletter ■2019-03-06
     -------------------------------------------------------------------------**/
    @ResponseStatus(value = HttpStatus.OK)
    @ExceptionHandler(GlobalException.class)
    public ModelAndView handleGlobalException(HttpServletResponse httpServletResponse, GlobalException globalException) {
        int intRetCode   = globalException.getRetCode();
        String strRetMsg = commonService.getMessage("common.msg.internalErr");

        try {
            logger.error("Global Exception Stack Trace: {}", ExceptionUtils.getStackTrace(globalException));

            if(globalException.isCustomMessage()) {
                strRetMsg = commonService.getMessage(globalException.getMessage());
            }

            if(globalException.isSendMail()) {
                sendMail(globalException);
            }
        } catch(Exception ex) {
            logger.error("handleGlobalException Stack Trace: {}", ExceptionUtils.getStackTrace(ex));
            strRetMsg = ex.getMessage();
            httpServletResponse.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }

        return CommonUtil.getResJsonView(intRetCode, strRetMsg);
    }

    /**--------------------------------------------------------------------
    * ■Exception 처리 ■payletter ■2019-05-22
    --------------------------------------------------------------------**/
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ModelAndView handleException(Exception exception) {
        int intRetCode   = GlobalConstants.COMMON_FAILED_CODE;
        String strRetMsg = commonService.getMessage("common.msg.internalErr");

        logger.error("Throwable Exception Stack Trace: {}", ExceptionUtils.getStackTrace(exception));

//        sendMail(exception);

        return CommonUtil.getResJsonView(intRetCode, strRetMsg);
    }

    /**--------------------------------------------------------------------
    * ■메일 전송 ■payletter ■2019-06-05
    --------------------------------------------------------------------**/
    private void sendMail(Throwable throwable) {
        try {
            Context mailBodyContext = new Context();
            mailBodyContext.setVariable("fileName", throwable.getStackTrace()[0].getFileName());
            mailBodyContext.setVariable("className", throwable.getStackTrace()[0].getClassName());
            mailBodyContext.setVariable("methodName", throwable.getStackTrace()[0].getMethodName());
            mailBodyContext.setVariable("errorMessage", ExceptionUtils.getMessage(throwable));
            mailBodyContext.setVariable("stackTrace", ExceptionUtils.getStackTrace(throwable));

            sendMailService.sendMail(systemMailReceiver,
                                     GlobalConstants.FROM_MAIL_ADDRESS,
                                     String.format(GlobalConstants.SYSTEM_ERROR_MAIL_SUBJECT, location),
                                     GlobalConstants.SYSTEM_ERROR_MAIL_TEMPLATE,
                                     mailBodyContext);
        } catch (Exception e) {
            logger.error("sendMail Stack Trace: {}", ExceptionUtils.getStackTrace(e));
        }
    }
}
