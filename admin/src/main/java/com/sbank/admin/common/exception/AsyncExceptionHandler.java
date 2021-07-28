package com.sbank.admin.common.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.beans.factory.annotation.Value;

import java.lang.reflect.Method;

/**--------------------------------------------------------------------
 * ■동기화 예외처리 핸들러 ■payletter
 --------------------------------------------------------------------**/
public class AsyncExceptionHandler implements AsyncUncaughtExceptionHandler {
	private static final Logger logger = LoggerFactory.getLogger(AsyncExceptionHandler.class);

	@Value("${system.mail.receiver}")
	private String systemMailReceiver;

	@Value("${location}")
	private String location;

	@Override
	public void handleUncaughtException(Throwable throwable, Method method, Object... obj) {
		logger.info("handleUncaughtException throwable {}", throwable.getMessage());
		logger.info("handleUncaughtException method    {}", method.getName());

		for(Object param : obj) {
			logger.info("handleUncaughtException parameter {}", param);
		}
	}
}
