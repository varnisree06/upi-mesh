package com.demo.upi_offline_mesh.service;

import com.demo.upi_offline_mesh.model.PaymentInstruction;
import com.demo.upi_offline_mesh.model.TransactionStatus;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class TransactionStateServiceTest {

    @Test
    void shouldMoveFromCreatedToForwarding() {

        PaymentInstruction payment =
                createPayment();

        TransactionStateService service =
                new TransactionStateService();

        service.moveTo(
                payment,
                TransactionStatus.FORWARDING
        );

        assertEquals(
                TransactionStatus.FORWARDING,
                payment.getStatus()
        );
    }

    @Test
    void shouldMoveFromForwardingToDelivered() {

        PaymentInstruction payment =
                createPayment();

        TransactionStateService service =
                new TransactionStateService();

        service.moveTo(
                payment,
                TransactionStatus.FORWARDING
        );

        service.moveTo(
                payment,
                TransactionStatus.DELIVERED
        );

        assertEquals(
                TransactionStatus.DELIVERED,
                payment.getStatus()
        );
    }

    @Test
    void shouldMoveFromDeliveredToSettled() {

        PaymentInstruction payment =
                createPayment();

        TransactionStateService service =
                new TransactionStateService();

        service.moveTo(
                payment,
                TransactionStatus.FORWARDING
        );

        service.moveTo(
                payment,
                TransactionStatus.DELIVERED
        );

        service.moveTo(
                payment,
                TransactionStatus.SETTLED
        );

        assertEquals(
                TransactionStatus.SETTLED,
                payment.getStatus()
        );
    }

    @Test
    void shouldMoveFromForwardingToFailed() {

        PaymentInstruction payment =
                createPayment();

        TransactionStateService service =
                new TransactionStateService();

        service.moveTo(
                payment,
                TransactionStatus.FORWARDING
        );

        service.moveTo(
                payment,
                TransactionStatus.FAILED
        );

        assertEquals(
                TransactionStatus.FAILED,
                payment.getStatus()
        );
    }

    @Test
    void shouldRejectInvalidTransition() {

        PaymentInstruction payment =
                createPayment();

        TransactionStateService service =
                new TransactionStateService();

        assertThrows(
                IllegalStateException.class,
                () -> service.moveTo(
                        payment,
                        TransactionStatus.SETTLED
                )
        );
    }

    @Test
    void shouldRejectTransitionAfterSettlement() {

        PaymentInstruction payment =
                createPayment();

        TransactionStateService service =
                new TransactionStateService();

        service.moveTo(
                payment,
                TransactionStatus.FORWARDING
        );

        service.moveTo(
                payment,
                TransactionStatus.DELIVERED
        );

        service.moveTo(
                payment,
                TransactionStatus.SETTLED
        );

        assertThrows(
                IllegalStateException.class,
                () -> service.moveTo(
                        payment,
                        TransactionStatus.FORWARDING
                )
        );
    }

    private PaymentInstruction createPayment() {

        return new PaymentInstruction(
                UUID.randomUUID(),
                1L,
                2L,
                500,
                "INR",
                Instant.now(),
                UUID.randomUUID().toString()
        );
    }
}