package com.zkd.demo.crypto2.config;

import com.zkd.demo.crypto2.properties.CustomCryPtoProperties;
import com.zkd.demo.crypto2.type.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;

import javax.annotation.Resource;

public class CustomCryptoConfig {

    private static final Logger logger = LoggerFactory.getLogger(CustomCryptoConfig.class);

    @Resource
    private CustomCryPtoProperties customCryPtoProperties;

    @Bean
    public Sm2Encryptor sm2Encryptor() {
        return new Sm2Encryptor().init(customCryPtoProperties);
    }

    @Bean
    public Sm3Encryptor sm3Encryptor() {
        return new Sm3Encryptor().init(customCryPtoProperties);
    }

    @Bean
    public Sm4Encryptor sm4Encryptor() {
        if (customCryPtoProperties.getKeyPair().isJceEnable()) {
            logger.info(" >>>>> 渔翁SM4服务 init");
            return new JceSm4Encryptor().init(customCryPtoProperties);
        }
        logger.info(" >>>>> 常规SM4 init");
        return new Sm4Encryptor().init(customCryPtoProperties);
    }

    @Bean
    public Base64Encryptor base64Encryptor() {
        return new Base64Encryptor().init(customCryPtoProperties);
    }

}
