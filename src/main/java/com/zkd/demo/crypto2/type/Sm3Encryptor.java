package com.zkd.demo.crypto2.type;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SmUtil;
import cn.hutool.crypto.digest.SM3;
import com.zkd.demo.crypto2.properties.CustomCryPtoProperties;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;

@Slf4j
public class Sm3Encryptor implements IEncryptor {

    private SM3 sm3;

    @Override
    public String encrypt(Object value) {
        return sm3.digestHex(String.valueOf(value), StandardCharsets.UTF_8);
    }

    @Override
    public String decrypt(Object value) {
        throw new RuntimeException("Sm3 not support decrypt");
    }

    /**
     * 加解密初始化接口
     *
     * @return 加解密工具对象
     */
    @Override
    public Sm3Encryptor init(CustomCryPtoProperties customCryPtoProperties) {
        if (StrUtil.isBlank(customCryPtoProperties.getKeyPair().getKey())) {
            this.sm3 = SmUtil.sm3();
            return this;
        }
        this.sm3 = SmUtil.sm3WithSalt(customCryPtoProperties.getKeyPair().getKey().getBytes());
        return this;
    }
}