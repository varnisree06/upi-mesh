package com.demo.upi_offline_mesh.crypto;

import org.junit.jupiter.api.Test;

import java.security.KeyPair;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RsaOaepCryptoTest {

    @Test
    void shouldEncryptAndDecrypt() throws Exception {

        KeyPair keyPair =
                RsaOaepCrypto.generateKeyPair();

        String original =
                "my-secret-aes-key";

        String encrypted =
                RsaOaepCrypto.encrypt(
                        original,
                        keyPair.getPublic()
                );

        String decrypted =
                RsaOaepCrypto.decrypt(
                        encrypted,
                        keyPair.getPrivate()
                );

        assertEquals(
                original,
                decrypted
        );
    }
}