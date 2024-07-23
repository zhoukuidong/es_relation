package com.zkd.demo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 动态数据源配置表
 *
 * @author zkd
 * @TableName dynamic_data_source_config
 * @date 2021/10/15 */
@TableName(value ="dynamic_data_source_config")
@Data
public class DynamicDataSourceConfig implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 数据源名称编码
     */
    private String datasourceCode;

    /**
     * 数据源名称
     */
    private String datasourceName;

    /**
     * jdbc连接url
     */
    private String url;

    /**
     * jdbc连接用户名
     */
    private String username;

    /**
     * jdbc连接密码
     */
    private String password;

    /**
     * 是否开启加解密
     */
    private Boolean enableCrypto;

    /**
     * 加解密密钥，如果配置开启加解密，则需要配置
     */
    private String secret;

    /**
     * 是否删除
     */
    private Boolean ifDelete;

    /**
     * 创建人
     */
    private String creator;

    /**
     * 创建时间
     */
    private LocalDateTime createAt;

    /**
     * 更新人
     */
    private String updater;

    /**
     * 更新时间
     */
    private LocalDateTime updateAt;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}