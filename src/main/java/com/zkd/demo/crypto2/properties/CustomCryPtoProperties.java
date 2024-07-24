package com.zkd.demo.crypto2.properties;

import com.zkd.demo.crypto2.type.IEncryptor;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "custom.crypto")
@Data
public class CustomCryPtoProperties {


    /**
     * 默认加解密方式（非必填），优先级：注解属性指定>外部配置（mybaties组件内的配置）>此处配置；三者位置可配置其一即可
     */
    private Class<? extends IEncryptor> defaultEncryptor;

    /**
     * 秘钥相关配置，非必填
     */
    private CustomKeyPair KeyPair;

    @Data
    public static class CustomKeyPair {

        /**
         * 公钥，一般可公开，适用于非对称加密(只对于非对称加密时候必填，如：RSA、SM2)；
         */
        private String publicKey;

        /**
         * 公钥，一般不外部泄露，适用于非对称加密(只对于非对称加密时候必填，如：RSA、SM2)；
         */
        private String privateKey;

        /**
         * 秘钥，一般不外部泄露；适用于SM4
         */
        private String key;

        /**
         * 是否替换为渔翁加密机
         */
        private boolean jceEnable = false;

        /**
         * 渔翁jce SM4加解密key
         */
        private int keyid = 1;

        /**
         * 渔翁jce模式，CBC或ECB
         */
        private String mode = "CBC";

        /**
         * 渔翁jce补丁，true为内部补丁，可输入任意长度；false为上层打补丁，即输入数据必须为密钥模长的整数倍
         */
        private boolean ispad = true;

        /**
         * 外部配置文件路径
         */
        private String configPath;

    }
}