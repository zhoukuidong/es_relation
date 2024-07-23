package com.zkd.demo.properties;

import lombok.Data;

/**
 * @desc 动态数据源
 */
@Data
public class CusDsProperties {

    /**
     * 是否开启加解密，默认为false
     */
    private Boolean enableCrypto = false;
    private String dataSourceCode;
    private String url;
    private String username;
    private String password;
    private String secret;
    private Boolean ifMaster = false;
}
