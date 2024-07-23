package com.zkd.demo.annotation;

import java.lang.annotation.*;

/**
 * @author zkd
 * @date 2021/10/15
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CusIngoreDs {

    String name() default "";
}
