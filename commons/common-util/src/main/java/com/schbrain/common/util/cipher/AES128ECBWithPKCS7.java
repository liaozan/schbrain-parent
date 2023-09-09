package com.schbrain.common.util.cipher;

import cn.hutool.crypto.symmetric.AES;
import com.schbrain.common.exception.BaseException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.security.Security;
import java.util.Base64;

import static java.nio.charset.StandardCharsets.UTF_8;

@Slf4j
public class AES128ECBWithPKCS7 {

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    /**
     * encrypt input text
     */
    public static String encrypt(String input, String secretKey) {
        return encrypt(input, secretKey, true);
    }

    /**
     * encrypt input text
     */
    public static String encrypt(String input, String secretKey, boolean toBase64) {
        byte[] encrypted = encryptByte(input.getBytes(UTF_8), secretKey);
        return toBase64 ? Base64.getEncoder().encodeToString(encrypted) : new String(encrypted, UTF_8);
    }

    /**
     * decrypt input text
     */
    public static String decrypt(String input, String secretKey) {
        return decrypt(input, secretKey, true);
    }

    /**
     * decrypt input text
     */
    public static String decrypt(String input, String secretKey, boolean isBase64) {
        byte[] bytes = isBase64 ? Base64.getDecoder().decode(input) : input.getBytes(UTF_8);
        return new String(decryptByte(bytes, secretKey), UTF_8);
    }

    /**
     * encrypt input bytes
     */
    public static byte[] encryptByte(byte[] input, String secretKey) {
        return createAes(secretKey).encrypt(input);
    }

    /**
     * decrypt input bytes
     */
    public static byte[] decryptByte(byte[] input, String secretKey) {
        return createAes(secretKey).decrypt(input);
    }

    /**
     * create aes crypto
     */
    private static AES createAes(String secretKey) {
        if (StringUtils.isBlank(secretKey)) {
            throw new BaseException("secretKey is blank!");
        }
        return new AES("ECB", "PKCS7Padding", secretKey.getBytes(UTF_8));
    }

}
