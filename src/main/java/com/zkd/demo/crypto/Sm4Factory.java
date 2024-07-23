package com.zkd.demo.crypto;

import cn.hutool.core.lang.Singleton;
import cn.hutool.crypto.Mode;
import cn.hutool.crypto.Padding;
import cn.hutool.crypto.symmetric.SM4;
import org.springframework.stereotype.Component;

/**
 * 国密加密
 */
@Component
public class Sm4Factory {

    public SM4 getSm4(String secret) {
        return Singleton.get(secret, () -> new SM4(Mode.ECB, Padding.PKCS5Padding, secret.getBytes()));
    }
}
