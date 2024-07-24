package com.zkd.demo.basic.generator;

import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

@Slf4j
public class NumberGenerator {

    private static final int DEFAULT_LENGTH = 32;

    /**
     * 编码生成器
     * @param modulesEnum 编码模块枚举，非必填
     * @param length 编号长度，默认为32位 非必填
     * @return
     */
    public static String getCode(ModulesEnum modulesEnum, Integer length) {
        if (null == length || length < 13) {
            //不支持生成小于13位的编码
            length = DEFAULT_LENGTH;
        }
        String prefix = "";
        String code = "";
        if (null != modulesEnum) {
            if (null != modulesEnum.getCode()) {
                code = modulesEnum.getCode();
            }
            if (null != modulesEnum.getPrefix()) {
                prefix = modulesEnum.getPrefix();
            }
        }
        //获取当前时间戳并进行乱序
        String currentTimeMillisString = String.valueOf(System.currentTimeMillis());
        List<String> currentTimeMillisList = Arrays.asList(currentTimeMillisString.split(""));
        Collections.shuffle(currentTimeMillisList);
        String currentTimeMillis = String.join("", currentTimeMillisList);
        StringBuffer number = new StringBuffer(length)
                .append(prefix)
                .append(code)
                .append(currentTimeMillis);
        if (null != modulesEnum && null != modulesEnum.getPrefix() && null != modulesEnum.getCode()) {
            number.append(createRandomNumber(length - modulesEnum.getPrefix().length() - modulesEnum.getCode().length() - String.valueOf(currentTimeMillis).length()));
        } else {
            number.append(createRandomNumber(length - number.length()));
        }
        return number.toString();
    }


    public static String createRandomNumber(int length) {
        if (length <= 0) {
            return "";
        }
        StringBuilder strBuffer = new StringBuilder();
        Random rd = new Random();
        for (int i = 0; i < length; i++) {
            strBuffer.append(rd.nextInt(10));
        }
        return strBuffer.toString();
    }
}
