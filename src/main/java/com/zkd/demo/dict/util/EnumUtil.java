package com.zkd.demo.dict.util;

import cn.hutool.core.util.StrUtil;
import com.zkd.demo.dict.enums.BaseEnum;

import java.util.LinkedHashMap;
import java.util.Map;

public class EnumUtil {

    // 返回的对象实现CodeEnum接口
    public static <T extends BaseEnum> T getEnumByCode(Class<T> enumClass, Integer code) {
        for (T each : enumClass.getEnumConstants()) {
            if (StrUtil.equals(String.valueOf(each.getCode()), String.valueOf(code))) {
                return each;
            }
        }
        return null;
    }

    /**
     * 根据code返回对应的的desc
     * @param <T>
     * @param enumClass
     * @param code
     * @return
     */
    public static <T extends BaseEnum> String getDescByCode(Class<T> enumClass, Integer code) {
        for (T each : enumClass.getEnumConstants()) {
            if (null != code && each.getCode() == code) {
                return each.getDescription();
            }
        }
        return "";
    }

    /**
     * 根据description 获取Enum
     * @param enumClass
     * @param description
     * @param <T>
     * @return
     */
    public static <T extends BaseEnum> T getEnumByDesc(Class<T> enumClass, String description) {
        for (T each : enumClass.getEnumConstants()) {
            if (StrUtil.isNotBlank(description) && StrUtil.equalsIgnoreCase(description, each.getDescription())) {
                return each;
            }
        }
        return null;
    }

    /**
     * enum转换为map
     * @author: xiaosl
     * @date: 2020-03-11 16:51:39
     * @param enumClass
     * @return {@link Map < String, String>}
     */
    public static <T extends BaseEnum> Map<String, String> coverEnumToMap(Class<T> enumClass) {
        Map<String, String> map = new LinkedHashMap<>();
        for (T each : enumClass.getEnumConstants()) {
            map.put(String.valueOf(each.getCode()), each.getDescription());
        }
        return map;
    }


    // 返回的对象实现CodeEnum接口
    public static <T extends BaseEnum> Map<String, Object> getEnumMap(Class<T> enumClass) {
        Map<String, Object> map = new LinkedHashMap<>(enumClass.getEnumConstants().length);
        for (T each : enumClass.getEnumConstants()) {
            map.put(String.valueOf(each.getCode()), each.getDescription());
        }
        return map;
    }

}
