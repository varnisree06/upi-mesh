package com.demo.upi_offline_mesh.crypto;

import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AesGcmCryptoTest {

    @Test
    void shouldEncryptAndDecrypt() throws Exception {

        SecretKey key =
                AesGcmCrypto.generateKey();

        String original =
                "Hello Offline UPI";

        String encrypted =
                AesGcmCrypto.encrypt(original, key);

        String decrypted =
                AesGcmCrypto.decrypt(encrypted, key);

        assertEquals(original, decrypted);
    }
}