package com.zkd.demo.executor;

import com.baomidou.dynamic.datasource.toolkit.DynamicDataSourceContextHolder;
import com.zkd.demo.holder.CusDsContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.util.concurrent.*;

@Slf4j
public class CusDsExecutor extends ThreadPoolExecutor {
    public CusDsExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
    }

    public CusDsExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory);
    }

    public CusDsExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, RejectedExecutionHandler handler) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, handler);
    }

    public CusDsExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory, RejectedExecutionHandler handler) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler);
    }

    @Override
    public void execute(Runnable command) {
        String ds = determineDs();
        super.execute(() -> {
            try {
                DynamicDataSourceContextHolder.push(ds);
                command.run();
            } finally {
                DynamicDataSourceContextHolder.poll();
            }
        });
    }

    public String determineDs() {
        String peek = DynamicDataSourceContextHolder.peek();
        String dataSourceKey = peek;
        if (!StringUtils.hasText(peek)) {
            String currentThreadPrimaryDs = CusDsContextHolder.getCurrentThreadPrimaryDs();
            if (StringUtils.hasText(currentThreadPrimaryDs)) {
                dataSourceKey = currentThreadPrimaryDs;
            }
            log.info("current thread name:{}", Thread.currentThread().getName());
        }
        return dataSourceKey;
    }
}
