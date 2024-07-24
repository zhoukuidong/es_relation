package com.zkd.demo.datasource.dialect;


import com.zkd.demo.datasource.common.SqlConstant;

public class PostgreSQLDialectGenerator extends BaseDialectGenerator {

    @Override
    public String generatePageSql(String sql, Object start, Object pageSize) {
        if (super.checkLimitExist(sql)) {
            return sql;
        }
        return sql + SqlConstant.LIMIT + pageSize + SqlConstant.OFFSET + start;
    }
}
