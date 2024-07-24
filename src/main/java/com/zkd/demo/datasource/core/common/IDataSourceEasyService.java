package com.zkd.demo.datasource.core.common;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zkd.demo.basic.util.Assert;
import com.zkd.demo.datasource.entity.DO.TDataSourceDO;
import com.zkd.demo.datasource.entity.DO.TDataSourceLinkValueDO;
import com.zkd.demo.datasource.entity.DO.TDataSourceTypeCodeDO;
import com.zkd.demo.datasource.entity.DO.TDataSourceTypeDO;
import com.zkd.demo.datasource.entity.enums.DeleteEnum;
import com.zkd.demo.datasource.mapper.TDataSourceMapper;
import com.zkd.demo.datasource.mapper.TDataSourceTypeCodeMapper;
import com.zkd.demo.datasource.mapper.TDataSourceTypeMapper;
import com.zkd.demo.datasource.service.DataSourceLinkValueService;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

import static com.zkd.demo.datasource.core.constant.Constant.LIMIT_ONE;


public interface IDataSourceEasyService {

    String SCHEMA_NAME = "schema";

    /**
     * 根据sourceName+dataSourceList+page_param获取分页数据源数据
     */
    default Page<TDataSourceDO> queryDataSourcePageByNSl(TDataSourceMapper dataSourceMapper, String sourceName,
                                                         List<String> dataSourceList, Integer pageNum, Integer pageSize) {
        return dataSourceMapper.selectPage(
                new Page<TDataSourceDO>().setCurrent(pageNum).setSize(pageSize),
                new LambdaQueryWrapper<TDataSourceDO>()
                        .eq(TDataSourceDO::getDeleteFlag, DeleteEnum.N.getKey())
                        .like(StringUtils.hasLength(sourceName), TDataSourceDO::getSourceName, sourceName)
                        .in(CollectionUtils.isNotEmpty(dataSourceList), TDataSourceDO::getSourceCode, dataSourceList)
                        .orderByDesc(TDataSourceDO::getCreateAt));
    }

    /**
     * 根据dataSourceList获取数据源列表
     */
    default List<TDataSourceDO> queryDataSourceListBySl(TDataSourceMapper dataSourceMapper, List<String> dataSourceList) {
        return dataSourceMapper.selectList(new LambdaQueryWrapper<TDataSourceDO>()
                .eq(TDataSourceDO::getDeleteFlag, DeleteEnum.N.getKey())
                .in(CollectionUtils.isNotEmpty(dataSourceList), TDataSourceDO::getSourceCode, dataSourceList));
    }

    /**
     * 根据sourceCode获取数据源
     */
    default TDataSourceDO queryDataSourceBySc(TDataSourceMapper dataSourceMapper, String sourceCode) {
        return dataSourceMapper.selectOne(new LambdaQueryWrapper<TDataSourceDO>()
                .eq(TDataSourceDO::getDeleteFlag, DeleteEnum.N.getKey())
                .eq(TDataSourceDO::getSourceCode, sourceCode)
                .last(LIMIT_ONE));
    }

    /**
     * 获取数据源类型列表
     */
    default List<TDataSourceTypeDO> queryDataSourceTypeList(TDataSourceTypeMapper dataSourceTypeMapper) {
        return dataSourceTypeMapper.selectList(new LambdaQueryWrapper<TDataSourceTypeDO>()
                .eq(TDataSourceTypeDO::getDeleteFlag, DeleteEnum.N.getKey())
                .orderByAsc(TDataSourceTypeDO::getSourceTypeOrder));
    }

    /**
     * 根据sourceTypeCodeList获取数据源类型列表
     */
    default List<TDataSourceTypeDO> queryDataSourceTypeListByTcs(TDataSourceTypeMapper dataSourceTypeMapper, List<String> sourceTypeCodeList) {
        return dataSourceTypeMapper.selectList(new LambdaQueryWrapper<TDataSourceTypeDO>()
                .eq(TDataSourceTypeDO::getDeleteFlag, DeleteEnum.N.getKey())
                .in(CollectionUtils.isNotEmpty(sourceTypeCodeList), TDataSourceTypeDO::getSourceTypeCode, sourceTypeCodeList));
    }

    /**
     * 根据sourceTypeCode获取当前数据源类型
     */
    default TDataSourceTypeDO queryDataSourceTypeByTc(TDataSourceTypeMapper dataSourceTypeMapper, String sourceTypeCode) {
        return dataSourceTypeMapper.selectOne(new LambdaQueryWrapper<TDataSourceTypeDO>()
                .eq(TDataSourceTypeDO::getDeleteFlag, DeleteEnum.N.getKey())
                .eq(TDataSourceTypeDO::getSourceTypeCode, sourceTypeCode)
                .last(LIMIT_ONE));
    }

    /**
     * 根据sourceTypeCode获取数据源类型code列表
     */
    default List<TDataSourceTypeCodeDO> queryDataSourceTypeCodeListByTc(TDataSourceTypeCodeMapper dataSourceTypeCodeMapper, String sourceTypeCode) {
        return dataSourceTypeCodeMapper.selectList(new LambdaQueryWrapper<TDataSourceTypeCodeDO>()
                .eq(TDataSourceTypeCodeDO::getDeleteFlag, DeleteEnum.N.getKey())
                .eq(TDataSourceTypeCodeDO::getSourceTypeCode, sourceTypeCode));
    }

    /**
     * 数据源连接数据修改(sourceCode,sourceTypeKeyCode)
     */
    default boolean updateDataSourceLinkValueByScKc(DataSourceLinkValueService dataSourceLinkValueService, TDataSourceLinkValueDO entity,
                                                    String sourceCode, String sourceTypeKeyCode) {
        return dataSourceLinkValueService.update(entity.setUpdateAt(LocalDateTime.now()), new LambdaUpdateWrapper<TDataSourceLinkValueDO>()
                .set(null == entity.getLinkValue(), TDataSourceLinkValueDO::getLinkValue, entity.getLinkValue())
                .eq(TDataSourceLinkValueDO::getDeleteFlag, DeleteEnum.N.getKey())
                .eq(TDataSourceLinkValueDO::getSourceCode, sourceCode)
                .eq(TDataSourceLinkValueDO::getSourceTypeKeyCode, sourceTypeKeyCode));
    }

    /**
     * 数据源连接数据修改(sourceCode)
     */
    default boolean updateDataSourceLinkValueBySc(DataSourceLinkValueService dataSourceLinkValueService, TDataSourceLinkValueDO entity, String sourceCode) {
        return dataSourceLinkValueService.update(entity.setUpdateAt(LocalDateTime.now()),
                new LambdaQueryWrapper<TDataSourceLinkValueDO>()
                        .eq(TDataSourceLinkValueDO::getDeleteFlag, DeleteEnum.N.getKey())
                        .eq(TDataSourceLinkValueDO::getSourceCode, sourceCode));
    }

    /**
     * 根据sourceCode逻辑删除数据源连接数据
     */
    default boolean logicDeleteDataSourceLinkValueBySc(DataSourceLinkValueService dataSourceLinkValueService, String sourceCode, String currentUserCode) {
        return updateDataSourceLinkValueBySc(dataSourceLinkValueService,
                new TDataSourceLinkValueDO()
                        .setDeleteFlag(DeleteEnum.Y.getKey())
                        .setUpdator(currentUserCode),
                sourceCode);
    }


    /**
     * 数据源修改(sourceCode)
     */
    default int updateDataSourceBySc(TDataSourceMapper dataSourceMapper, TDataSourceDO entity, String sourceCode) {
        return dataSourceMapper.update(entity.setUpdateAt(LocalDateTime.now()),
                new LambdaQueryWrapper<TDataSourceDO>()
                        .eq(TDataSourceDO::getDeleteFlag, DeleteEnum.N.getKey())
                        .eq(TDataSourceDO::getSourceCode, sourceCode));
    }

    /**
     * 根据sourceCode逻辑删除数据源
     */
    default int logicDeleteDataSourceBySc(TDataSourceMapper dataSourceMapper, String sourceCode, String currentUserCode) {
        return updateDataSourceBySc(dataSourceMapper,
                new TDataSourceDO()
                        .setDeleteFlag(DeleteEnum.Y.getKey())
                        .setUpdator(currentUserCode),
                sourceCode);
    }

    /**
     * 根据sourceCode获取sourceType
     */
    default String querySourceTypeBySourceCode(TDataSourceMapper dataSourceMapper, TDataSourceTypeMapper dataSourceTypeMapper, String sourceCode) {
        TDataSourceDO dataSourceDO = queryDataSourceBySc(dataSourceMapper, sourceCode);
        Assert.isTrue(null != dataSourceDO, "数据源不存在，请核实");
        TDataSourceTypeDO dataSourceTypeDO = queryDataSourceTypeByTc(dataSourceTypeMapper, dataSourceDO.getSourceTypeCode());
        return dataSourceTypeDO.getSourceType();
    }

}
