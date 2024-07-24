package com.zkd.demo.datasource.dialect;


import com.zkd.demo.datasource.common.SqlConstant;

public abstract class BaseDialectGenerator {

    public String generatePageSql(String sql, Object start, Object pageSize) {
        if (checkLimitExist(sql)) {
            return sql;
        }
        return sql + SqlConstant.LIMIT + start + ',' + pageSize;
    }


    /**
     * 校验sql中是否存在limit
     */
    protected Boolean checkLimitExist(String sql) {
        return sql.contains(SqlConstant.LIMIT);
    }
}
