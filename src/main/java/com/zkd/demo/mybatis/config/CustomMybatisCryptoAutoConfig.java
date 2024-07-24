package com.zkd.demo.mybatis.config;

import com.zkd.demo.crypto2.config.CustomCryptoConfig;
import com.zkd.demo.crypto2.properties.CustomCryPtoProperties;
import com.zkd.demo.crypto2.type.IEncryptor;
import com.zkd.demo.mybatis.plugins.MybatisDecryptionPlugin;
import com.zkd.demo.mybatis.properties.CustomMybatisCryptoProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.util.List;
import java.util.stream.Collectors;

@Import(CustomCryptoConfig.class)
@EnableConfigurationProperties({CustomMybatisCryptoProperties.class, CustomCryPtoProperties.class})
@ConditionalOnProperty(name = "custom.crypto.mybaties.enable", havingValue = "true", matchIfMissing = false)
@Configuration
public class CustomMybatisCryptoAutoConfig {

    @Autowired
    private List<IEncryptor> iEncryptors;

    // mybaties和mybaties-plus 不兼容，无法解析mybaties-plus
//    @Bean
//    @ConditionalOnBean(IEncryptor.class)
//    @ConditionalOnMissingBean(MybatisEncryptionPlugin.class)
//    public MybatisEncryptionPlugin encryptionInterceptor(CustomMybatisCryptoProperties properties) {
//        return new MybatisEncryptionPlugin(properties,
//                iEncryptors.stream().collect(Collectors.toMap(IEncryptor::getClass, encryptor -> encryptor, (key1, key2) -> key2))
//        );
//    }

    @Bean
    @ConditionalOnBean(IEncryptor.class)
    @ConditionalOnMissingBean(MybatisDecryptionPlugin.class)
    public MybatisDecryptionPlugin decryptionInterceptor(CustomMybatisCryptoProperties properties) {
        return new MybatisDecryptionPlugin(properties, iEncryptors.stream().collect(Collectors.toMap(IEncryptor::getClass, encryptor -> encryptor, (key1, key2) -> key2)));
    }
}