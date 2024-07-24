package com.zkd.demo.crypto2.type;

import cn.hutool.core.util.HexUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONValidator;
import com.zkd.demo.crypto2.properties.CustomCryPtoProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class JceSm4Encryptor extends Sm4Encryptor {

    private static final Logger logger = LoggerFactory.getLogger(JceSm4Encryptor.class);

    private int keyid;
    private String mode;
    private boolean ispad;

    @Override
    public String encrypt(Object value) {
        byte[] iv = new byte[16];//通过生成随机数生成初始化向量iv
        String data = String.valueOf(value);
        //将字符串转成16进制
        String hexData = HexUtil.encodeHexStr(data);
        byte[] indata = string2Byte(hexData);

        byte[] cipherdata = InternalSM4Enc(keyid, mode, ispad, indata, iv);
        if (cipherdata == null) {
            logger.error("SM4 internal enc is error! error data [{}]", data);
            return data;
        } else {
            logger.info("SM4 internal enc is ok!");

        }
        return byte2String(cipherdata);
    }

    @Override
    public String decrypt(Object value) {
        byte[] iv = new byte[16];//通过生成随机数生成初始化向量iv
        String data = String.valueOf(value);
        byte[] bytes = string2Byte(data);
        byte[] tmpdata = InternalSM4Dec(keyid, mode, ispad, bytes, iv);
        if (null != tmpdata) {
            logger.info("SM4 internal Dec is ok, Dec data [{}]", data);
        } else {
            logger.error("SM4 internal Dec is error , error data [{}]", data);
            return data;
        }
        String s = byte2String(tmpdata);
        //将16进制字符串转回来
        return HexUtil.decodeHexStr(s);
    }

    @Override
    public Sm4Encryptor init(CustomCryPtoProperties customCryPtoProperties) {
        CustomCryPtoProperties.CustomKeyPair keyPair = customCryPtoProperties.getKeyPair();
        keyid = keyPair.getKeyid();
        mode = keyPair.getMode();
        ispad = keyPair.isIspad();
        return this;
    }


    SecretKey key = null;

    /**
     * 内部对称密钥加密运算
     *
     * @param keyid  密钥号
     * @param mode   "CBC"或者"ECB"
     * @param ispad  true为内部打补丁，即输入数据可为任意长度;false为上层打补丁，即输入数据必须为密钥模长的整数倍
     * @param indata 待加密数据
     * @param iv
     * @return 加密后的数据
     */
    public byte[] InternalSM4Enc(int keyid, String mode, boolean ispad, byte[] indata, byte[] iv) {
        String alg = "SM4" + "/" + mode + "/";
        byte[] cipherdata = null;
        byte[] tail = null;
        IvParameterSpec ivspe = null;
        if (ispad) {
            alg += "PKCS5PADDING";
        } else {
            alg += "NOPADDING";
        }
        String sysalg = "RandomSM4" + keyid;
        try {
            /*
             * alg:参数格式"算法名称/模式/打补丁方式"；
             * 如"SM1/ECB/NOPADDING"为SM1算法，ECB模式，不打补丁
             * "SM1/CBC/PKCS5PADDING"为SM1算法，CBC模式，打补丁
             */
            SecureRandom ran = SecureRandom.getInstance(sysalg, "FishermanJCE");
            Cipher cp = Cipher.getInstance(alg, "FishermanJCE");
            if (mode.equalsIgnoreCase("CBC")) {
                ivspe = new IvParameterSpec(iv, 0, 16);
                cp.init(Cipher.ENCRYPT_MODE, key, ivspe, ran);
            } else {
                cp.init(Cipher.ENCRYPT_MODE, key, ran);
            }
            cipherdata = cp.update(indata);
            tail = cp.doFinal();
        } catch (Exception e) {
            logger.error(alg + " internal enc error");
            e.printStackTrace();
            return null;
        }

        byte[] ret = null;
        if (tail != null) {
            if (cipherdata == null) {
                ret = new byte[tail.length];
                System.arraycopy(tail, 0, ret, 0, tail.length);
            } else {
                ret = new byte[cipherdata.length + tail.length];
                System.arraycopy(cipherdata, 0, ret, 0, cipherdata.length);
                System.arraycopy(tail, 0, ret, cipherdata.length, tail.length);
            }
        } else {
            ret = new byte[cipherdata.length];
            System.arraycopy(cipherdata, 0, ret, 0, cipherdata.length);
        }
        return ret;
    }

    /**
     * 内部对称密钥解密运算
     *
     * @param keyid  密钥号
     * @param mode   "CBC"或者"ECB"
     * @param ispad  true为内部打补丁，即输入数据可为任意长度;false为上层打补丁，即输入数据必须为密钥模长的整数倍
     * @param indata 待解密数据
     * @param iv
     * @return 解密后的数据
     */
    public byte[] InternalSM4Dec(int keyid, String mode, boolean ispad, byte[] indata, byte[] iv) {
        String alg = "SM4" + "/" + mode + "/";
        byte[] data = null;
        byte[] tail = null;
        IvParameterSpec ivspe = null;
        if (ispad) {
            alg += "PKCS5PADDING";
        } else {
            alg += "NOPADDING";
        }

        String sysran = "RandomSM4" + keyid;
        try {
            /*
             * alg:参数格式"算法名称/模式/打补丁方式"；
             * 如"SM1/ECB/NOPADDING"为SM1算法，ECB模式，不打补丁
             * "SM1/CBC/PKCS5PADDING"为SM1算法，CBC模式，打补丁
             */
            SecureRandom ran = SecureRandom.getInstance(sysran, "FishermanJCE");
            Cipher cp = Cipher.getInstance(alg, "FishermanJCE");
            if (mode.equalsIgnoreCase("CBC")) {
                ivspe = new IvParameterSpec(iv, 0, 16);
                cp.init(Cipher.DECRYPT_MODE, key, ivspe, ran);
            } else {
                cp.init(Cipher.DECRYPT_MODE, key, ran);
            }
            data = cp.update(indata);
            tail = cp.doFinal();
        } catch (Exception e) {
            logger.error(alg + " internal dec error");
            e.printStackTrace();
            return null;
        }
        byte[] ret = null;
        if (tail != null) {
            if (data != null) {
                ret = new byte[data.length + tail.length];
                System.arraycopy(data, 0, ret, 0, data.length);
                System.arraycopy(tail, 0, ret, data.length, tail.length);
            } else {
                ret = new byte[tail.length];
                System.arraycopy(tail, 0, ret, 0, tail.length);
            }
        } else {
            ret = new byte[data.length];
            System.arraycopy(data, 0, ret, 0, data.length);
        }
        return ret;
    }

    public byte[] string2Byte(String s) {
        s = s.replace(" ", "");
        s = s.replace("#", "");
        byte[] baKeyword = new byte[s.length() / 2];
        for (int i = 0; i < baKeyword.length; i++) {
            try {
                baKeyword[i] = (byte) (0xff & Integer.parseInt(s.substring(i * 2, i * 2 + 2), 16));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return baKeyword;
    }

    public String byte2String(byte[] data) {
        char[] hexArray = "0123456789ABCDEF".toCharArray();
        char[] hexChars = new char[data.length * 2];

        for (int j = 0; j < data.length; j++) {
            int v = data[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }

        String result = new String(hexChars);
        result = result.replace(" ", "");
        return result;
    }
}