package com.zkd.demo.mybatis.plugins;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.zkd.demo.crypto2.type.IEncryptor;
import com.zkd.demo.mybatis.annotation.EncryptedField;
import com.zkd.demo.mybatis.properties.CustomMybatisCryptoProperties;
import com.zkd.demo.mybatis.provider.EncryptedFieldsProvider;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.executor.resultset.ResultSetHandler;
import org.apache.ibatis.plugin.*;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

@Slf4j
@Intercepts({
        @Signature(type = ResultSetHandler.class, method = "handleResultSets", args = {Statement.class})
})
public class MybatisDecryptionPlugin extends BaseMybatisEncryption implements Interceptor {

    private CustomMybatisCryptoProperties properties;
    private volatile Map<Class, IEncryptor> encryptorProvider;

    public MybatisDecryptionPlugin(CustomMybatisCryptoProperties properties, Map<Class, IEncryptor> encryptorProvider) {
        this.properties = properties;
        this.encryptorProvider = encryptorProvider;
    }

    @Override
    public Object intercept(Invocation invocation) throws InvocationTargetException, IllegalAccessException {
        Object result = invocation.proceed();
        if (result == null) {
            return null;
        }
        if (result instanceof ArrayList) {
            //noinspection rawtypes
            ArrayList resultList = (ArrayList) result;
            if (resultList.isEmpty()) {
                return result;
            }
            Object firstItem = resultList.get(0);
            boolean needToDecrypt = decryptionRequired(firstItem);
            if (!needToDecrypt) {
                return result;
            }
            Set<Field> encryptedFields = EncryptedFieldsProvider.get(firstItem.getClass());
            if (encryptedFields == null || encryptedFields.isEmpty()) {
                return result;
            }
            for (Object item : resultList) {
                decryptEntity(encryptedFields, item);
            }
        }
        if (decryptionRequired(result)) {
            decryptEntity(EncryptedFieldsProvider.get(result.getClass()), result);
        }
        return result;
    }

    /**
     * 数据脱敏处理
     *
     * @param encryptedFields 属性注解集合
     * @param item 数据项
     */
    private void decryptEntity(Set<Field> encryptedFields, Object item) {
        if (CollUtil.isNotEmpty(encryptedFields)) {
            for (Field field : encryptedFields) {
                EncryptedField encryptedField = field.getAnnotation(EncryptedField.class);
                if (encryptedField != null) {
                    try {
                        IEncryptor iEncryptor = getEncryptor(encryptedField, encryptorProvider, properties, field.getName());
                        if (null == iEncryptor) {
                            continue;
                        }
                        field.setAccessible(true);
                        Object originalVal = field.get(item);
                        //对象null不解密
                        if (originalVal != null) {
                            //空串不解密
                            String originValStr = String.valueOf(originalVal);
                            if (StrUtil.isNotBlank(originValStr)) {
                                try {
                                    String decryptedVal = iEncryptor.decrypt(originValStr);
                                    field.set(item, decryptedVal);
                                } catch (Exception e) {
                                    log.error("数据解码器失败：[{}]属性数据解密失败，原始数据[{}]", field.getName(), originValStr, e);
                                }
                            }
                        }
                    } catch (Exception e) {
                        throw new RuntimeException("decrypt filed error.", e);
                    }
                }
            }
        }

    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {
        Interceptor.super.setProperties(properties);
    }
}