package com.zkd.demo.config;

import com.baomidou.dynamic.datasource.aop.DynamicDataSourceAnnotationAdvisor;
import com.baomidou.dynamic.datasource.spring.boot.autoconfigure.DynamicDataSourceProperties;

import com.zkd.demo.annotation.CusDsTransactional;
import com.zkd.demo.interceptor.CusDsLocalTransactionInterceptor;
import com.zkd.demo.properties.CusDsConfigProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.Advisor;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Role;

/**
 * @desc 多数据源事务配置
*/
@ConditionalOnProperty(
        prefix = CusDsConfigProperties.PREFIX,
        havingValue = "true",
        name = "enable")
@Configuration(proxyBeanMethods = false)
@Slf4j
public class CusDsTransactionConfig {

    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    @Bean
    @ConditionalOnProperty(prefix = DynamicDataSourceProperties.PREFIX, name = "seata", havingValue = "false", matchIfMissing = true)
    public Advisor cusDsTransactionAdvisor() {
        CusDsLocalTransactionInterceptor interceptor = new CusDsLocalTransactionInterceptor();
        return new DynamicDataSourceAnnotationAdvisor(interceptor, CusDsTransactional.class);
    }

}
