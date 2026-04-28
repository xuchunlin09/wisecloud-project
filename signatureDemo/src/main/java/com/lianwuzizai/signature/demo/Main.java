package com.lianwuzizai.signature.demo;

import com.lianwuzizai.signature.utils.AesUtils;
import com.lianwuzizai.signature.utils.Base64Util;

import java.util.HashMap;
import java.util.Map;

public class Main {
    public static void main(String[] args) throws Exception {
        //accessKeySecret
        String key = "lMzFUXJgEuwNJdQVDgBbjRYJkADgYAek";
        //unencrypted parameter
        String parameter = "{\"amount\":\"30.8\",\"payType\":\"TNG\",\"mid\":\"123\",\"lang\":\"ZH\"}";
        System.out.println("unencrypted parameter:" + parameter);
        //encrypted parameter by AES
        byte[] enc = AesUtils.encrypt(parameter, key);
        String data = Base64Util.encodeMessage(enc);
        System.out.println("encrypted parameter:" + data);

        //encrypted parameter is signed with Hmac-SHA256
//        String encryptedSignByHmacSha256 = EncryptedMessageSignature.createEncryptedSignByHmacSha256(data, key);
//        System.out.println("Signed by HmacSHA256 result string:" + encryptedSignByHmacSha256);

        //encrypted parameter is signed with MD5
//        String encryptedSignByMd5 = EncryptedMessageSignature.createEncryptedSignByMd5(data, key);
//        System.out.println("Signed by MD5 result string:" + encryptedSignByMd5);

        Map<String, Object> map = new HashMap();
        map.put("amount", "33.12");
        map.put("payType", "TNG");
        map.put("mid", "123");
        System.out.println("unencrypted parameter:" + map.toString());

        //unencrypted parameter is signed with Hmac-SHA256
        String nonEncryptedSignByHmacSha256 = NonEncryptedMessageSignature.createNonEncryptedSignByHmacSha256(map, key);
        System.out.println("Signed by HmacSHA256 result string:" + nonEncryptedSignByHmacSha256);

        //unencrypted parameter is signed with MD5
        String nonEncryptedSignByMd5 = NonEncryptedMessageSignature.createNonEncryptedSignByMd5(map, key);
        System.out.println("Signed by HmacSHA256 result string:" + nonEncryptedSignByMd5);

    }
}