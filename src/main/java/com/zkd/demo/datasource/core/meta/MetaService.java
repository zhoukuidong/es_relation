package com.zkd.demo.datasource.core.meta;


import com.dtstack.dtcenter.loader.dto.SqlQueryDTO;
import com.zkd.demo.datasource.entity.meta.Table;

import java.util.Collections;
import java.util.List;
import java.util.Map;


public interface MetaService {

    List<String> getTables(String sourceCode);

    Map<String, String> getTablesMap(Map<String, String> dataMap, Integer sourceType);

    Table getMetaData(String sourceCode, String tableName);

    Table getMetaData(Map<String, String> dataMap, Integer sourceType, String tableName);

    /**
     * 获取预览数据
     */
    default List<List<Object>> getPreview(Map<String, String> dataMap, Integer sourceType, SqlQueryDTO sqlQueryDTO) {
        return Collections.emptyList();
    }

    /**
     * 获取建表语句
     */
    default String getCreateTableSql(Map<String, String> dataMap, Integer sourceType, SqlQueryDTO sqlQueryDTO) {
        return "";
    }


}
