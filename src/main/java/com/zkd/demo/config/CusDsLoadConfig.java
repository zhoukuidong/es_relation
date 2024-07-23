package com.zkd.demo.config;

import com.baomidou.dynamic.datasource.DynamicRoutingDataSource;
import com.baomidou.dynamic.datasource.ds.ItemDataSource;
import com.baomidou.dynamic.datasource.toolkit.DynamicDataSourceContextHolder;
import com.zkd.demo.api.CusDsApi;
import com.zkd.demo.holder.CusDsContextHolder;
import com.zkd.demo.properties.CusDsConfigProperties;
import com.zkd.demo.properties.CusDsProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.util.function.Supplier;

/**
 * @desc 动态数据源配置
 */
@ConditionalOnProperty(
        prefix = CusDsConfigProperties.PREFIX,
        havingValue = "true",
        name = "enable")
@Component
@Slf4j
public class CusDsLoadConfig {

    @Autowired(required = false)
    private CusDsApi cusDsApi;
    @Resource
    private DataSource dataSource;
    @Resource
    private CusDsConfigProperties cusDsConfigProperties;

    /**
     * 添加数据源
     *
     * @param dataSourceCode
     * @return boolean
     */
    public boolean addDs(String dataSourceCode) {

        // 如果没读取到配置，则返回false，使用默认数据源
        CusDsProperties cusDsProperties = cusDsApi.getDataSourceProperty(dataSourceCode);
        if (cusDsProperties == null || Boolean.TRUE.equals(cusDsProperties.getIfMaster())) {
            log.info("specialized data source: {} not found, using default data source:{}", dataSourceCode, cusDsConfigProperties.getPrimary());
            return false;
        }

        if (ifDsExist(dataSourceCode)) {
            log.info("{} data source already exists, no need to create", dataSourceCode);
            return true;
        }

        String username = cusDsProperties.getUsername();
        String password = cusDsProperties.getPassword();

        createDataSource(dataSourceCode, cusDsProperties.getUrl(), username, password);
        return true;

    }

    /**
     * 决定使用哪个数据源
     *
     * @param dataSourceCode
     */
    public void determineDs(String dataSourceCode) {
        boolean addResult = this.addDs(dataSourceCode);
        cusDsApi.setCurrentDs(addResult ? dataSourceCode : cusDsConfigProperties.getPrimary());
    }

    /**
     * 如果需要在方法体执行过程中切换数据源，调用此方法，传入业务方法体，在业务方法执行结束后弹出最新设置的数据源，恢复到入栈之前
     *
     * @param dataSourceCode
     * @param supplier
     * @return {@link T}
     */
    public <T> T determineDs(String dataSourceCode, Supplier<T> supplier) {
        String backUpDs = CusDsContextHolder.getCurrentThreadPrimaryDs();
        this.determineDs(dataSourceCode);
        try {
            return supplier.get();
        } finally {
            DynamicDataSourceContextHolder.poll();
            CusDsContextHolder.poll();
            // 执行完后将当前主数据源恢复成执行前的状态
            CusDsContextHolder.setCurrentThreadPrimaryDs(backUpDs);
        }
    }

    /**
     * 判断当前数据源是否已经创建过
     *
     * @param dataSourceCode
     * @return boolean
     */
    public boolean ifDsExist(String dataSourceCode) {
        DynamicRoutingDataSource dynamicRoutingDataSource = ((DynamicRoutingDataSource) dataSource);
        DataSource currentDs = dynamicRoutingDataSource.getDataSource(dataSourceCode);

        if (currentDs instanceof ItemDataSource) {
            ItemDataSource itemDs = (ItemDataSource) currentDs;
            String itemDsCode = itemDs.getName();
            if (dataSourceCode.equals(itemDsCode)) {
                // 已创建过该ds，直接返回
                return true;
            }
        }
        return false;
    }

    /**
     * 创建数据源连接并加入到数据源集合中
     *
     * @param dataSourceCode
     * @param url
     * @param username
     * @param password
     */
    public void createDataSource(String dataSourceCode, String url, String username, String password) {
        // 保证同一个数据源名称的连接只会被创建一次，进行加锁处理，加锁成功之后再次判断
        synchronized (this) {
            boolean exist = ifDsExist(dataSourceCode);
            if (exist) {
                log.info("{} data source already exists, no need to create in lock", dataSourceCode);
                return;
            }
            DataSource ds = buildDataSource(dataSourceCode,
                    url,
                    username, password);
            DynamicRoutingDataSource dynamicRoutingDataSource = ((DynamicRoutingDataSource) dataSource);
            dynamicRoutingDataSource.addDataSource(dataSourceCode, ds);
        }
    }

    /**
     * 建立数据库连接
     *
     * @param dataSourceCode
     * @param url
     * @param username
     * @param password
     * @return {@link DataSource}
     */
    public DataSource buildDataSource(String dataSourceCode, String url, String username, String password) {
        DataSource ds = DataSourceBuilder.create()
                .url(url)
                .username(username)
                .password(password)
                .build();
        return new ItemDataSource(dataSourceCode,
                ds, ds,
                cusDsConfigProperties.getP6spy(),
                cusDsConfigProperties.getSeata(),
                cusDsConfigProperties.getSeataMode());
    }

}
