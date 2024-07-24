package com.zkd.demo.mybatis.plugins;

import com.esotericsoftware.kryo.kryo5.Kryo;
import com.zkd.demo.crypto2.type.IEncryptor;
import com.zkd.demo.mybatis.annotation.EncryptedField;
import com.zkd.demo.mybatis.kry.KryoPool;
import com.zkd.demo.mybatis.properties.CustomMybatisCryptoProperties;
import com.zkd.demo.mybatis.provider.EncryptedFieldsProvider;
import com.zkd.demo.mybatis.provider.KeyFieldsProvider;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.binding.MapperMethod;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.*;

import java.lang.reflect.Field;
import java.util.*;

@Slf4j
@Intercepts({@Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class})})
public class MybatisEncryptionPlugin extends BaseMybatisEncryption implements Interceptor {
    private CustomMybatisCryptoProperties properties;
    private volatile Map<Class, IEncryptor> encryptorProvider;

    public MybatisEncryptionPlugin(CustomMybatisCryptoProperties properties, Map<Class, IEncryptor> encryptorProvider) {
        this.properties = properties;
        this.encryptorProvider = encryptorProvider;
    }

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        Object[] args = invocation.getArgs();
        MappedStatement ms = (MappedStatement) args[0];
        Object parameter = args[1];
        //判断是否需要执行加解密动作
        if (encryptionRequired(parameter, ms.getSqlCommandType())) {
            Kryo kryo = null;
            try {
                kryo = KryoPool.obtain();
                //复制参数
                Object copiedParameter = kryo.copy(parameter);
                boolean isParamMap = parameter instanceof MapperMethod.ParamMap;
                if (isParamMap) {
                    //noinspection unchecked
                    MapperMethod.ParamMap<Object> paramMap = (MapperMethod.ParamMap<Object>) copiedParameter;
                    encryptParamMap(paramMap);
                } else {
                    processFields(EncryptedFieldsProvider.get(copiedParameter.getClass()), copiedParameter);
                }
                args[1] = copiedParameter;
                Object result = invocation.proceed();
                if (!isParamMap) {
                    handleKeyProperties(ms, parameter, copiedParameter);
                }
                return result;
            } finally {
                if (kryo != null) {
                    KryoPool.free(kryo);
                }
            }
        } else {
            return invocation.proceed();
        }
    }

    /**
     * 参数集合处理
     *
     * @param paramMap
     */
    private void encryptParamMap(MapperMethod.ParamMap<Object> paramMap) {
        Set<Map.Entry<String, Object>> entrySet = paramMap.entrySet();
        // 暂存已加密的属性参数，防止重复加密
        Set<Object> paramCache = new HashSet<>();
        for (Map.Entry<String, Object> entry : entrySet) {
            String key = entry.getKey();
            Object value = entry.getValue();
            if (value == null || key == null || paramCache.contains(value)) {
                continue;
            }
            if (value instanceof ArrayList) {
                //noinspection rawtypes
                ArrayList list = (ArrayList) value;
                if (!list.isEmpty()) {
                    Object firstItem = list.get(0);
                    Class<?> itemClass = firstItem.getClass();
                    //获取参数类中所有存在注解的属性
                    Set<Field> encryptedFields = EncryptedFieldsProvider.get(itemClass);
                    for (Object item : list) {
                        processFields(encryptedFields, item);
                    }
                }
            } else {
                processFields(EncryptedFieldsProvider.get(value.getClass()), value);
            }
            paramCache.add(value);
        }
    }

    /**
     * 属性值加密
     *
     * @param encryptedFields 注解属性集合
     * @param entry 参数值对象
     */
    private void processFields(Set<Field> encryptedFields, Object entry) {
        if (encryptedFields == null || encryptedFields.isEmpty()) {
            return;
        }
        for (Field field : encryptedFields) {
            EncryptedField encryptedField = field.getAnnotation(EncryptedField.class);
            if (encryptedField == null) {
                continue;
            }
            try {
                IEncryptor iEncryptor = getEncryptor(encryptedField, encryptorProvider, properties, field.getName());
                if (null == iEncryptor) {
                    continue;
                }
                Object originalVal = field.get(entry);
                if (originalVal == null) {
                    continue;
                }
                String encryptedVal = iEncryptor.encrypt(originalVal);
                field.set(entry, encryptedVal);
            } catch (Exception e) {
                throw new RuntimeException("process encrypted filed error.", e);
            }
        }
    }

    /**
     * 属性值设置
     * @param ms sql执行的MappedStatement对象
     * @param parameter 原始参数值
     * @param copyOfParameter 复制后的参数值
     * @throws IllegalAccessException
     */
    private void handleKeyProperties(MappedStatement ms, Object parameter, Object copyOfParameter) throws IllegalAccessException {
        List<Field> keyFields = KeyFieldsProvider.get(ms, copyOfParameter);
        for (Field keyField : keyFields) {
            keyField.set(parameter, keyField.get(copyOfParameter));
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
