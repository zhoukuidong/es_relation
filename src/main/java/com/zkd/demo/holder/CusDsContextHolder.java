package com.zkd.demo.holder;

import com.alibaba.ttl.TransmittableThreadLocal;
import com.baomidou.dynamic.datasource.toolkit.DynamicDataSourceContextHolder;
import org.springframework.util.StringUtils;

import java.util.ArrayDeque;
import java.util.Deque;

public final class CusDsContextHolder {

    /**
     * 当前线程的主数据源名称
     */
    public static final ThreadLocal<String> CURRENT_THREAD_PRIMARY_DS = new TransmittableThreadLocal<>();
    /**
     * 处理嵌套执行的时候，需要使用队列处理
     */
    public static final ThreadLocal<Deque<String>> CURRENT_THREAD_DS_DEQUE = new TransmittableThreadLocal() {
        @Override
        protected Deque<String> initialValue() {
            return new ArrayDeque<>();
        }
    };

    /**
     * 设置当前主线程数据源状态
     *
     * @param ds
     * @return {@link String}
     */
    public static String setCurrentThreadPrimaryDs(String ds) {
        String dataSourceStr = StringUtils.hasText(ds) ? ds : "";
        CURRENT_THREAD_PRIMARY_DS.set(dataSourceStr);
        return dataSourceStr;
    }

    /**
     * 获取当前主线程数据源状态
     *
     * @return {@link String}
     */
    public static String getCurrentThreadPrimaryDs() {
        return CURRENT_THREAD_PRIMARY_DS.get();
    }

    /**
     * 清空当前主线程数据源状态
     */
    public static void removeCurrentThreadPrimaryDs() {
        CURRENT_THREAD_PRIMARY_DS.remove();
    }

    /**
     * 获得当前线程数据源
     *
     * @return 数据源名称
     */
    public static String peek() {
        return CURRENT_THREAD_DS_DEQUE.get().peek();
    }

    /**
     * 设置当前线程数据源
     * 数据源嵌套使用情况下调用
     *
     * @param ds 数据源名称
     * @return {@link String}
     */
    public static String push(String ds) {
        String dataSourceStr = StringUtils.hasText(ds) ? ds : "";
        CURRENT_THREAD_DS_DEQUE.get().push(dataSourceStr);
        return dataSourceStr;
    }

    /**
     * 清空当前线程数据源
     * 如果当前线程是连续切换数据源 只会移除掉当前线程的数据源名称
     */
    public static void poll() {
        Deque<String> deque = CURRENT_THREAD_DS_DEQUE.get();
        deque.poll();
        if (deque.isEmpty()) {
            CURRENT_THREAD_DS_DEQUE.remove();
        }
    }

    /**
     * 强制清空本地线程
     */
    public static void clear() {
        CURRENT_THREAD_DS_DEQUE.remove();
    }

    /**
     * 清空数据源状态
     */
    public static void removeAll() {
        DynamicDataSourceContextHolder.clear();
        CusDsContextHolder.removeCurrentThreadPrimaryDs();
        CusDsContextHolder.clear();
    }
}
