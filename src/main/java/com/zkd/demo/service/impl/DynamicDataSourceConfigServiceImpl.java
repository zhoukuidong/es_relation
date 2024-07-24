package com.zkd.demo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zkd.demo.entity.DynamicDataSourceConfig;
import com.zkd.demo.mapper.DynamicDataSourceConfigMapper;
import com.zkd.demo.properties.CusDsConfigProperties;
import com.zkd.demo.service.DynamicDataSourceConfigService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @description 针对表【dynamic_data_source_config(动态数据源配置表)】的数据库操作Service实现
 * @createDate 2023-08-08 15:15:48
 */
@ConditionalOnProperty(
        prefix = CusDsConfigProperties.PREFIX,
        havingValue = "true",
        name = "enable")
@Service
public class DynamicDataSourceConfigServiceImpl extends ServiceImpl<DynamicDataSourceConfigMapper, DynamicDataSourceConfig>
        implements DynamicDataSourceConfigService {

    /**
     * @return {@link List}<{@link DynamicDataSourceConfig}>
     */
    public List<DynamicDataSourceConfig> listAllDynamicDataSourceConfig() {
        LambdaQueryWrapper<DynamicDataSourceConfig> queryWrapper = Wrappers.lambdaQuery(DynamicDataSourceConfig.class)
                .eq(DynamicDataSourceConfig::getIfDelete, Boolean.FALSE);
        List<DynamicDataSourceConfig> dynamicDataSourceConfigs = this.list(queryWrapper);
        return dynamicDataSourceConfigs;
    }

    /**
     * @param dataSourceCode
     * @return {@link DynamicDataSourceConfig}
     */
    public DynamicDataSourceConfig getByDataSourceCode(String dataSourceCode) {
        LambdaQueryWrapper<DynamicDataSourceConfig> queryWrapper = Wrappers.lambdaQuery(DynamicDataSourceConfig.class)
                .eq(DynamicDataSourceConfig::getIfDelete, Boolean.FALSE)
                .eq(DynamicDataSourceConfig::getDatasourceCode, dataSourceCode)
                .last("limit 1");
        DynamicDataSourceConfig dynamicDataSourceConfig = this.getOne(queryWrapper);
        return dynamicDataSourceConfig;
    }

}




