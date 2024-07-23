    -- auto-generated definition
    create table dynamic_data_source_config
    (
        id              bigint auto_increment
            primary key comment '主键',
        datasource_code varchar(64)                          null comment '数据源名称编码',
        datasource_name varchar(64)                          null comment '数据源名称',
        url             varchar(512)                         not null comment 'jdbc连接url',
        username        varchar(64)                          null comment 'jdbc连接用户名',
        password        varchar(64)                          null comment 'jdbc连接密码',
        enable_crypto   tinyint(1) default 0                 null comment '是否开启加解密',
        secret          varchar(64)                          null comment '加解密密钥，如果配置开启加解密，则需要配置',
        if_delete       tinyint(1) default 0                 not null comment '是否删除',
        creator         varchar(32)                          null comment '创建人',
        create_at       timestamp  default CURRENT_TIMESTAMP not null comment '创建时间',
        updater         varchar(32)                          null comment '更新人',
        update_at       timestamp  default CURRENT_TIMESTAMP not null comment '更新时间'
    )
        comment '动态数据源配置表';

    create index idx_code on dynamic_data_source_config(datasource_code);
    create index idx_ds_name on dynamic_data_source_config(datasource_name);