package com.demo.upi_offline_mesh.crypto;

import com.demo.upi_offline_mesh.model.PaymentInstruction;
import org.junit.jupiter.api.Test;

import java.security.KeyPair;
import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class HybridEncryptionServiceTest {

    @Test
    void shouldEncryptAndDecryptPayment() throws Exception {

        // Receiver creates RSA key pair
        KeyPair receiverKeys =
                RsaOaepCrypto.generateKeyPair();

        // Alice creates payment
        PaymentInstruction original =
                new PaymentInstruction(
                        UUID.randomUUID(),
                        1L,
                        2L,
                        500,
                        "INR",
                        Instant.now(),
                        UUID.randomUUID().toString()
                );

        HybridEncryptionService service =
                new HybridEncryptionService(
                        new tools.jackson.databind.ObjectMapper()
                );

        // Encrypt using receiver's public key
        HybridEncryptedPayload encrypted =
                service.encrypt(
                        original,
                        receiverKeys.getPublic()
                );

        // Make sure encryption produced data
        assertNotNull(
                encrypted.getEncryptedData()
        );

        assertNotNull(
                encrypted.getEncryptedKey()
        );

        // Decrypt using receiver's private key
        PaymentInstruction decrypted =
                service.decrypt(
                        encrypted,
                        receiverKeys.getPrivate()
                );

        // Verify the payment survived the round trip
        assertEquals(
                original.getPaymentId(),
                decrypted.getPaymentId()
        );

        assertEquals(
                original.getSenderId(),
                decrypted.getSenderId()
        );

        assertEquals(
                original.getReceiverId(),
                decrypted.getReceiverId()
        );

        assertEquals(
                original.getAmount(),
                decrypted.getAmount()
        );

        assertEquals(
                original.getCurrency(),
                decrypted.getCurrency()
        );
    }
}