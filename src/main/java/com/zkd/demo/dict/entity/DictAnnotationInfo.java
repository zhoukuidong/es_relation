package com.zkd.demo.dict.entity;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class DictAnnotationInfo {

    /**
     * 目标属性名称
     */
    private String targetFiledName;

    /**
     * 数据字典名称
     */
    private String dictKey;

    /**
     * 字典默认值
     */
    private String defaultValue;

    /**
     * 是否是用户id
     */
    private boolean dictUserId;

    /**
     * 是否是多个字符串以逗号拼接场景；如：A,B,C,D
     */
    private boolean array;

    /**
     * array=true时的切割符号
     */
    private String arraySeparator = ",";

    /**
     * 属性值执行自定义方法进行获取；用户名翻译dictUserId优先级最高
     */
    private String invokeMethod;

    /**
     * 值添加前缀
     */
    private String appendPrefix;

    /**
     * 值添加后缀
     */
    private String appendSuffix;

}