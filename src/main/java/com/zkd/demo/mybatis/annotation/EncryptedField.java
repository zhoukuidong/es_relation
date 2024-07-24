package com.zkd.demo.mybatis.annotation;

import com.zkd.demo.crypto2.type.IEncryptor;

import java.lang.annotation.*;

@Documented
@Inherited
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface EncryptedField {

    /**
     *  属性加密方式
     *
     * @return
     */
    Class<? extends IEncryptor> encryptor() default IEncryptor.class;

}