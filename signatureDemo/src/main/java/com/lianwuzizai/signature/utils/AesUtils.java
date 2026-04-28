package com.lianwuzizai.signature.utils;


import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.Charset;

@SuppressWarnings("all")
public class AesUtils {

    private static final String PODDING = "AES/ECB/PKCS5Padding";
    private static final Charset CHARSET = Charset.forName("UTF-8");


    //encrypte
    public static byte[] encrypt(String data, String key) throws Exception {
        byte[] raw = key.getBytes(CHARSET);
        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
        Cipher cipher = Cipher.getInstance(PODDING);
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
        return cipher.doFinal(data.getBytes(CHARSET));
    }

    // decrypte
    public static String decrypt(byte[] data, String key) throws Exception {
        byte[] raw = key.getBytes(CHARSET);
        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
        Cipher cipher = Cipher.getInstance(PODDING);
        cipher.init(Cipher.DECRYPT_MODE, skeySpec);
        byte[] original = cipher.doFinal(data);
        String originalString = new String(original, CHARSET);
        return originalString;
    }

    /**
     * byte [] encrypt
     *
     * @param data
     * @param key
     * @return
     * @throws Exception
     */
    public static byte[] encrypt(String data, byte[] key) throws Exception {
        SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");
        Cipher cipher = Cipher.getInstance(PODDING);
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
        return cipher.doFinal(data.getBytes(CHARSET));
    }

    /**
     * byte[] decrypt
     *
     * @param data
     * @param key
     * @return
     * @throws Exception
     */
    public static String decrypt(byte[] data, byte[] key) throws Exception {
        SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");
        Cipher cipher = Cipher.getInstance(PODDING);
        cipher.init(Cipher.DECRYPT_MODE, skeySpec);
        byte[] original = cipher.doFinal(data);
        String originalString = new String(original, CHARSET);
        return originalString;
    }
}