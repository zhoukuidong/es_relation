package com.zkd.demo.datasource.core.meta;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.dtstack.dtcenter.loader.client.ClientCache;
import com.dtstack.dtcenter.loader.client.IClient;
import com.dtstack.dtcenter.loader.dto.ColumnMetaDTO;
import com.dtstack.dtcenter.loader.dto.SqlQueryDTO;
import com.dtstack.dtcenter.loader.dto.source.ISourceDTO;
import com.zkd.demo.datasource.entity.DTO.ConnDTO;
import com.zkd.demo.datasource.entity.DTO.Link;
import com.zkd.demo.datasource.entity.enums.DataSourceEnum;
import com.zkd.demo.datasource.entity.meta.Table;
import com.zkd.demo.datasource.mapper.TDataSourceMapper;
import com.zkd.demo.datasource.mapper.TDataSourceTypeMapper;
import com.zkd.demo.datasource.service.DataSourceLinkValueService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class MaxcomputeMetaService extends NoSqlMetaService {

    private DataSourceLinkValueService dataSourceLinkValueService;
    private TDataSourceMapper dataSourceMapper;
    private TDataSourceTypeMapper dataSourceTypeMapper;

    public MaxcomputeMetaService(DataSourceLinkValueService dataSourceLinkValueService, TDataSourceMapper dataSourceMapper, TDataSourceTypeMapper dataSourceTypeMapper) {
        this.dataSourceLinkValueService = dataSourceLinkValueService;
        this.dataSourceMapper = dataSourceMapper;
        this.dataSourceTypeMapper = dataSourceTypeMapper;
    }

    @Override
    public List<String> getTables(String sourceCode) {
        final List<Link> links = dataSourceLinkValueService.queryLinkListBySourceCode(sourceCode);
        final ConnDTO connDTO = getConnectionByDataResource(dataSourceMapper, dataSourceTypeMapper, sourceCode, links);
        IClient client = connDTO.getClient();
        return (List<String>) client.getTableList(connDTO.getSourceDTO(), null);
    }

    @Override
    public Map<String, String> getTablesMap(Map<String, String> dataMap, Integer sourceType) {
        Map<String, String> res = new HashMap<>();
        ISourceDTO sourceDTO = DataSourceEnum.getSourceDTO(dataMap, sourceType, null, null, null);
        IClient client = ClientCache.getClient(sourceType);
        List list = client.getTableList(sourceDTO, null);
        Optional.ofNullable(list).ifPresent(o -> {
            o.forEach(v -> res.putIfAbsent(v.toString(), ""));
        });
        return res;
    }

    @Override
    public Table getMetaData(String sourceCode, String tableName) {
        final Table table = Table.create(tableName);
        ConnDTO connDTO = getConnectionByDataResource(dataSourceLinkValueService, dataSourceMapper, dataSourceTypeMapper, sourceCode);
        // 获得表元数据（表注释）
        SqlQueryDTO tableCommentQuery = SqlQueryDTO.builder()
                .tableName(tableName)
                .build();
        String tableMetaComment = connDTO.getTableMetaComment(tableCommentQuery);
        table.setComment(tableMetaComment);
        IClient client = connDTO.getClient();
        SqlQueryDTO columnQuery = SqlQueryDTO.builder()
                .tableName(tableName)
                .build();
        List<ColumnMetaDTO> columnMetaData = client.getColumnMetaData(connDTO.getSourceDTO(), columnQuery);
        if (CollectionUtils.isNotEmpty(columnMetaData)) {
            columnMetaData.stream().map(item -> columnMetaDTOCoverToColumn(item, tableName)).forEach(table::setColumn);
        }
        return table;
    }

    @Override
    public Table getMetaData(Map<String, String> dataMap, Integer sourceType, String tableName) {
        final Table table = Table.create(tableName);
        ISourceDTO sourceDTO = DataSourceEnum.getSourceDTO(dataMap, sourceType, null, null, null);
        IClient client = ClientCache.getClient(sourceType);
        ConnDTO connDTO = new ConnDTO().setClient(client).setSourceDTO(sourceDTO);
        // 获得表元数据（表注释）
        SqlQueryDTO tableCommentQuery = SqlQueryDTO.builder()
                .tableName(tableName)
                .build();
        String tableMetaComment = connDTO.getTableMetaComment(tableCommentQuery);
        table.setComment(tableMetaComment);
        SqlQueryDTO columnQuery = SqlQueryDTO.builder()
                .tableName(tableName)
                .build();
        List<ColumnMetaDTO> columnMetaData = client.getColumnMetaData(connDTO.getSourceDTO(), columnQuery);
        if (CollectionUtils.isNotEmpty(columnMetaData)) {
            columnMetaData.stream().map(item -> columnMetaDTOCoverToColumn(item, tableName)).forEach(table::setColumn);
        }
        return table;
    }
}
