package com.zkd.demo.properties;

import com.baomidou.dynamic.datasource.enums.SeataMode;
import com.zkd.demo.executor.enums.QueueTypeEnum;
import com.zkd.demo.executor.enums.RejectedTypeEnum;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * @desc 动态加载数据源配置属性类
 */
@Data
@Configuration
@ConfigurationProperties(prefix = CusDsConfigProperties.PREFIX)
@RefreshScope
public class CusDsConfigProperties {


    public static final String PREFIX = "cus-dynamic-data-source";

    /**
     * 是否开启数据源加载信息加解密，默认不开启
     */
    private Boolean enable = false;
    /**
     * 必须设置默认的库,默认master
     */
    private String primary = "master";
    /**
     * 是否使用p6spy输出，默认不输出
     */
    private Boolean p6spy = false;
    /**
     * 是否使用开启seata，默认不开启
     */
    private Boolean seata = false;
    /**
     * seata使用模式，默认AT
     */
    private SeataMode seataMode = SeataMode.AT;

    /**
     * 默认加解密密钥
     */
    private String secret = "I6JozG0ErOFdtppr";
    /**
     * 是否开启加载web前置拦截处理
     */
    private Boolean enableWebPreHandle = true;
    /**
     * 是否开启{@link org.springframework.transaction.annotation.Transactional}注解，默认不开启，因为多数据源场景下如果开启会导致多数据源切换问题
     */
    private Boolean enableTransactionalAnnotation = false;
    /**
     * 决定切换数据源的请求头变量名称
     */
    private String determineDsParam = "dataSource";

    private ExecutorProperties executorProperties = new ExecutorProperties();

    @Data
    public static class ExecutorProperties {
        /**
         * 核心线程数
         */
        private Integer corePoolSize = Runtime.getRuntime().availableProcessors() * 2 + 1;
        /**
         * 最大线程数
         */
        private Integer maximumPoolSize = 100;
        /**
         * 线程存活时间，单位毫秒
         */
        private Long keepAliveTime = 5000L;
        private TimeUnit timeUnit = TimeUnit.MILLISECONDS;
        private Integer capacity = 1024;
        /**
         * 阻塞队列类型
         */
        private String queueType = QueueTypeEnum.LINKED_BLOCKING_QUEUE.getName();
        private String rejectPolicy = RejectedTypeEnum.CALLER_RUNS_POLICY.getName();
        private Boolean fair = false;
        private String poolNamePrefix = "cus-ds-tp-";

    }

}