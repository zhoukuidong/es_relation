package com.zkd.demo.datasource.core.meta;


import com.zkd.demo.datasource.core.cover.DataSourceCover;
import com.zkd.demo.datasource.entity.meta.Table;

import java.util.List;
import java.util.Map;

public class NoSqlMetaService implements MetaService, DataSourceCover {

    @Override
    public List<String> getTables(String sourceCode) {
        throw new RuntimeException("This method is not supported by this data source...");
    }

    @Override
    public Map<String, String> getTablesMap(Map<String, String> dataMap, Integer sourceType) {
        throw new RuntimeException("This method is not supported by this data source...");
    }

    @Override
    public Table getMetaData(String sourceCode, String tableName) {
        throw new RuntimeException("This method is not supported by this data source...");
    }

    @Override
    public Table getMetaData(Map<String, String> dataMap, Integer sourceType, String tableName) {
        throw new RuntimeException("This method is not supported by this data source...");
    }
}
