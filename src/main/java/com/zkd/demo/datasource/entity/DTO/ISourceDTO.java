package com.zkd.demo.datasource.entity.DTO;

import java.sql.Connection;


public interface ISourceDTO {

    /**
     * 获取用户名
     */
    String getUsername();

    /**
     * 获取密码
     */
    String getPassword();

    /**
     * 获取数据源类型
     */
    Integer getSourceType();

    /**
     * 获取连接信息
     */
    Connection getConnection();

    /**
     * 设置 Connection 信息
     */
    void setConnection(Connection connection);
}
