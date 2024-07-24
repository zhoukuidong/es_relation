package com.zkd.demo.mybatis.properties;

import com.zkd.demo.crypto2.properties.CustomCryPtoProperties;
import com.zkd.demo.crypto2.type.IEncryptor;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "custom.crypto.mybaties")
@Data
public class CustomMybatisCryptoProperties {

    /**
     * 开启Mybaties数据加解密功能
     */
    private Boolean enable;

    /**
     * 默认加解密方式（非必填），优先级：注解属性指定 > 此处配置 > KeyPair配置；三者位置可配置其一即可
     */
    private Class<? extends IEncryptor> defaultEncryptor;

    /**
     * 加解密相关参数，包含秘钥相关
     */
    @Autowired
    private CustomCryPtoProperties cryPto;

}