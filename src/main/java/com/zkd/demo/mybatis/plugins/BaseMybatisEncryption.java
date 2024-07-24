package com.zkd.demo.mybatis.plugins;

import com.zkd.demo.crypto2.type.IEncryptor;
import com.zkd.demo.mybatis.annotation.EncryptedField;
import com.zkd.demo.mybatis.properties.CustomMybatisCryptoProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.mapping.SqlCommandType;

import java.util.Map;

@Slf4j
public class BaseMybatisEncryption {

    protected static boolean encryptionRequired(Object parameter, SqlCommandType sqlCommandType) {
        return (sqlCommandType == SqlCommandType.INSERT || sqlCommandType == SqlCommandType.UPDATE) && decryptionRequired(parameter);
    }

    protected static boolean decryptionRequired(Object parameter) {
        return !(parameter == null || parameter instanceof Double || parameter instanceof Integer
                || parameter instanceof Long || parameter instanceof Short || parameter instanceof Float
                || parameter instanceof Boolean || parameter instanceof Character
                || parameter instanceof Byte);
    }

    protected static IEncryptor getEncryptor(EncryptedField encryptedField, Map<Class, IEncryptor> encryptorProvider, CustomMybatisCryptoProperties properties, String fieldName) throws InstantiationException, IllegalAccessException {
        IEncryptor iEncryptor = encryptorProvider.get(encryptedField.encryptor());
        if (null == iEncryptor) {
            //获取默认加解密器
            Class<? extends IEncryptor> defaultEncryptor = null;
            if (null != properties && null != properties.getCryPto() && null != properties.getCryPto().getDefaultEncryptor()) {
                defaultEncryptor = properties.getCryPto().getDefaultEncryptor();
            }
            //优先以com.custom.crypto.mybaties.properties.CustomMybatisCryptoProperties.defaultEncryptor 属性配置为主
            if (null != properties && null != properties.getDefaultEncryptor()) {
                defaultEncryptor = properties.getDefaultEncryptor();
            }
            //赋值默认加解密器
            if (null != defaultEncryptor) {
                iEncryptor = encryptorProvider.get(defaultEncryptor);
            }
            if (null == iEncryptor) {
                log.warn("属性[{}]未检测到数据脱敏器", fieldName);
                return null;
            }
        }
        return iEncryptor;
    }


}
