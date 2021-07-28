package com.sbank.admin.common.annotation;

import com.sbank.admin.common.util.CommonUtil;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**--------------------------------------------------------------------
 * ■권한 클래스 ■payletter ■2018-09-28
 --------------------------------------------------------------------**/
@Target(value=ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AuthMethod {
	CommonUtil.UserAuth hasAuth() default CommonUtil.UserAuth.READONLY;
}
