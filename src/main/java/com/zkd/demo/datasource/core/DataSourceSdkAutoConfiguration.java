package com.zkd.demo.datasource.core;

import com.zkd.demo.datasource.service.DataSourceService;
import com.zkd.demo.datasource.service.impl.DataHubServiceImpl;
import com.zkd.demo.datasource.service.impl.DefaultDataSourceServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;


@Slf4j
@Configuration
public class DataSourceSdkAutoConfiguration {

    @Bean
    @Primary
    public DataSourceService dataSourceService() {
        log.info(" >>>>> 默认数据源模式初始化");
        return new DefaultDataSourceServiceImpl();
    }

    @Bean
    public DataSourceService dataHubService() {
        log.info(" >>>>> datahub 数据源模式初始化");
        return new DataHubServiceImpl();
    }
}
