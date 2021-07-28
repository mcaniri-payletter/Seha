package com.sbank.admin;

import com.sbank.admin.common.constants.GlobalConstants;
import com.sbank.admin.common.domain.BaseModel;
import com.sbank.admin.common.util.CommonUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.ui.ModelMap;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

/**--------------------------------------------------------------------
 * ■AOP 설정(I/O 로깅) ■payletter ■2018-09-28
 --------------------------------------------------------------------**/
@Aspect
@Component
public class ServiceAOP {
	private static final Logger logger = LoggerFactory.getLogger("AOP");

	/**--------------------------------------------------------------------
	 * ■controllerPointcut ■payletter ■2018-09-28
	 --------------------------------------------------------------------**/
	@Pointcut("within(com.sbank.admin..controller.*)")
	// 서비스까지 AOP 설정하는 경우
	// @Pointcut("within(com.sbank.admin..*)")
	public void controllerPointcut() {
	}

	@Pointcut("within(com.sbank.admin..service.*) && !(@annotation(com.sbank.admin.common.annotation.NoLogging) || @annotation(com.sbank.admin.common.annotation.Batches))")
	public void servicePointcut() {
	}

	/**--------------------------------------------------------------------
	 * ■컨트롤러에 대해서만 I/O 로깅 ■payletter ■2018-09-28
	 --------------------------------------------------------------------**/
	@Around("controllerPointcut()")
	public Object controllerAround(final ProceedingJoinPoint objProceedingJoinPoint) throws Throwable {
		// 메서드 실행
		Object objResult = executeMethod(objProceedingJoinPoint);

		// 응답 모델 데이터에 CI Field가 존재하면 해당 필드를 마스킹 처리 된 값으로 치환하여 반환
		if(objResult instanceof ModelAndView) {
			ModelAndView result = (ModelAndView)objResult;
			ModelMap modelMap   = result.getModelMap();
			Object modelData    = modelMap.get("data");

			if(modelData != null) {
				if(modelData instanceof ArrayList) {
					List<?> modelDataList = (ArrayList)modelData;
					for(int i = 0; i < modelDataList.size(); i++) {
						CommonUtil.setCIMaskedValue(modelDataList.get(i));
					}
				}
			}
		}
		return objResult;
	}

	/**--------------------------------------------------------------------
	 * ■서비스 AOP 처리 ■payletter ■2018-09-28
	 --------------------------------------------------------------------**/
	@Around("servicePointcut()")
	public Object servicePointcut(final ProceedingJoinPoint objProceedingJoinPoint) throws Throwable {
		// 메서드 실행 전
		setLangCode(objProceedingJoinPoint);

		for(Object object : objProceedingJoinPoint.getArgs()) {
			if (object instanceof BaseModel) {
				servicePaging((BaseModel) object);
			}
        }

		// 메서드 실행
		Object objResult = executeMethod(objProceedingJoinPoint);

		return objResult;
	}

	/**--------------------------------------------------------------------
	 * ■컨트롤러, 서비스 메소드 실행 AOP ■payletter ■2018-09-28
	 --------------------------------------------------------------------**/
	private Object executeMethod(ProceedingJoinPoint objProceedingJoinPoint) throws Throwable {

		String methodInfo = String.format("%s/%s",objProceedingJoinPoint.getSignature().getDeclaringTypeName(), objProceedingJoinPoint.getSignature().getName());
		// 메서드 실행 전
		logger.info("### {} : Start", methodInfo);

		StringBuilder objSB = new StringBuilder();
		int intArgs = objProceedingJoinPoint.getArgs().length;

		for(Object object : objProceedingJoinPoint.getArgs()) {
			if(object != null) {
				objSB.append(CommonUtil.getObjectInfo(object));
			} else {
				objSB.append(object);
			}

			objSB.append(--intArgs != 0 ? ", " : "");
		}

		logger.info("### {} : ARGS - {}", methodInfo, objSB.toString());

		// 메서드 실행
		Object objResult = objProceedingJoinPoint.proceed();

		// 메서드 실행 후
		logger.info("### {} : RESULT - {}", methodInfo,  objResult);
		logger.info("### {} : End\n\n", methodInfo);

		return objResult;
	}

	/**--------------------------------------------------------------------
	 * ■서비스에서 페이징 실제 처리 ■payletter ■2018-09-28
	 --------------------------------------------------------------------**/
	private void servicePaging(BaseModel baseModel) {
		if (baseModel.getLength() != 0) {
			Integer intPageNo = (baseModel.getStart() / baseModel.getLength()) + 1;
			int intPageFetchNo = (intPageNo - 1) * baseModel.getLength();

			baseModel.setPageNo(intPageNo);
			baseModel.setPageSize(baseModel.getLength());
			baseModel.setPageFechNo(intPageFetchNo);
		}
	}

	/**--------------------------------------------------------------------
	 * ■서비스에서 언어코드 세팅 ■payletter ■2018-09-28
	 --------------------------------------------------------------------**/
	private void setLangCode(ProceedingJoinPoint objProceedingJoinPoint) {
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
		HttpSession session = request.getSession();
		String langCode = (String)session.getAttribute(GlobalConstants.SESSION_LANG_INFO);
		langCode = langCode == null ? "ko" : langCode;

		for(Object object : objProceedingJoinPoint.getArgs()) {
			if (object instanceof BaseModel) {
				((BaseModel) object).setLangCode(langCode);
			}
		}
	}
}
