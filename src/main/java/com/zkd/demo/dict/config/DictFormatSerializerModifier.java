package com.zkd.demo.dict.config;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.text.StrSplitter;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.fasterxml.jackson.databind.ser.BeanSerializerModifier;
import com.zkd.demo.dict.annotation.DictFormat;
import com.zkd.demo.dict.entity.DictAnnotationInfo;
import com.zkd.demo.dict.properties.CustomDictProperties;
import com.zkd.demo.dict.service.DictService;
import org.springframework.beans.factory.ObjectProvider;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class DictFormatSerializerModifier extends BeanSerializerModifier {


    private final Map<String, Map<String, Object>> cacheMap = new ConcurrentHashMap<>();

    private List<DictService> dictCollectorList;

    private CustomDictProperties customDictProperties;

    public DictFormatSerializerModifier(ObjectProvider<List<DictService>> dicServiceProvider, CustomDictProperties customDictProperties) {
        this.customDictProperties = customDictProperties;
        if (dicServiceProvider.getIfAvailable() != null) {
            dictCollectorList = dicServiceProvider.getIfAvailable();
        }
    }


    @Override
    public List<BeanPropertyWriter> changeProperties(SerializationConfig config, BeanDescription beanDesc, List<BeanPropertyWriter> beanProperties) {
        for (BeanPropertyWriter writer : beanProperties) {
            DictFormat dictFormat = writer.getAnnotation(DictFormat.class);
            if (dictFormat != null) {
                String sourceFileName = writer.getFullName().getSimpleName();
                String targetFiledName = isEmpty(dictFormat.targetFiled()) ? sourceFileName + customDictProperties.getTargetSuffix() : dictFormat.targetFiled();
                writer.assignSerializer(new DictJsonSerializer(new DictAnnotationInfo()
                        .setTargetFiledName(targetFiledName)
                        .setDictKey(isEmpty(dictFormat.dictKey()) ? sourceFileName : dictFormat.dictKey())
                        .setDefaultValue(dictFormat.defaultValue())
                        .setDictUserId(dictFormat.dictUserId())
                        .setArray(dictFormat.isArray())
                        .setArraySeparator(isEmpty(dictFormat.arraySeparator()) ? StrUtil.COMMA : dictFormat.arraySeparator())
                        .setAppendPrefix(dictFormat.appendPrefix())
                        .setAppendSuffix(dictFormat.appendSuffix())
                        .setInvokeMethod(dictFormat.invokeMethod())));
            }
        }

        return beanProperties;
    }

    private static boolean isEmpty(Object str) {
        return str == null || "".equals(str);
    }

    protected class DictJsonSerializer extends JsonSerializer<Object> {
        private final DictAnnotationInfo dictAnnotationInfo;

        DictJsonSerializer(DictAnnotationInfo dictAnnotationInfo) {
            this.dictAnnotationInfo = dictAnnotationInfo;
        }

        @Override
        public void serialize(Object value, JsonGenerator gen, SerializerProvider serializers) throws IOException {

            //处理用户名
            if (null != value && dictAnnotationInfo.isDictUserId()) {
                gen.writeObject(value);
                gen.writeFieldName(dictAnnotationInfo.getTargetFiledName());
                Object targetName = null;
                if (!dictCollectorList.isEmpty()) {
                    targetName = dictCollectorList.get(0).getUserNameByUserId(String.valueOf(value));
                }
                gen.writeObject(targetName);
                return;
            }
            //自自定义方法函数调用
            if (null != value && StrUtil.isNotBlank(dictAnnotationInfo.getInvokeMethod()) && CollUtil.isNotEmpty(dictCollectorList)) {
                Object targetName = ReflectUtil.invoke(dictCollectorList.get(0), dictAnnotationInfo.getInvokeMethod(), value);
                gen.writeObject(targetName);
                return;
            }

            //字典key要转换成String，否则不好匹配
            String dictKeyStringValue = value == null ? null : String.valueOf(value);
            //判断是否需要刷新本地字典缓存
            refreshCacheMap(dictAnnotationInfo.getDictKey(), dictKeyStringValue);
            //写入原始值
            gen.writeObject(dictKeyStringValue);
            //获取翻译字典值
            String translatorsValue = getTranslatorsValue(gen, dictAnnotationInfo, dictKeyStringValue);
            //写入字典值
            setTranslators(gen, dictAnnotationInfo, dictKeyStringValue, translatorsValue);

        }
    }

    private String getTranslatorsValue(JsonGenerator gen, DictAnnotationInfo dictAnnotationInfo, String needTranslatorValue) throws IOException {
        String dictKey = dictAnnotationInfo.getDictKey();
        String afterTranslatorValue = "";
        if (StrUtil.isBlank(needTranslatorValue)) {
            return dictAnnotationInfo.getDefaultValue();
        }
        if (StrUtil.isNotBlank(needTranslatorValue) && StrUtil.isNotBlank(dictKey) && cacheMap.containsKey(dictKey)) {
            //翻译的字典是多个值的集合
            if (!dictAnnotationInfo.isArray()) {
                return (String) cacheMap.get(dictKey).get(needTranslatorValue);
            }
            List<String> needTranslatorList = StrSplitter.split(needTranslatorValue, dictAnnotationInfo.getArraySeparator(), 0, true, true);
            if (CollUtil.isEmpty(needTranslatorList)) {
                return needTranslatorValue;
            }
            //字典获取对应的值
            return needTranslatorList.stream().map(s -> {
                return (String) cacheMap.get(dictKey).get(s);
            }).filter(StrUtil::isNotBlank).collect(Collectors.joining(dictAnnotationInfo.getArraySeparator()));
        }
        return needTranslatorValue;
    }


    /**
     * 写入字典值
     *
     * @param gen                序列化对象
     * @param dictAnnotationInfo 注解对象
     * @param originValue        原始值
     * @param translatorValue    翻译后的值
     * @throws IOException
     */
    private void setTranslators(JsonGenerator gen, DictAnnotationInfo dictAnnotationInfo, String originValue, String translatorValue) throws IOException {
        //设置翻译后的前后缀
        if (StrUtil.isNotBlank(dictAnnotationInfo.getAppendPrefix()) || StrUtil.isNotBlank(dictAnnotationInfo.getAppendSuffix())) {
            //针对于非默认值的进行追加前后缀
            if (StrUtil.isNotBlank(originValue) && !StrUtil.equals(translatorValue, dictAnnotationInfo.getDefaultValue())) {
                translatorValue = dictAnnotationInfo.getAppendPrefix() + translatorValue + dictAnnotationInfo.getAppendSuffix();
            }
        }
        //写翻译后column、value
        gen.writeFieldName(dictAnnotationInfo.getTargetFiledName());
        gen.writeObject(translatorValue);
    }


    /**
     * 刷新本地缓存字典
     *
     * @param dictKey            当前字典key
     * @param dictKeyStringValue 字典对应的value值
     */
    private void refreshCacheMap(String dictKey, String dictKeyStringValue) {
        //如果不存在key,或不存在value，重新加载
        if (!cacheMap.containsKey(dictKey) || !cacheMap.get(dictKey).containsKey(dictKeyStringValue)) {
            // 这里不能进行clean  如果clean了话 当A线程执行到获取的值的时候，同时这个时候将map进行了clean 则会出现空指针的问题，这里是不进行clean 即重新加载字典的时候进行覆盖，或者可以进行刷新字典的时候进行锁处理也可。
            //cacheMap.clear();
            dictCollectorList.forEach(dictCollector -> {
                if (customDictProperties.getEnablePackage()) {
                    cacheMap.putAll(dictCollector.getDictWithPackages(customDictProperties.getDictPackage(), customDictProperties.getFilters()));
                }
                if (customDictProperties.getEnableDb()) {
                    cacheMap.putAll(dictCollector.getDictWithDb());
                }
            });
        }
    }
}