package com.demo.upi_offline_mesh.crypto;

import javax.crypto.Cipher;
import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;

public class RsaOaepCrypto {

    private static final String ALGORITHM =
            "RSA/ECB/OAEPWithSHA-256AndMGF1Padding";

    private static final int KEY_SIZE = 2048;

    public static KeyPair generateKeyPair() throws Exception {

        KeyPairGenerator keyPairGenerator =
                KeyPairGenerator.getInstance("RSA");

        keyPairGenerator.initialize(KEY_SIZE);

        return keyPairGenerator.generateKeyPair();
    }

    public static String encrypt(
            String plaintext,
            PublicKey publicKey
    ) throws Exception {

        Cipher cipher =
                Cipher.getInstance(ALGORITHM);

        cipher.init(
                Cipher.ENCRYPT_MODE,
                publicKey
        );

        byte[] encrypted =
                cipher.doFinal(
                        plaintext.getBytes(StandardCharsets.UTF_8)
                );

        return Base64.getEncoder()
                .encodeToString(encrypted);
    }

    public static String decrypt(
            String encryptedData,
            PrivateKey privateKey
    ) throws Exception {

        Cipher cipher =
                Cipher.getInstance(ALGORITHM);

        cipher.init(
                Cipher.DECRYPT_MODE,
                privateKey
        );

        byte[] encrypted =
                Base64.getDecoder()
                        .decode(encryptedData);

        byte[] decrypted =
                cipher.doFinal(encrypted);

        return new String(
                decrypted,
                StandardCharsets.UTF_8
        );
    }
}