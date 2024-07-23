package com.zkd.demo.invoke;

import com.zkd.demo.api.CusDsCryptoApi;
import com.zkd.demo.crypto.Sm4Factory;
import com.zkd.demo.properties.CusDsConfigProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author ZhangLi
 * @date 2023/8/17
 * @desc 加解密操作，使用配置的sm4密钥进行二次加解密操作
 */
@Component
@Slf4j
public class CusDsCryptoApiInvoke implements CusDsCryptoApi {

    @Autowired(required = false)
    private Sm4Factory sm4Factory;
    @Autowired
    private CusDsConfigProperties cusDsConfigProperties;


    @Override
    public String encrypt(String secret, String source) {
        String firstEncrypt = sm4Factory.getSm4(secret).encryptBase64(source.getBytes());
        String secondEncrypt = sm4Factory.getSm4(cusDsConfigProperties.getSecret()).encryptBase64(firstEncrypt.getBytes());
        return secondEncrypt;
    }

    @Override
    public String decrypt(String secret, String source) {
        String firstDecrypt = sm4Factory.getSm4(cusDsConfigProperties.getSecret()).decryptStr(source);
        String secondDecrypt = sm4Factory.getSm4(secret).decryptStr(firstDecrypt);
        return secondDecrypt;
    }
}
