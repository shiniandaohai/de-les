package com.boer.delos.utils.sign;

import java.security.Security;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by zhukang on 16/5/21.
 */
public class AESUtils {

    /**
     * 密钥算法
     * java6支持56位密钥，bouncycastle支持64位
     */
    public static final String KEY_ALGORITHM = "AES";

    /**
     * 加密/解密算法/工作模式/填充方式
     */

    public static final String AES_ALGORITHM = "AES/ECB/PKCS5PADDING";

    /**
     * 生成加密密钥
     * @return 密钥
     */
    public final static byte[] generateKey() throws Exception{
        //实例化密钥生成器
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
        KeyGenerator kg = KeyGenerator.getInstance(KEY_ALGORITHM, "BC");
//        KeyGenerator kg = KeyGenerator.getInstance(KEY_ALGORITHM);
        //初始化密钥生成器，AES要求密钥长度为128位、192位、256位
        kg.init(256);
//        kg.init(128);
        //生成密钥
        SecretKey secretKey = kg.generateKey();
        //获取二进制密钥编码形式
        return secretKey.getEncoded();
    }


    /**
     * AES加密
     * @param plainBytes 需加密字节
     * @param keyBytes  加密密钥
     * @return
     * @throws Exception
     */
    public final static byte[] encryptData(byte[] plainBytes, byte[] keyBytes) throws Exception{
        Cipher cipher = Cipher.getInstance(AES_ALGORITHM);
        SecretKeySpec sks = new SecretKeySpec(keyBytes, "AES");
        cipher.init(Cipher.ENCRYPT_MODE, sks);
        return cipher.doFinal(plainBytes);
    }

    /**
     * AES解密
     * @param cryptBytes 需解密字节
     * @param keyBytes 加密密钥
     * @return
     * @throws Exception
     */
    public final static byte[] decryptData(byte[] cryptBytes, byte[] keyBytes) throws Exception{
        Cipher cipher = Cipher.getInstance(AES_ALGORITHM);
        SecretKeySpec sks = new SecretKeySpec(keyBytes, "AES");
        cipher.init(Cipher.DECRYPT_MODE, sks);
        return cipher.doFinal(cryptBytes);
    }

}
