package com.demo.upi_offline_mesh.crypto;

import org.junit.jupiter.api.Test;

import java.security.KeyPair;

import static org.junit.jupiter.api.Assertions.*;

class RsaSignatureCryptoTest {

    @Test
    void shouldCreateAndVerifySignature() throws Exception {

        KeyPair keyPair =
                RsaOaepCrypto.generateKeyPair();

        String data =
                "Alice pays Bob 500 INR";

        String signature =
                RsaSignatureCrypto.sign(
                        data,
                        keyPair.getPrivate()
                );

        boolean valid =
                RsaSignatureCrypto.verify(
                        data,
                        signature,
                        keyPair.getPublic()
                );

        assertTrue(valid);
    }

    @Test
    void shouldRejectModifiedData() throws Exception {

        KeyPair keyPair =
                RsaOaepCrypto.generateKeyPair();

        String original =
                "Alice pays Bob 500 INR";

        String signature =
                RsaSignatureCrypto.sign(
                        original,
                        keyPair.getPrivate()
                );

        String modified =
                "Alice pays Bob 5000 INR";

        boolean valid =
                RsaSignatureCrypto.verify(
                        modified,
                        signature,
                        keyPair.getPublic()
                );

        assertFalse(valid);
    }
}