package com.zkd.demo.annotation;

import java.lang.annotation.*;

/**
 * @author zkd
 * @date 2021/10/15
 * @desc 该注解只加在第一层执行的方法即可
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CusDsFinally {

}
