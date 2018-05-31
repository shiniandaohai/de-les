package com.boer.delos.utils.sign;

import java.io.InputStream;
import java.security.PublicKey;

/**
 * Created by zhukang on 16/5/21.
 */
public class iHomeUtils {


    /**
     * RSA加密
     * @param bytes
     * @param keyStream
     * @return
     * @throws Exception
     */
    public final static String RSAEncryData(byte[] bytes, InputStream keyStream)throws  Exception{
        //RSA加密
        PublicKey publicKey = RSAUtils.loadPublicKey(keyStream);
        byte[] encrypt = RSAUtils.encryptData(bytes, publicKey);
        return Base64Utils.encode(encrypt);
    }

    /**
     * AES加密
     * @param plainText
     * @param keyBytes
     * @return
     * @throws Exception
     */
    public final static String AESEncryData(String plainText, byte[] keyBytes) throws  Exception{
        byte[] aesEncryptBytes = AESUtils.encryptData(plainText.getBytes(), keyBytes);
        return Base64Utils.encode(aesEncryptBytes);
    }

    /**
     * AES解密
     * @param encryText
     * @param keyBytes
     * @return
     * @throws Exception
     */
    public final static String AESDecryData(String encryText, byte[] keyBytes) throws  Exception{
        byte[] aesDecryptBytes = AESUtils.decryptData(Base64Utils.decode(encryText), keyBytes);
        return new String(aesDecryptBytes);
    }
}
