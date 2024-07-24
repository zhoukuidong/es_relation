package com.zkd.demo.crypto2.type;

import com.zkd.demo.crypto2.properties.CustomCryPtoProperties;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class Base64Encryptor implements IEncryptor {

    @Override
    public String encrypt(Object value) {
        return Base64.getEncoder().encodeToString(String.valueOf(value).getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public String decrypt(Object value) {
        byte[] bytes = Base64.getDecoder().decode(String.valueOf(value));
        return new String(bytes, StandardCharsets.UTF_8);
    }

    @Override
    public Base64Encryptor init(CustomCryPtoProperties customCryPtoProperties) {
        return this;
    }
}