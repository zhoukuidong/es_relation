package com.zkd.demo.dict.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties(prefix = "spring.dict")
@Data
public class CustomDictProperties {

    /**
     * 数据字典扫描枚举包地址，所有枚举必须基于BaseEnum，否则不加载
     */
    private String dictPackage;

    /**
     * 扫描包地址需要过滤的枚举类
     */
    private List<String> filters;

    /**
     * 翻译后的属性名后缀
     */
    private String targetSuffix = "Description";

    /**
     * 开启集成枚举包模式
     */

    private Boolean enablePackage = true;

    /**
     * 开启集成数据库模式
     */
    private Boolean enableDb = false;
}