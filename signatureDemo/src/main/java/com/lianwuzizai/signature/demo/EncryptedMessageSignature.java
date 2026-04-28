package com.lianwuzizai.signature.demo;

import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;

@SuppressWarnings("all")
public class EncryptedMessageSignature {

    private static Logger logger = LoggerFactory.getLogger(EncryptedMessageSignature.class);

    /**
     * Encrypted Message Signature By Hmac-SHA256
     *
     * @param data parameter encrypted by md5
     * @param sign entry signature
     * @param signKey accessKeySecret
     * @return
     * @throws UnsupportedEncodingException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     */
    public static Boolean parameterEncryptedSignByHmacSha256Verify(String data, String sign, String signKey) throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException {
        String newSign = createEncryptedSignByHmacSha256(data, signKey);
        return newSign.equals(sign);
    }

    public static String createEncryptedSignByHmacSha256(String data, String signKey) throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException {
        logger.info("Signature original string:{}", data);
        String signData = hmacSha256Encipher(data, signKey);
        logger.info("Signed by Hmac-SHA256 result string:{}", signData);
        return signData;
    }

    /**
     * Encrypted Message Signature By MD5
     *
     * @param data parameter encrypted by md5
     * @param sign entry signature
     * @param signKey accessKeySecret
     * @return
     * @throws UnsupportedEncodingException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     */
    public static Boolean parameterEncryptedSignByMd5Verify(String data, String sign, String signKey) throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException {
        String newSign = createEncryptedSignByMd5(data, signKey);
        return newSign.equals(sign);
    }

    public static String createEncryptedSignByMd5(String data, String signKey) throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException {
        logger.info("Signature original string:{}", data + signKey);
        //encripted parameter and accessKeySecret get MD5
        String signData = DigestUtils.md5Hex((getContentBytes(data + signKey, "UTF-8")));
        logger.info("Signed by MD5 result string:{}", signData);
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
