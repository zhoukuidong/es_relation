package com.zkd.demo.crypto2.type;

import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SmUtil;
import cn.hutool.crypto.symmetric.SM4;
import com.zkd.demo.crypto2.properties.CustomCryPtoProperties;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;

@Slf4j
public class Sm4Encryptor implements IEncryptor {

    private SM4 sm4;

    @Override
    public String encrypt(Object value) {
        return sm4.encryptHex(String.valueOf(value), StandardCharsets.UTF_8);
    }

    public String encryptBase64(Object value) {
        return sm4.encryptBase64(String.valueOf(value), StandardCharsets.UTF_8);
    }

    @Override
    public String decrypt(Object value) {
        return sm4.decryptStr(String.valueOf(value), CharsetUtil.CHARSET_UTF_8);
    }

    @Override
    public Sm4Encryptor init(CustomCryPtoProperties customCryPtoProperties) {
        if (StrUtil.isBlank(customCryPtoProperties.getKeyPair().getKey())) {
            this.sm4 = SmUtil.sm4();
            return this;
        }
        this.sm4 = SmUtil.sm4(customCryPtoProperties.getKeyPair().getKey().getBytes());
        return this;
    }
}