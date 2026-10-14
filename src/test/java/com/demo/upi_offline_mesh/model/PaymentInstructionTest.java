package com.demo.upi_offline_mesh.model;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class PaymentInstructionTest {

    @Test
    void newPaymentShouldStartAsCreated() {

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

        assertEquals(
                TransactionStatus.CREATED,
                payment.getStatus()
        );
    }

    @Test
    void shouldBeAbleToChangeTransactionStatus() {

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

        payment.setStatus(
                TransactionStatus.FORWARDING
        );

        assertEquals(
                TransactionStatus.FORWARDING,
                payment.getStatus()
        );
    }
}