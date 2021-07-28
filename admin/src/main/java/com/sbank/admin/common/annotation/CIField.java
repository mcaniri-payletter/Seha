package com.sbank.admin.common.annotation;

import com.sbank.admin.common.util.CommonUtil;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**--------------------------------------------------------------------
 * ■CI Field 어노테이션 ■payletter ■2018-09-28
 --------------------------------------------------------------------**/
@Target(value= ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CIField {
    CommonUtil.CIFieldType type();
}
