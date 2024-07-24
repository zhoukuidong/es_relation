package com.zkd.demo.basic.core.cors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@ConditionalOnProperty(
        prefix = "custom.cors",
        name = {"enable"},
        havingValue = "true"
)
@ConditionalOnWebApplication
@Configuration
public class CorsAutoConfiguration {
    @Autowired
    private CorsProperties properties;

    public CorsAutoConfiguration() {
    }

    @Bean
    @ConditionalOnMissingBean({CorsFilter.class})
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        this.properties.getAllowedOrigin().forEach(config::addAllowedOrigin);
        config.setAllowCredentials(true);
        this.properties.getAllowedMethod().forEach(config::addAllowedMethod);
        this.properties.getAllowedHeader().forEach(config::addAllowedHeader);
        config.addExposedHeader(this.properties.getExposedHeader());
        config.setMaxAge(this.properties.getMaxAge());
        UrlBasedCorsConfigurationSource corsConfigurationSource = new UrlBasedCorsConfigurationSource();
        corsConfigurationSource.registerCorsConfiguration("/**", config);
        return new CorsFilter(corsConfigurationSource);
    }
}