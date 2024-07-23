package com.zkd.demo.config;

import com.baomidou.dynamic.datasource.toolkit.DynamicDataSourceContextHolder;
import com.zkd.demo.annotation.CusDs;
import com.zkd.demo.holder.CusDsContextHolder;
import com.zkd.demo.properties.CusDsConfigProperties;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.lang.reflect.Method;

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
public class CusDsAspectConfig {

    @Resource
    private CusDsLoadConfig cusDsLoadConfig;

    @Pointcut("@within(com.zkd.demo.annotation.CusDs) || @annotation(com.zkd.demo.annotation.CusDs)")
    public void handlingPointcut() {
    }

    @Around("handlingPointcut()")
    public Object handlingAround(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        // 先取方法上的注解
        CusDs cusDsAnnotation = method.getAnnotation(CusDs.class);
        if (cusDsAnnotation == null) {
            cusDsAnnotation = joinPoint.getTarget().getClass().getAnnotation(CusDs.class);
        }

        String dsName = cusDsAnnotation == null ? null : cusDsAnnotation.value();
        if (!StringUtils.hasText(dsName) && cusDsAnnotation.useThreadLocalDs()) {
            dsName = CusDsContextHolder.getCurrentThreadPrimaryDs();
        }
        boolean ifPush = false;
        if (StringUtils.hasText(dsName)) {
            ifPush = true;
        }
        try {
            if (ifPush) {
                CusDsContextHolder.push(dsName);
                DynamicDataSourceContextHolder.push(CusDsContextHolder.peek());
                cusDsLoadConfig.addDs(dsName);
            }
            Object proceed = joinPoint.proceed();
            return proceed;
        } catch (Throwable e) {
            throw e;
        } finally {
            if (ifPush) {
                DynamicDataSourceContextHolder.poll();
                CusDsContextHolder.poll();
            }
        }
    }
}