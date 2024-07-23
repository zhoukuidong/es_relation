package com.zkd.demo.config;

import com.zkd.demo.interceptor.CusDsWebInterceptor;
import com.zkd.demo.properties.CusDsConfigProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;

@ConditionalOnProperty(
        prefix = CusDsConfigProperties.PREFIX,
        havingValue = "true",
        name = "enable")
@Component
public class CusDsWebConfig implements WebMvcConfigurer {

    @Resource
    private CusDsConfigProperties cusDsConfigProperties;
    @Autowired
    private CusDsLoadConfig cusDsLoadConfig;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new CusDsWebInterceptor(cusDsConfigProperties, cusDsLoadConfig))
                .addPathPatterns("/**");
    }
}
