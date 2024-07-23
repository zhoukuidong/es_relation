package com.zkd.demo.executor.reject;

import com.zkd.demo.executor.enums.RejectedTypeEnum;
import com.zkd.demo.executor.ex.CusDsExecutorException;
import com.zkd.demo.executor.handler.RejectedInvocationHandler;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Proxy;
import java.util.Objects;
import java.util.ServiceLoader;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

@Slf4j
public class RejectHandlerGetter {

    private RejectHandlerGetter() {
    }

    public static RejectedExecutionHandler buildRejectedHandler(String name) {
        if (Objects.equals(name, RejectedTypeEnum.ABORT_POLICY.getName())) {
            return new ThreadPoolExecutor.AbortPolicy();
        } else if (Objects.equals(name, RejectedTypeEnum.CALLER_RUNS_POLICY.getName())) {
            return new ThreadPoolExecutor.CallerRunsPolicy();
        } else if (Objects.equals(name, RejectedTypeEnum.DISCARD_OLDEST_POLICY.getName())) {
            return new ThreadPoolExecutor.DiscardOldestPolicy();
        } else if (Objects.equals(name, RejectedTypeEnum.DISCARD_POLICY.getName())) {
            return new ThreadPoolExecutor.DiscardPolicy();
        }

        ServiceLoader<RejectedExecutionHandler> serviceLoader = ServiceLoader.load(RejectedExecutionHandler.class);
        for (RejectedExecutionHandler handler : serviceLoader) {
            String handlerName = handler.getClass().getSimpleName();
            if (name.equalsIgnoreCase(handlerName)) {
                return handler;
            }
        }

        log.error("Cannot find specified rejectedHandler {}", name);
        throw new CusDsExecutorException("Cannot find specified rejectedHandler " + name);
    }

    public static RejectedExecutionHandler getProxy(String name) {
        return getProxy(buildRejectedHandler(name));
    }

    public static RejectedExecutionHandler getProxy(RejectedExecutionHandler handler) {
        return (RejectedExecutionHandler) Proxy
                .newProxyInstance(handler.getClass().getClassLoader(),
                        new Class[]{RejectedExecutionHandler.class},
                        new RejectedInvocationHandler(handler));
    }
}
