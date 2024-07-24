package com.zkd.demo.datasource.core.meta;

import com.alibaba.fastjson.JSON;

import com.dtstack.dtcenter.loader.client.ClientCache;
import com.dtstack.dtcenter.loader.client.IClient;
import com.dtstack.dtcenter.loader.dto.ColumnMetaDTO;
import com.dtstack.dtcenter.loader.dto.SqlQueryDTO;
import com.dtstack.dtcenter.loader.dto.source.ISourceDTO;
import com.zkd.demo.basic.core.exceptions.CommonException;
import com.zkd.demo.datasource.core.cover.DataSourceCover;
import com.zkd.demo.datasource.entity.DTO.ConnDTO;
import com.zkd.demo.datasource.entity.DTO.Link;
import com.zkd.demo.datasource.entity.enums.DataSourceEnum;
import com.zkd.demo.datasource.entity.meta.Column;
import com.zkd.demo.datasource.entity.meta.Table;
import com.zkd.demo.datasource.entity.meta.TableType;
import com.zkd.demo.datasource.mapper.TDataSourceMapper;
import com.zkd.demo.datasource.mapper.TDataSourceTypeMapper;
import com.zkd.demo.datasource.service.DataSourceLinkValueService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

import static com.zkd.demo.datasource.core.common.IDataSourceEasyService.SCHEMA_NAME;

public class RdsMetaService implements MetaService, DataSourceCover {

    private static final Logger logger = LoggerFactory.getLogger(RdsMetaService.class);

    private DataSourceLinkValueService dataSourceLinkValueService;
    private TDataSourceMapper dataSourceMapper;
    private TDataSourceTypeMapper dataSourceTypeMapper;


    public RdsMetaService(DataSourceLinkValueService dataSourceLinkValueService, TDataSourceMapper dataSourceMapper, TDataSourceTypeMapper dataSourceTypeMapper) {
        this.dataSourceLinkValueService = dataSourceLinkValueService;
        this.dataSourceMapper = dataSourceMapper;
        this.dataSourceTypeMapper = dataSourceTypeMapper;
    }

    @Override
    public List<String> getTables(String sourceCode) {
        final List<String> tables = new ArrayList<String>();
        List<Link> links = dataSourceLinkValueService.queryLinkListBySourceCode(sourceCode);
        ConnDTO connDTO = getConnectionByDataResource(dataSourceMapper, dataSourceTypeMapper, sourceCode, links);

        ResultSet rs = null;
        Map<String, String> map = linkListCoverToMap(links);
        String schema = map.getOrDefault(SCHEMA_NAME, null);
        try (Connection conn = connDTO.getConnection()) {
            final DatabaseMetaData metaData = conn.getMetaData();
            rs = metaData.getTables(conn.getCatalog(), conn.getSchema(), null, new String[]{TableType.TABLE.value()});
            if (rs == null) {
                return null;
            }
            String table;
            while (rs.next()) {
                table = rs.getString("TABLE_NAME");
                if (StringUtils.hasLength(table)) {
                    tables.add(table);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new CommonException(500, "conn get meta error");
        }
        logger.info(" >>>>> source:{} 探查到的tables:【{}】", sourceCode, JSON.toJSONString(tables));
        return tables;
    }

    @Override
    public Map<String, String> getTablesMap(Map<String, String> dataMap, Integer sourceType) {
        Map<String, String> tables = new HashMap<>();
        //获取schema
        String schema = dataMap.getOrDefault(SCHEMA_NAME, null);
        IClient client = ClientCache.getClient(sourceType);
        ISourceDTO sourceDTO = DataSourceEnum.getSourceDTO(dataMap, sourceType, null, schema, null);
        ConnDTO connDTO = new ConnDTO().setClient(client).setSourceDTO(sourceDTO);
        ResultSet rs = null;
        String schemaPattern = schema;
        try (Connection conn = connDTO.getConnection()) {
            final DatabaseMetaData metaData = conn.getMetaData();
            if (DataSourceEnum.GBase8s.getVal().equals(sourceType)) {
                schemaPattern = conn.getSchema();
            } else if (DataSourceEnum.OceanBase.getVal().equals(sourceType)) {
                schemaPattern = metaData.getUserName();
            }
            logger.info("sourceType is :{}, schemaPattern is :{} , catalog is :{}, dataMap is :{}", sourceType, schemaPattern, conn.getCatalog(),JSON.toJSONString(dataMap));
            if (null == metaData) {
                logger.error("metaData is null");
            }
            rs = metaData.getTables(conn.getCatalog(), schemaPattern, null, new String[]{TableType.TABLE.value()});

            if (rs == null) {
                return null;
            }
            String table;
            String table_schem;
            while (rs.next()) {
                table_schem = rs.getString("TABLE_SCHEM");
                table = rs.getString("TABLE_NAME");
                String tableMetaComment = connDTO.getTableMetaComment(SqlQueryDTO.builder().tableName(table).schema(table_schem).build());
                if (StringUtils.hasLength(table)) {
                    if (DataSourceEnum.SQLServer.getVal().compareTo(sourceType) == 0 || DataSourceEnum.Kingbase8.getVal().compareTo(sourceType) == 0
                            || DataSourceEnum.PostgreSQL.getVal().compareTo(sourceType) == 0 || DataSourceEnum.Gaussdb.getVal().compareTo(sourceType) == 0)  {
                        tables.putIfAbsent(table_schem + "." + table, tableMetaComment);
                    } else {
                        tables.putIfAbsent(table, tableMetaComment);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new CommonException(500, "conn get meta error");
        }
        logger.info(" >>>>> sourceType:{} 探查到的tables:【{}】", sourceType, JSON.toJSONString(tables));
        return tables;
    }

    @Override
    public Table getMetaData(String sourceCode, String tableName) {
        final Table table = Table.create(tableName);
        ConnDTO connDTO = getConnectionByDataResource(dataSourceLinkValueService, dataSourceMapper, dataSourceTypeMapper, sourceCode);
        ResultSet rs = null;

        try (Connection conn = connDTO.getConnection()) {
            // catalog和schema获取失败默认使用null代替
            String catalog = null;
            try {
                catalog = conn.getCatalog();
            } catch (SQLException e) {
                // ignore
            }
            String schema = null;
            try {
                schema = conn.getSchema();
            } catch (SQLException e) {
                // ignore
            }

            final DatabaseMetaData metaData = conn.getMetaData();

            // 获得表元数据（表注释）
            SqlQueryDTO sqlQueryDTO = SqlQueryDTO.builder()
                    .tableName(tableName)
                    .schema(schema)
                    .build();
            String tableMetaComment = connDTO.getTableMetaComment(sqlQueryDTO);
            table.setComment(tableMetaComment);

            // 获得主键
            rs = metaData.getPrimaryKeys(catalog, schema, tableName);
            while (rs.next()) {
                table.addPk(rs.getString("COLUMN_NAME"));
            }

            // 获得列
            rs = metaData.getColumns(catalog, schema, tableName, null);
            while (rs.next()) {
                table.setColumn(Column.create(tableName, rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new CommonException(500, "Get columns error!");
        }
        logger.info(" >>>>> source:{} tableName:{} 探查到的meta:【{}】", sourceCode, tableName, JSON.toJSONString(table));
        return table;
    }

    @Override
    public Table getMetaData(Map<String, String> dataMap, Integer sourceType, String tableName) {
        final Table table = Table.create(tableName);
        //获取schema
        String schema = dataMap.getOrDefault(SCHEMA_NAME, null);
        IClient client = ClientCache.getClient(sourceType);
        ISourceDTO sourceDTO = DataSourceEnum.getSourceDTO(dataMap, sourceType, null, schema, null);
        ConnDTO connDTO = new ConnDTO().setClient(client).setSourceDTO(sourceDTO);
        ResultSet rs = null;
        try (Connection conn = connDTO.getConnection()) {
            if (DataSourceEnum.GBase8s.getVal().compareTo(sourceType) == 0) {
                schema = conn.getSchema();
            }
            // catalog和schema获取失败默认使用null代替
            String catalog = null;
            try {
                catalog = conn.getCatalog();
            } catch (SQLException e) {
                // ignore
            }
            final DatabaseMetaData metaData = conn.getMetaData();

            // 获得表元数据（表注释）
            SqlQueryDTO sqlQueryDTO = SqlQueryDTO.builder()
                    .tableName(tableName)
                    .schema(schema)
                    .build();
            String tableMetaComment = connDTO.getTableMetaComment(sqlQueryDTO);
            table.setComment(tableMetaComment);

            // 获得主键
            rs = metaData.getPrimaryKeys(catalog, schema, tableName);
            while (rs.next()) {
                table.addPk(rs.getString("COLUMN_NAME"));
            }

            // 获得列
            try {
                rs = metaData.getColumns(catalog, schema, tableName, null);
            } catch (Exception e) {

            }
            while (rs.next()) {
                table.setColumn(Column.create(tableName, rs));
            }
            // 获得注释
            if (Objects.equals(DataSourceEnum.Oracle.getVal(), sourceType) || Objects.equals(DataSourceEnum.GBase8s.getVal(), sourceType) || Objects.equals(DataSourceEnum.OceanBase.getVal(), sourceType)) {
                SqlQueryDTO columnMetaDTO = SqlQueryDTO.builder()
                        .tableName(tableName)
                        .schema(schema)
                        .columns(table.getColumns().stream().map(Column::getName).collect(Collectors.toList()))
                        .build();
                List<ColumnMetaDTO> columnMetaData = connDTO.getColumnMetaData(columnMetaDTO);
                Map<String, String> map = columnMetaData.stream().filter(o -> Objects.nonNull(o.getComment())).collect(Collectors.toMap(ColumnMetaDTO::getKey, ColumnMetaDTO::getComment));
                table.getColumns().forEach(o -> {
                    o.setComment(map.get(o.getName()));
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new CommonException(500, "Get columns error!");
        }
        logger.info(" >>>>> sourceType:{} tableName:{} 探查到的meta:【{}】", sourceType, tableName, JSON.toJSONString(table));
        return table;
    }

    @Override
    public String getCreateTableSql(Map<String, String> dataMap, Integer sourceType, SqlQueryDTO sqlQueryDTO) {
        ISourceDTO sourceDTO = DataSourceEnum.getSourceDTO(dataMap, sourceType, null, sqlQueryDTO.getSchema(), null);
        IClient client = ClientCache.getClient(sourceType);
        String createTableSql = null;
        try {
            createTableSql = client.getCreateTableSql(sourceDTO, sqlQueryDTO);
        } catch (Exception e) {
            throw new CommonException(500, "获取表DDL 语句异常：" + e.getMessage());
        }
        return createTableSql;
    }

    @Override
    public List<List<Object>> getPreview(Map<String, String> dataMap, Integer sourceType, SqlQueryDTO sqlQueryDTO) {
        ISourceDTO sourceDTO = DataSourceEnum.getSourceDTO(dataMap, sourceType, null, sqlQueryDTO.getSchema(), null);
        IClient client = ClientCache.getClient(sourceType);
        List<List<Object>> result = null;
        try {
            result = client.getPreview(sourceDTO, sqlQueryDTO);
        } catch (Exception e) {
            throw new CommonException(500, "获取表预览数据异常：" + e.getMessage());
        }
        return result;
    }
}
