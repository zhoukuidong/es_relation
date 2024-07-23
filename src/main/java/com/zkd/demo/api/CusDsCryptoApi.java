package com.zkd.demo.api;

/**
 * @author zkd
 * @date 2021/10/15
 * @desc 数据源信息加解密处理接口，如果开启加解密后，需要实现此接口
 */
public interface CusDsCryptoApi {

    /**
     * 加密字符串
     *
     * @param secret
     * @param source
     * @return
     */
    default String encrypt(String secret, String source) {
        return source;
    }

    /**
     * 解密字符串
     *
     * @param secret
     * @param source
     * @return
     */
    default String decrypt(String secret, String source) {
        return source;
    }
}
