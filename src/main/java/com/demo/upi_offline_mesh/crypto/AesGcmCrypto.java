package com.demo.upi_offline_mesh.crypto;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

public class AesGcmCrypto {

    private static final String ALGORITHM = "AES/GCM/NoPadding";

    private static final int KEY_SIZE = 256;

    private static final int IV_SIZE = 12;

    private static final int TAG_SIZE = 128;

    public static SecretKey generateKey() throws Exception {

        KeyGenerator keyGenerator =
                KeyGenerator.getInstance("AES");

        keyGenerator.init(KEY_SIZE);

        return keyGenerator.generateKey();
    }

    public static String encrypt(
            String plaintext,
            SecretKey key
    ) throws Exception {

        byte[] iv = new byte[IV_SIZE];

        SecureRandom secureRandom = new SecureRandom();

        secureRandom.nextBytes(iv);

        GCMParameterSpec gcmParameterSpec =
                new GCMParameterSpec(TAG_SIZE, iv);

        Cipher cipher =
                Cipher.getInstance(ALGORITHM);

        cipher.init(
                Cipher.ENCRYPT_MODE,
                key,
                gcmParameterSpec
        );

        byte[] ciphertext =
                cipher.doFinal(
                        plaintext.getBytes(StandardCharsets.UTF_8)
                );

        byte[] combined =
                new byte[iv.length + ciphertext.length];

        System.arraycopy(
                iv,
                0,
                combined,
                0,
                iv.length
        );

        System.arraycopy(
                ciphertext,
                0,
                combined,
                iv.length,
                ciphertext.length
        );

        return Base64.getEncoder()
                .encodeToString(combined);
    }

    public static String decrypt(
            String encryptedData,
            SecretKey key
    ) throws Exception {

        byte[] combined =
                Base64.getDecoder()
                        .decode(encryptedData);

        byte[] iv =
                new byte[IV_SIZE];

        byte[] ciphertext =
                new byte[combined.length - IV_SIZE];

        System.arraycopy(
                combined,
                0,
                iv,
                0,
                IV_SIZE
        );

        System.arraycopy(
                combined,
                IV_SIZE,
                ciphertext,
                0,
                ciphertext.length
        );

        GCMParameterSpec gcmParameterSpec =
                new GCMParameterSpec(TAG_SIZE, iv);

        Cipher cipher =
                Cipher.getInstance(ALGORITHM);

        cipher.init(
                Cipher.DECRYPT_MODE,
                key,
                gcmParameterSpec
        );

        byte[] plaintext =
                cipher.doFinal(ciphertext);

        return new String(
                plaintext,
                StandardCharsets.UTF_8
        );
    }
}