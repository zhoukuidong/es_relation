package com.zkd.demo.basic.annotation;

import com.zkd.demo.basic.core.advice.DefaultExceptionAdvice;
import com.zkd.demo.basic.core.selector.ExceptionHandlerSelector;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Import({ExceptionHandlerSelector.class})
public @interface EnableExceptionHandler {
    Class[] value() default {DefaultExceptionAdvice.class};
}
