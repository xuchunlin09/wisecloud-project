package com.lianwuzizai.signature.demo;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.*;

@SuppressWarnings("all")
public class NonEncryptedMessageSignature {

    private static Logger logger = LoggerFactory.getLogger(NonEncryptedMessageSignature.class);

    /**
     * Non-Encrypted Message Signature By MD5
     *
     * @param data parameter non-encrypted
     * @param sign entry signature
     * @param signKey accessKeySecret
     * @return
     * @throws UnsupportedEncodingException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     */
    public static Boolean parameterNonEncryptedSignByMd5Verify(Map<String, Object> data, String sign, String signKey) {
        String newSign = createNonEncryptedSignByMd5(data, signKey);
        return newSign.equals(sign);
    }

    public static String createNonEncryptedSignByMd5(Map<String, Object> requestBody, String signKey) {

        String data = convertMapToJson(requestBody);
        logger.info("Signature original string:{}", data + signKey);
        String signData = DigestUtils.md5Hex((getContentBytes(data + signKey, "UTF-8")));
        logger.info("Signed by MD5 result string:{}", signData);
        return signData;
    }

    /**
     * Non-Encrypted Message Signature By Hmac-SHA256
     *
     * @param data parameter non-encrypted by md5
     * @param sign entry signature
     * @param signKey accessKeySecret
     * @return
     * @throws UnsupportedEncodingException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     */
    public static Boolean parameterNonEncryptedSignByHmacSha256Verify(Map<String, Object> data, String sign, String signKey) throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException {
        String newSign = createNonEncryptedSignByHmacSha256(data, signKey);
        return newSign.equals(sign);
    }

    public static String createNonEncryptedSignByHmacSha256(Map<String, Object> requestBody, String signKey) throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException {
        String data = convertMapToJson(requestBody);
        logger.info("Signature original string:{}", data);
        String signData = hmacSha256Encipher(data, signKey);
        logger.info("Signed by Hmac-SHA256 result string:{}", signData);
        return signData;
    }


    /**
     * @param content
     * @param charset
     * @return
     * @throws SignatureException
     * @throws UnsupportedEncodingException
     */
    private static byte[] getContentBytes(String content, String charset) {
        if (charset == null || "".equals(charset)) {
            return content.getBytes();
        }
        try {
            return content.getBytes(charset);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("There was an error in the MD5 signature process, the specified encoding set is wrong, the encoding set you currently specified is:" + charset);
        }
    }

    public static String convertMapToJson(Map<String, Object> requestBody) {
        if (CollectionUtils.isEmpty(requestBody)) {
            return "";
        }
        requestBody.remove("signatureValue");
        //Sort the parameter names in ASCII code from small to large (in alphabetical order from a to z; if the same first letter is encountered, look at the second letter, and so on).
        TreeSet<String> sortedKey = new TreeSet<String>(requestBody.keySet());
        StringBuilder builer = new StringBuilder();
        for (String key : sortedKey) {
            //Use the "&" character to connect the sorted parameters.
            builer.append(key).append("=").append(convertObjectToJson(requestBody.get(key))).append("&");
        }
        String result = builer.toString();
        return result.substring(0, result.length() - 1);
    }

    public static String convertObjectToJson(Object obj) {
        //logger.info("obj："+obj);
        if (obj == null) {
            return "";
        }
        if (obj.getClass().isArray()) {
            return StringUtils.join((Object[]) obj, "&");
        } else if (obj instanceof Map) {
            return convertMapToJson((Map<String, Object>) obj);
        } else if (obj.getClass().isPrimitive() || obj.getClass() == String.class) {
            return String.valueOf(obj);
        } else if (obj instanceof Collection) {
            StringBuilder builder = new StringBuilder();
            for (Object _obj : (List<Object>) obj) {
                builder.append(convertObjectToJson(_obj)).append("&");
            }
            String result = builder.toString();
            if (result == null || result.equals("")) {
                return "";
            } else {
                return result.substring(0, result.length() - 1);
            }
        } else {
            return String.valueOf(obj);
        }
    }

    public static String hmacSha256Encipher(String data, String secret) throws NoSuchAlgorithmException, UnsupportedEncodingException, InvalidKeyException {
        Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKey = new SecretKeySpec(secret.getBytes("utf-8"), "HmacSHA256");
        sha256_HMAC.init(secretKey);
        byte[] hash = sha256_HMAC.doFinal(data.getBytes("utf-8"));
        //convert to hexadecimal
        String encodeStr16 = byte2Hex(hash);
        return encodeStr16;
    }

    private static String byte2Hex(byte[] bytes) {
        StringBuffer stringBuffer = new StringBuffer();
        String temp = null;
        for (int i = 0; i < bytes.length; i++) {
            temp = Integer.toHexString(bytes[i] & 0xFF);
            if (temp.length() == 1) {
                stringBuffer.append("0");
            }
            stringBuffer.append(temp);
        }
        return stringBuffer.toString();
    }
}
