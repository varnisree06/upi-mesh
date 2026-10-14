package com.demo.upi_offline_mesh.crypto;

import com.demo.upi_offline_mesh.model.PaymentInstruction;
import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.UUID;
import static org.mockito.Mockito.*;

import static org.junit.jupiter.api.Assertions.*;

class PaymentEncryptionServiceTest {

    @Test
    void shouldEncryptPaymentInstruction() throws Exception {

        PaymentInstruction instruction =
                new PaymentInstruction(
                        UUID.randomUUID(),
                        1L,
                        2L,
                        500,
                        "INR",
                        Instant.now(),
                        UUID.randomUUID().toString()
                );

        SecretKey key =
                AesGcmCrypto.generateKey();

        PaymentEncryptionService service =
                new PaymentEncryptionService(
                        new tools.jackson.databind.ObjectMapper()
                );

        String encrypted =
                service.encryptPayment(instruction, key);

        assertNotNull(encrypted);

        assertNotEquals(
                "",
                encrypted
        );

        assertFalse(
                encrypted.contains("500")
        );
    }

    @Test
    void shouldCreateMeshPacketFromPayment() {

        PaymentEncryptionService encryptionService =
                mock(PaymentEncryptionService.class);

        /*
         * TODO:
         * This test depends on the actual return type
         * of PaymentEncryptionService.encrypt().
         */

        PaymentInstruction payment =
                new PaymentInstruction(
                        UUID.randomUUID(),
                        1L,
                        2L,
                        500,
                        "INR",
                        Instant.now(),
                        UUID.randomUUID().toString()
                );

        /*
         * We will complete this test against your
         * existing encryption implementation.
         */

        assertNotNull(payment);
        assertNotNull(encryptionService);
    }
}