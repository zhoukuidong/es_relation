package com.zkd.demo.datasource.core.cover;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dtstack.dtcenter.loader.client.ClientCache;
import com.dtstack.dtcenter.loader.client.IClient;
import com.dtstack.dtcenter.loader.dto.ColumnMetaDTO;
import com.dtstack.dtcenter.loader.dto.source.ISourceDTO;
import com.google.common.collect.Maps;
import com.zkd.demo.datasource.core.common.IDataSourceEasyService;
import com.zkd.demo.datasource.entity.DO.TDataSourceDO;
import com.zkd.demo.datasource.entity.DO.TDataSourceTypeCodeDO;
import com.zkd.demo.datasource.entity.DO.TDataSourceTypeDO;
import com.zkd.demo.datasource.entity.DTO.ConnDTO;
import com.zkd.demo.datasource.entity.DTO.Link;
import com.zkd.demo.datasource.entity.VO.DataSourceDropVO;
import com.zkd.demo.datasource.entity.VO.DataSourceListVO;
import com.zkd.demo.datasource.entity.VO.DataSourceTypeVO;
import com.zkd.demo.datasource.entity.enums.DataSourceEnum;
import com.zkd.demo.datasource.entity.meta.Column;
import com.zkd.demo.datasource.mapper.TDataSourceMapper;
import com.zkd.demo.datasource.mapper.TDataSourceTypeMapper;
import com.zkd.demo.datasource.service.DataSourceLinkValueService;
import org.springframework.beans.BeanUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public interface DataSourceCover extends IDataSourceEasyService {


    /**
     * 数据源列表实体转换
     */
    default Page<DataSourceListVO> pageDOCoverToVO(TDataSourceTypeMapper dataSourceTypeMapper, Page<TDataSourceDO> page) {
        Page<DataSourceListVO> result = new Page<>();
        BeanUtils.copyProperties(page, result);
        List<TDataSourceDO> records = page.getRecords();
        if (CollectionUtils.isNotEmpty(records)) {
            List<String> sourceTypeCodeList = records.stream().map(TDataSourceDO::getSourceTypeCode).collect(Collectors.toList());
            Map<String, String> sourceTypeMap = queryDataSourceTypeListByTcs(dataSourceTypeMapper, sourceTypeCodeList)
                    .stream()
                    .collect(Collectors.toMap(TDataSourceTypeDO::getSourceTypeCode, TDataSourceTypeDO::getSourceTypeName, (k1, k2) -> k1));
            List<DataSourceListVO> voRecords = records.parallelStream().map(item -> item.changeToVO(sourceTypeMap.getOrDefault(item.getSourceTypeCode(), null))).collect(Collectors.toList());
            result.setRecords(voRecords);
        }
        return result;
    }

    /**
     * 数据源类型实体转换
     */
    default DataSourceTypeVO typeDoCoverToVO(TDataSourceTypeDO dataSourceTypeDO) {
        DataSourceTypeVO vo = new DataSourceTypeVO();
        BeanUtils.copyProperties(dataSourceTypeDO, vo);
        return vo;
    }

    /**
     * 数据源类型详情实体转换-不带值
     */
    default Link typeCodeCoverToLink(TDataSourceTypeCodeDO dataSourceTypeCodeDO) {
        Link link = new Link();
        BeanUtils.copyProperties(dataSourceTypeCodeDO, link);
        return link;
    }

    /**
     * link list转换成对应的Map
     */
    default Map<String, String> linkListCoverToMap(List<Link> list) {
        Map<String, String> map = Maps.newHashMap();
        list.forEach(item -> map.put(item.getSourceTypeKey(), item.getLinkValue()));
        return map;
    }

    /**
     * dataSourceDO转换成DataSourceDropVO
     */
    default DataSourceDropVO dataSourceCoverToDropVO(TDataSourceDO dataSourceDO) {
        DataSourceDropVO vo = new DataSourceDropVO();
        BeanUtils.copyProperties(dataSourceDO, vo);
        return vo;
    }

    /**
     * columnMetaDTO转换成Column
     */
    default Column columnMetaDTOCoverToColumn(ColumnMetaDTO columnMetaDTO, String tableName) {
        Column column = new Column();
        column.setName(columnMetaDTO.getKey());
        column.setComment(columnMetaDTO.getComment());
        column.setTypeName(columnMetaDTO.getType());
        column.setTableName(tableName);
        column.setNullable(columnMetaDTO.isNullable());
        column.setPart(columnMetaDTO.getPart());
        return column;
    }

    default ConnDTO getConnectionByDataResource(TDataSourceMapper dataSourceMapper, TDataSourceTypeMapper dataSourceTypeMapper,
                                                String sourceCode, List<Link> links) {
        //获取当前数据源
        TDataSourceDO dataSourceDO = queryDataSourceBySc(dataSourceMapper, sourceCode);
        //获取当前数据源类型
        TDataSourceTypeDO dataSourceTypeDO = queryDataSourceTypeByTc(dataSourceTypeMapper, dataSourceDO.getSourceTypeCode());

        //link list转map
        Map<String, String> map = linkListCoverToMap(links);
        //获取schema
        String schema = map.getOrDefault(SCHEMA_NAME, null);
        IClient client = ClientCache.getClient(Integer.parseInt(dataSourceTypeDO.getSourceType()));
        ISourceDTO sourceDTO = DataSourceEnum.getSourceDTO(map, Integer.valueOf(dataSourceTypeDO.getSourceType()), null, schema, null);
        return new ConnDTO().setClient(client).setSourceDTO(sourceDTO);
    }

    default ConnDTO getConnectionByDataResource(DataSourceLinkValueService dataSourceLinkValueService, TDataSourceMapper dataSourceMapper,
                                                TDataSourceTypeMapper dataSourceTypeMapper, String sourceCode) {
        //获取该数据源的连接信息
        List<Link> links = dataSourceLinkValueService.queryLinkListBySourceCode(sourceCode);
        return getConnectionByDataResource(dataSourceMapper, dataSourceTypeMapper, sourceCode, links);
    }

    default ConnDTO getConnectionByLink(String sourceType, List<Link> links) {
        //link list转map
        Map<String, String> map = linkListCoverToMap(links);
        IClient client = ClientCache.getClient(Integer.parseInt(sourceType));
        ISourceDTO sourceDTO = DataSourceEnum.getSourceDTO(map, Integer.valueOf(sourceType), null, null, null);
        return new ConnDTO().setClient(client).setSourceDTO(sourceDTO);
    }

}
