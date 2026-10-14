package com.demo.upi_offline_mesh.service;

import com.demo.upi_offline_mesh.model.FailureReason;
import com.demo.upi_offline_mesh.model.ForwardingResult;
import com.demo.upi_offline_mesh.model.MeshPacket;
import com.demo.upi_offline_mesh.model.PaymentInstruction;
import com.demo.upi_offline_mesh.model.TransactionStatus;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class TransactionRecoveryServiceTest {

    @Test
    void shouldMovePaymentToDeliveredAfterSuccessfulRecovery() {

        RetryService retryService =
                mock(RetryService.class);

        TransactionStateService stateService =
                new TransactionStateService();

        TransactionRecoveryService recoveryService =
                new TransactionRecoveryService(
                        stateService,
                        retryService
                );

        PaymentInstruction payment =
                createPayment();

        /*
         * CREATED → FORWARDING
         */
        stateService.moveTo(
                payment,
                TransactionStatus.FORWARDING
        );

        MeshPacket packet =
                createPacket();

        /*
         * Retry eventually succeeds.
         */
        when(
                retryService.forwardWithRetry(
                        anyString(),
                        any(MeshPacket.class),
                        anyInt()
                )
        ).thenReturn(
                ForwardingResult.success()
        );

        boolean recovered =
                recoveryService.recover(
                        payment,
                        "A",
                        packet,
                        3
                );

        assertTrue(recovered);

        assertEquals(
                TransactionStatus.DELIVERED,
                payment.getStatus()
        );

        verify(
                retryService,
                times(1)
        ).forwardWithRetry(
                "A",
                packet,
                3
        );
    }


    @Test
    void shouldMovePaymentToFailedAfterRecoveryFails() {

        RetryService retryService =
                mock(RetryService.class);

        TransactionStateService stateService =
                new TransactionStateService();

        TransactionRecoveryService recoveryService =
                new TransactionRecoveryService(
                        stateService,
                        retryService
                );

        PaymentInstruction payment =
                createPayment();

        /*
         * CREATED → FORWARDING
         */
        stateService.moveTo(
                payment,
                TransactionStatus.FORWARDING
        );

        MeshPacket packet =
                createPacket();

        /*
         * Retry attempts are exhausted.
         */
        when(
                retryService.forwardWithRetry(
                        anyString(),
                        any(MeshPacket.class),
                        anyInt()
                )
        ).thenReturn(
                ForwardingResult.failure(
                        FailureReason.NO_ROUTE
                )
        );

        boolean recovered =
                recoveryService.recover(
                        payment,
                        "A",
                        packet,
                        3
                );

        assertFalse(recovered);

        assertEquals(
                TransactionStatus.FAILED,
                payment.getStatus()
        );
    }


    @Test
    void shouldRejectRecoveryWhenPaymentIsNotForwarding() {

        RetryService retryService =
                mock(RetryService.class);

        TransactionStateService stateService =
                new TransactionStateService();

        TransactionRecoveryService recoveryService =
                new TransactionRecoveryService(
                        stateService,
                        retryService
                );

        PaymentInstruction payment =
                createPayment();

        /*
         * Payment is still CREATED.
         *
         * Recovery must not start.
         */
        MeshPacket packet =
                createPacket();

        assertThrows(
                IllegalStateException.class,
                () -> recoveryService.recover(
                        payment,
                        "A",
                        packet,
                        3
                )
        );

        verifyNoInteractions(
                retryService
        );
    }


    @Test
    void shouldNotAllowRecoveryAfterSettlement() {

        RetryService retryService =
                mock(RetryService.class);

        TransactionStateService stateService =
                new TransactionStateService();

        TransactionRecoveryService recoveryService =
                new TransactionRecoveryService(
                        stateService,
                        retryService
                );

        PaymentInstruction payment =
                createPayment();

        /*
         * CREATED
         *    ↓
         * FORWARDING
         *    ↓
         * DELIVERED
         *    ↓
         * SETTLED
         */

        stateService.moveTo(
                payment,
                TransactionStatus.FORWARDING
        );

        stateService.moveTo(
                payment,
                TransactionStatus.DELIVERED
        );

        stateService.moveTo(
                payment,
                TransactionStatus.SETTLED
        );

        MeshPacket packet =
                createPacket();

        assertThrows(
                IllegalStateException.class,
                () -> recoveryService.recover(
                        payment,
                        "A",
                        packet,
                        3
                )
        );

        verifyNoInteractions(
                retryService
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


    private MeshPacket createPacket() {

        return new MeshPacket(
                UUID.randomUUID(),
                "A",
                "BRIDGE",
                5,
                Instant.now(),
                "encrypted-data",
                "encrypted-key",
                "signature"
        );
    }
}