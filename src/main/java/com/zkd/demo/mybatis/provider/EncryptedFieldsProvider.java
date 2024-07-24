package com.zkd.demo.mybatis.provider;

import cn.hutool.core.util.ReflectUtil;
import com.zkd.demo.mybatis.annotation.EncryptedField;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class EncryptedFieldsProvider {

    private static final Map<Class<?>, Set<Field>> encryptedFieldCache = new ConcurrentHashMap<>();

    public static Set<Field> get(Class<?> parameterClass) {
        return encryptedFieldCache.computeIfAbsent(parameterClass, aClass -> {
            //Field[] declaredFields = aClass.getDeclaredFields();
            Field[] declaredFields = ReflectUtil.getFields(aClass);
            Set<Field> fieldSet = Arrays.stream(declaredFields).filter(field -> field.isAnnotationPresent(EncryptedField.class) && field.getType() == String.class).collect(Collectors.toSet());
            for (Field field : fieldSet) {
                field.setAccessible(true);
            }
            return fieldSet;
        });
    }
}