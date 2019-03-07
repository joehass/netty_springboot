package com.weaver.authentication;

/**
 * @Author: 胡烨
 * @Date: 2019/2/15 13:52
 * @Version 1.0
 */

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.security.sasl.SaslException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * 一个实用程序类，它提供了对处理salted质询响应身份验证机制（scram）有用的方法。
 */
public class ScramUtils {

    public static byte[] computeHmac(final byte[] key,final String string) throws SaslException {
        Mac mac = createSha1Hmac(key);
        mac.update(string.getBytes(StandardCharsets.US_ASCII));
        return mac.doFinal();
    }

    public static Mac createSha1Hmac(final byte[] keyBytes) throws SaslException {
        try {
            SecretKeySpec key = new SecretKeySpec(keyBytes, "HmacSHA1");
            Mac mac = Mac.getInstance("HmacSHA1");
            mac.init(key);
            return mac;
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new SaslException(e.getMessage(), e);
        }
    }
}
