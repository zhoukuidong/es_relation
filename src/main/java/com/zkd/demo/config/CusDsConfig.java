package com.zkd.demo.config;

import com.zkd.demo.properties.CusDsConfigProperties;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import javax.annotation.PostConstruct;

/**
 * @author zkd
 * @date 2021/10/15
 * @desc 动态数据源加载配置
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(
        prefix = CusDsConfigProperties.PREFIX,
        havingValue = "true",
        name = "enable")
@ComponentScan("com.zkd.demo")
@Slf4j
@Import(CusDsLoadConfig.class)
@MapperScan(value = "com.zkd.demo.mapper")
public class CusDsConfig {

    @PostConstruct
    public void init() {
        log.info("cus dynamic data source is initialized");
    }

}
