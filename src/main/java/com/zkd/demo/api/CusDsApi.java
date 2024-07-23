package com.zkd.demo.api;

import com.baomidou.dynamic.datasource.toolkit.DynamicDataSourceContextHolder;
import com.zkd.demo.holder.CusDsContextHolder;
import com.zkd.demo.properties.CusDsProperties;


import java.util.HashMap;
import java.util.Map;

/**
 * @author zkd
 * @date 2021/10/15
 * @desc 动态数据源信息获取api
 */
public interface CusDsApi {

    /**
     * 获取所有数据源配置属性
     *
     * @return {@link Map}<{@link String}, {@link CusDsProperties}>
     */
    default Map<String, CusDsProperties> getAllDataSourceProperties() {
        return new HashMap<>(8);
    }

    /**
     * 获取指定数据源配置属性
     *
     * @param dataSourceCode
     * @return {@link CusDsProperties}
     */
    default CusDsProperties getDataSourceProperty(String dataSourceCode) {
        return null;
    }

    /**
     * 获取当前数据源
     * @return
     */
    default String getCurrentDs() {
        return DynamicDataSourceContextHolder.peek();
    }

    /**
     * 获取当前数据源
     * @return
     */
    default String getCurrentThreadPrimaryDs() {
        return CusDsContextHolder.getCurrentThreadPrimaryDs();
    }

    /**
     * 设置当前数据源
     * @param dataSource
     */
    default void setCurrentDs(String dataSource) {
        DynamicDataSourceContextHolder.push(dataSource);
        CusDsContextHolder.setCurrentThreadPrimaryDs(dataSource);
        CusDsContextHolder.push(dataSource);
        return;
    }

    /**
     * 删除数据源
     *
     * @param dataSourceCode
     * @return boolean
     */
    default boolean removeDataSource(String dataSourceCode) {
        return true;
    }
}
