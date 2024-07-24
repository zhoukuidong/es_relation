package com.zkd.demo.invoke;

import cn.hutool.core.lang.Singleton;
import com.baomidou.dynamic.datasource.DynamicRoutingDataSource;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.zkd.demo.api.CusDsApi;
import com.zkd.demo.api.CusDsCryptoApi;
import com.zkd.demo.entity.DynamicDataSourceConfig;
import com.zkd.demo.properties.CusDsProperties;
import com.zkd.demo.service.DynamicDataSourceConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Slf4j
public class CusDsApiInvoke implements CusDsApi {

    @Autowired(required = false)
    private CusDsCryptoApi cusDsCryptoApi;
    @Autowired(required = false)
    private DynamicDataSourceConfigService dynamicDataSourceConfigService;
    @Resource
    private DataSource dataSource;

    @Override
    @DS("master")
    public Map<String, CusDsProperties> getAllDataSourceProperties() {

        Map<String, CusDsProperties> allDataSourceProperties = Singleton.get("allDataSourceProperties", () -> {
            LambdaQueryWrapper<DynamicDataSourceConfig> queryWrapper = Wrappers.lambdaQuery(DynamicDataSourceConfig.class)
                    .eq(DynamicDataSourceConfig::getIfDelete, Boolean.FALSE);
            List<DynamicDataSourceConfig> dynamicDataSourceConfigs = dynamicDataSourceConfigService.list(queryWrapper);
            if (CollectionUtils.isEmpty(dynamicDataSourceConfigs)) {
                return new HashMap<>(2);
            }

            Map<String, CusDsProperties> resMap = dynamicDataSourceConfigs.stream().map(v -> {
                String username = v.getEnableCrypto() && cusDsCryptoApi != null ? cusDsCryptoApi.decrypt(v.getSecret(), v.getUsername()) : v.getUsername();
                String password = v.getEnableCrypto() && cusDsCryptoApi != null ? cusDsCryptoApi.decrypt(v.getSecret(), v.getPassword()) : v.getPassword();

                CusDsProperties cusDsProperties = new CusDsProperties();
                cusDsProperties.setEnableCrypto(v.getEnableCrypto());
                cusDsProperties.setDataSourceCode(v.getDatasourceCode());
                cusDsProperties.setUrl(v.getUrl());
                cusDsProperties.setUsername(username);
                cusDsProperties.setPassword(password);
                cusDsProperties.setSecret(v.getSecret());
                return cusDsProperties;
            }).collect(Collectors.toMap(k -> k.getDataSourceCode(), v -> v, (v1, v2) -> v2));
            return resMap;
        });
        return allDataSourceProperties;

    }

    /**
     * @param dataSourceCode
     * @return {@link CusDsProperties}
     */
    @Override
    @DS("master")
    public CusDsProperties getDataSourceProperty(String dataSourceCode) {
        CusDsProperties cusDsProperties = Singleton.get(dataSourceCode, () -> {
            Map<String, CusDsProperties> allDataSourceProperties = this.getAllDataSourceProperties();
            CusDsProperties properties = allDataSourceProperties.get(dataSourceCode);
            if (properties == null) {
                CusDsProperties masterProperty = new CusDsProperties();
                masterProperty.setIfMaster(true);
                return masterProperty;
            } else {
                return properties;
            }
        });
        return cusDsProperties;
    }

    @Override
    public boolean removeDataSource(String dataSourceCode) {
        Singleton.remove(dataSourceCode);
        DynamicRoutingDataSource dynamicRoutingDataSource = ((DynamicRoutingDataSource) dataSource);
        dynamicRoutingDataSource.removeDataSource(dataSourceCode);
        return true;
    }
}



