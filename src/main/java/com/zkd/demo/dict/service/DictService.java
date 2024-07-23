package com.zkd.demo.dict.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.StrUtil;
import com.zkd.demo.dict.util.EnumUtil;

import java.util.*;

public interface DictService {


    /**
     * @param basePackage 包扫描地址
     * @param filterDict  需要过滤不加载的枚举类simpleName
     * @return {@link Map < String, Map< String, Object>>}
     * @author: xiaosl
     * @date: 2021-08-27 20:18:02
     * @description: 基于包扫描枚举类加载数据字典
     */
    default Map<String, Map<String, Object>> getDictWithPackages(String basePackage, List<String> filterDict) {
        Map<String, Map<String, Object>> dictMap = new HashMap<>();
        if (StrUtil.isBlankIfStr(basePackage)) {
            return dictMap;
        }
        Set<Class<?>> classSet = ClassUtil.scanPackage(basePackage);
        if (CollUtil.isEmpty(classSet)) {
            return dictMap;
        }
        List<String> finalFilterDict = CollUtil.isEmpty(filterDict) ? new ArrayList<>() : filterDict;
        classSet.forEach(classObject -> {
            if (!classObject.isEnum()) {
                return;
            }
            if (finalFilterDict.contains(classObject.getSimpleName())) {
                return;
            }
            //TODO 过滤非implements BaseEnum
            try {
                dictMap.put(classObject.getSimpleName(), EnumUtil.getEnumMap((Class) classObject));
            } catch (Exception e) {
            }
        });
        return dictMap;
    }

    /**
     * @return {@link Map< String, Map< String, Object>>}
     * @author: xiaosl
     * @date: 2021-08-27 20:19:38
     * @description: 基于数据库加载数据字典
     */
    default Map<String, Map<String, Object>> getDictWithDb() {
        return new HashMap<>();
    }

    /**
     * @param userId 用户编号
     * @return {@link String}
     * @author: xiaosl
     * @date: 2021-06-30 23:01:33
     * @description: 根据userId获取用户名称
     */
    public String getUserNameByUserId(Object userId);


}
