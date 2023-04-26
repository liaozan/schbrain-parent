package com.schbrain.common.util.cipher;

import cn.hutool.crypto.symmetric.AES;
import lombok.extern.slf4j.Slf4j;
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
    public static String encrypt(String input, String key) {
        byte[] encrypted = encryptByte(input, key);
        if (null == encrypted) {
            return "";
        }
        return Base64.getEncoder().encodeToString(encrypted);
    }

    public static byte[] encryptByte(String input, String key) {
        byte[] encrypted;
        try {
            encrypted = new AES("ECB", "PKCS7Padding", key.getBytes(UTF_8)).encrypt(input);
        } catch (Exception e) {
            log.error("AES128ECBWithPKCS7 encrypt ({}) error! ", input, e);
            return null;
        }
        return encrypted;
    }

    /**
     * decrypt input text
     */
    public static String decrypt(String input, String key) {
        return decryptByte(Base64.getDecoder().decode(input), key);
    }

    public static String decryptByte(byte[] input, String key) {
        try {
            byte[] output = new AES("ECB", "PKCS7Padding", key.getBytes(UTF_8)).decrypt(input);
            return new String(output, UTF_8);
        } catch (Exception e) {
            log.error("AES128ECBWithPKCS7 decryptByte ({}) error!", new String(input, UTF_8), e);
            return "";
        }
    }

}