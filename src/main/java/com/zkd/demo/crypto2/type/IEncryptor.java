package com.zkd.demo.crypto2.type;

import com.zkd.demo.crypto2.properties.CustomCryPtoProperties;

public interface IEncryptor {

    /**
     * 加密方法
     *
     * @param value 数据项
     * @return 加密后数据
     */
    String encrypt(Object value);

    /**
     * map加密
     */
    default String encryptMap(Object value) {
        return null;
    }

    /**
     * 解密方法
     *
     * @param value 数据项
     * @return 解密后原始数据
     */
    String decrypt(Object value);

    /**
     * map解密
     */
    default String decryptMap(Object value) {
        return null;
    }

    /**
     * 加解密初始化接口
     *
     * @return 加解密工具对象
     */
    IEncryptor init(CustomCryPtoProperties customCryPtoProperties);

    default IEncryptor init(String config) {
        return null;
    }
}
