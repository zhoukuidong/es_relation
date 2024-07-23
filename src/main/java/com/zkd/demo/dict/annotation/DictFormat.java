package com.zkd.demo.dict.annotation;

import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DictFormat {
    /**
     * 字典名称编码, 默认为当前属性的名称
     */
    String dictKey() default "";

    /**
     * 字典的属性原始值是否转String输出
     */
    boolean dictKeyToString() default false;

    /**
     * 目标属性, 一个字典对应有个值，这个也要有个属性存在，默认为dicKey+"Name"
     */
    String targetFiled() default "";

    /**
     * 默认值，当字典里没有对应的值时，显示的字典值
     */
    String defaultValue() default "未知";

    /**
     * 是否是用户Id
     */
    boolean dictUserId() default false;

    /**
     * 是否是逗号分隔多个字段翻译；如：A,B,C,D；
     *
     * @return
     */
    boolean isArray() default false;

    /**
     * 属性值执行自定义方法进行获取；用户名翻译dictUserId优先级最高
     *
     * @return
     */
    String invokeMethod() default "";

    /**
     * 前缀
     *
     * @return
     */
    String appendPrefix() default "";

    /**
     * 后缀
     *
     * @return
     */
    String appendSuffix() default "";

    /**
     * 针对于 isArray=true才有效
     *
     * @return
     */
    String arraySeparator() default ",";


}
