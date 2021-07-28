package com.sbank.admin.common.resolver;

import com.sbank.admin.common.domain.SessionInfo;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**--------------------------------------------------------------------
 * ■파라미터를 정의된 타입으로 변환하여 Controller에 전달하는 클래스 ■payletter ■2018-09-28
 --------------------------------------------------------------------**/
public class ArgumentResolver implements HandlerMethodArgumentResolver {
	@Override
	public Object resolveArgument(MethodParameter methodParameter, ModelAndViewContainer modelAndViewContainer,
			NativeWebRequest nativeWebRequest, WebDataBinderFactory webDataBinderFactory) throws Exception {
    	HttpServletRequest objHttpServletRequest = (HttpServletRequest) nativeWebRequest.getNativeRequest();
        Class<?> parameterType = methodParameter.getParameterType();

        if (parameterType.equals(RequestParameterMap.class)) {
    		RequestAdapter reqAdapter = new RequestAdapter();

    		RequestParameterMap<String, Object> paramMap = new RequestParameterMap<>();

			paramMap.setMap((Map<String, Object>)reqAdapter.convert((HttpServletRequest) nativeWebRequest.getNativeRequest()));

    		return paramMap;
        }

    	SessionInfo sessionInfo = new SessionInfo();

		sessionInfo.setAdminNo(objHttpServletRequest.getAttribute("adminNo") == null ? 0 : (int) objHttpServletRequest.getAttribute("adminNo"));
		sessionInfo.setAdminID((String) objHttpServletRequest.getAttribute("adminID"));

        return sessionInfo;
	}

	@Override
	public boolean supportsParameter(MethodParameter methodParameter) {
		if (methodParameter.getParameterType().equals(RequestParameterMap.class)) {
			return RequestParameterMap.class.isAssignableFrom(methodParameter.getParameterType());
		}

        return SessionInfo.class.isAssignableFrom(methodParameter.getParameterType());
	}
}
