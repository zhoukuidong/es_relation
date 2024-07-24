package com.zkd.demo.crypto2.type;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SmUtil;
import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.SM2;
import com.zkd.demo.crypto2.properties.CustomCryPtoProperties;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Sm2Encryptor implements IEncryptor {

    private SM2 sm2;


    @Override
    public String encrypt(Object value) {
        return sm2.encryptBcd(String.valueOf(value), KeyType.PublicKey);
    }

    @Override
    public String decrypt(Object value) {
        return StrUtil.utf8Str(sm2.decryptFromBcd(String.valueOf(value), KeyType.PrivateKey));
    }

    /**
     * 加解密初始化接口
     *
     * @return 加解密工具对象
     */
    @Override
    public Sm2Encryptor init(CustomCryPtoProperties customCryPtoProperties) {
        if (null != customCryPtoProperties.getKeyPair() && StrUtil.isAllNotBlank(customCryPtoProperties.getKeyPair().getPrivateKey(), customCryPtoProperties.getKeyPair().getPublicKey())) {
            this.sm2 = SmUtil.sm2(customCryPtoProperties.getKeyPair().getPrivateKey(), customCryPtoProperties.getKeyPair().getPublicKey());
            return this;
        }
        log.warn("国密SM2加密器加载未检测到密钥对，本次使用默认秘钥加载");
        return this;
    }
}