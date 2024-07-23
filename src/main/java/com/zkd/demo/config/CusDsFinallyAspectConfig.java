package com.zkd.demo.config;

import com.zkd.demo.holder.CusDsContextHolder;
import com.zkd.demo.properties.CusDsConfigProperties;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * @author zkd
 * @date 2021/10/15
 */
@ConditionalOnProperty(
        prefix = CusDsConfigProperties.PREFIX,
        havingValue = "true",
        name = "enable")
@Aspect
@Component
@Slf4j
public class CusDsFinallyAspectConfig {

    @Pointcut("@within(com.zkd.demo.annotation.CusDsFinally) || @annotation(com.zkd.demo.annotation.CusDsFinally)")
    public void handlingPointcut() {
    }

    @Around("handlingPointcut()")
    public Object handlingAround(ProceedingJoinPoint joinPoint) throws Throwable {
        try {
            Object proceed = joinPoint.proceed();
            return proceed;
        } catch (Throwable e) {
            throw e;
        } finally {
            CusDsContextHolder.removeAll();
        }
    }
}