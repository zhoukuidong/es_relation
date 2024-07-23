package com.zkd.demo.annotation;

import java.lang.annotation.*;


/**
 * @author zkd
 * @date 2021/10/15
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CusDsTransactional {

    /**
     * 指定需要事务回滚的异常类型，默认只回滚{@link RuntimeException}
     * @return {@link Class}<{@link ?} {@link extends} {@link Throwable}>{@link []}
     */
    Class<? extends Throwable>[] rollbackFor() default {RuntimeException.class};
}
