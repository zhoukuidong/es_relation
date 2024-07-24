package com.zkd.demo.datasource.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zkd.demo.crypto2.type.Sm4Encryptor;
import com.zkd.demo.datasource.core.common.IDataSourceEasyService;
import com.zkd.demo.datasource.entity.DO.TDataSourceDO;
import com.zkd.demo.datasource.entity.DO.TDataSourceLinkValueDO;
import com.zkd.demo.datasource.entity.DO.TDataSourceTypeCodeDO;
import com.zkd.demo.datasource.entity.DTO.Link;
import com.zkd.demo.datasource.entity.enums.DeleteEnum;
import com.zkd.demo.datasource.mapper.TDataSourceLinkValueMapper;
import com.zkd.demo.datasource.mapper.TDataSourceMapper;
import com.zkd.demo.datasource.mapper.TDataSourceTypeCodeMapper;
import com.zkd.demo.datasource.service.DataSourceLinkValueService;
import com.zkd.demo.mybatis.properties.CustomMybatisCryptoProperties;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class DataSourceLinkValueServiceImpl extends ServiceImpl<TDataSourceLinkValueMapper, TDataSourceLinkValueDO> implements DataSourceLinkValueService, IDataSourceEasyService {

    private TDataSourceMapper dataSourceMapper;
    private TDataSourceLinkValueMapper dataSourceLinkValueMapper;
    private TDataSourceTypeCodeMapper dataSourceTypeCodeMapper;
    private CustomMybatisCryptoProperties mybatisCryptoProperties;
    private Sm4Encryptor sm4Encryptor;

    @Override
    public List<Link> queryLinkListBySourceCode(String sourceCode) {
        TDataSourceDO dataSourceDO = queryDataSourceBySc(dataSourceMapper, sourceCode);
        if (null == dataSourceDO || StrUtil.isBlank(dataSourceDO.getSourceTypeCode())) {
            throw new IllegalArgumentException("数据源编码「" + sourceCode + "」对应的数据源信息不存在");
        }
        String sourceTypeCode = dataSourceDO.getSourceTypeCode();
        List<TDataSourceTypeCodeDO> tDataSourceTypeCodeDOS = queryDataSourceTypeCodeListByTc(dataSourceTypeCodeMapper, sourceTypeCode);
        if (CollUtil.isEmpty(tDataSourceTypeCodeDOS)) {
            throw new IllegalArgumentException("数据源编码「" + sourceCode + "」对应的数据源类型「{" + sourceTypeCode + "}」信息不存在");
        }

        List<TDataSourceLinkValueDO> linkValueList = new LambdaQueryChainWrapper<TDataSourceLinkValueDO>(dataSourceLinkValueMapper)
                .eq(TDataSourceLinkValueDO::getDeleteFlag, DeleteEnum.N.getKey())
                .eq(TDataSourceLinkValueDO::getSourceCode, sourceCode)
                .list();
        if (CollUtil.isEmpty(linkValueList)) {
            throw new IllegalArgumentException("数据源编码「" + sourceCode + "」对应的数据源字段值数据为空");
        }
        Map<String, String> linkValueMap = linkValueList.stream().collect(Collectors.toMap(TDataSourceLinkValueDO::getSourceTypeKeyCode, TDataSourceLinkValueDO::getLinkValue));

        return tDataSourceTypeCodeDOS.stream().map(item -> {
            String linkValue = linkValueMap.get(item.getSourceTypeKeyCode());
            return new Link()
                    .setLinkValue(mybatisCryptoProperties.getEnable() ? sm4Encryptor.decrypt(linkValue) : linkValue)
                    .setSourceTypeKeyCode(item.getSourceTypeKeyCode())
                    .setSourceTypeKeyName(item.getSourceTypeKeyName())
                    .setSourceTypeKey(item.getSourceTypeKey())
                    .setDefaultValue(item.getDefaultValue())
                    .setKeyType(item.getKeyType())
                    .setRequiredFlag(item.getRequiredFlag())
                    .setCheckRegular(item.getCheckRegular())
                    .setOnlyReadFlag(item.getOnlyReadFlag());

        }).collect(Collectors.toList());

    }
}
