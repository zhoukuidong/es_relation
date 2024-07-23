package com.zkd.demo.config;

import cn.hutool.core.thread.NamedThreadFactory;
import com.alibaba.ttl.threadpool.TtlExecutors;
import com.zkd.demo.executor.CusDsExecutor;
import com.zkd.demo.executor.enums.QueueTypeEnum;
import com.zkd.demo.executor.reject.RejectHandlerGetter;
import com.zkd.demo.properties.CusDsConfigProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;
import java.util.concurrent.*;

/**
 * @author zkd
 * @date 2021/10/16
 * @desc 动态数据源加载配置
 */
@ConditionalOnProperty(
        prefix = CusDsConfigProperties.PREFIX,
        havingValue = "true",
        name = "enable")
@Configuration(proxyBeanMethods = false)
@Slf4j
public class CusDsExecutorConfig {

    @Resource
    private CusDsConfigProperties cusDsConfigProperties;

    @Bean(name = "cusDsExecutor")
    public ExecutorService createExecutor() {
        CusDsConfigProperties.ExecutorProperties executorProperties = cusDsConfigProperties.getExecutorProperties();
        String queueType = executorProperties.getQueueType();

        BlockingQueue<Runnable> blockingQueue = QueueTypeEnum.buildBq(queueType, executorProperties.getCapacity(), executorProperties.getFair());
        RejectedExecutionHandler rejectedExecutionHandler = RejectHandlerGetter.getProxy(executorProperties.getRejectPolicy());

        ThreadFactory namedThreadFactory = new NamedThreadFactory(executorProperties.getPoolNamePrefix(), false);
        ThreadPoolExecutor threadPoolExecutor = new CusDsExecutor(
                executorProperties.getCorePoolSize(),
                executorProperties.getMaximumPoolSize(),
                executorProperties.getKeepAliveTime(),
                executorProperties.getTimeUnit(),
                blockingQueue, namedThreadFactory, rejectedExecutionHandler
        );
        return TtlExecutors.getTtlExecutorService(threadPoolExecutor);
    }

}
