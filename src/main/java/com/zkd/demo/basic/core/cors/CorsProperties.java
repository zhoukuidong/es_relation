package com.zkd.demo.basic.core.cors;

import lombok.Data;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@ConditionalOnProperty(prefix = "custom.cors", name = "enable", havingValue = "true")
@ConfigurationProperties(prefix = "custom.cors")
@Component
@Data
public class CorsProperties {
    /**
     * 是否开启跨域
     */
    private boolean enable = false;
    /**
     * 放行哪些原始域
     */
    private List<String> allowedOrigin = Collections.singletonList("*");
    /**
     * 放行哪些请求方式
     */
    private List<String> allowedMethod = Collections.singletonList("*");
    /**
     * 放行哪些原始请求头部信息
     */
    private List<String> allowedHeader = Collections.singletonList("*");
    /**
     * 暴露哪些头部信息
     */
    private String exposedHeader = "";

    private long maxAge = 3600L;
}

