package com.zkd.demo.annotation;

import java.lang.annotation.*;

/**
 * @author zkd
 * @date 2021/10/15
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CusDs {

    String value() default "";

    /**
     * 是否使用线程池指定的数据源
     * 调用CusDsContextHolder.setCurrentThreadPrimaryDs("数据源名称");
     * @return
     */
    boolean useThreadLocalDs() default false;

}
