package com.demo.upi_offline_mesh.service;

import com.demo.upi_offline_mesh.model.PaymentInstruction;
import com.demo.upi_offline_mesh.model.TransactionStatus;
import com.demo.upi_offline_mesh.repository.AccountRepository;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PaymentOrchestrationServiceTest {

    @Test
    void shouldPrepareValidPaymentForForwarding() {

        AccountRepository accountRepository =
                mock(AccountRepository.class);

        Clock clock =
                Clock.fixed(
                        Instant.parse("2026-09-22T10:00:00Z"),
                        ZoneOffset.UTC
                );

        /*
         * Sender exists
         */
        when(
                accountRepository.existsById(1L)
        ).thenReturn(true);

        /*
         * Receiver exists
         */
        when(
                accountRepository.existsById(2L)
        ).thenReturn(true);

        PaymentValidationService validationService =
                new PaymentValidationService(
                        accountRepository,
                        clock,
                        5
                );

        TransactionStateService transactionStateService =
                new TransactionStateService();

        PaymentOrchestrationService orchestrationService =
                new PaymentOrchestrationService(
                        validationService,
                        transactionStateService
                );

        PaymentInstruction payment =
                new PaymentInstruction(
                        UUID.randomUUID(),
                        1L,
                        2L,
                        500,
                        "INR",
                        Instant.parse(
                                "2026-09-22T09:58:00Z"
                        ),
                        UUID.randomUUID().toString()
                );

        /*
         * Initial state
         */
        assertEquals(
                TransactionStatus.CREATED,
                payment.getStatus()
        );

        /*
         * Prepare payment
         */
        PaymentInstruction preparedPayment =
                orchestrationService.preparePayment(
                        payment
                );

        /*
         * Same payment object should be returned
         */
        assertSame(
                payment,
                preparedPayment
        );

        /*
         * CREATED → FORWARDING
         */
        assertEquals(
                TransactionStatus.FORWARDING,
                preparedPayment.getStatus()
        );

        /*
         * Verify account validation happened
         */
        verify(
                accountRepository
        ).existsById(1L);

        verify(
                accountRepository
        ).existsById(2L);
    }


    @Test
    void shouldRejectInvalidPayment() {

        AccountRepository accountRepository =
                mock(AccountRepository.class);

        Clock clock =
                Clock.fixed(
                        Instant.parse("2026-09-22T10:00:00Z"),
                        ZoneOffset.UTC
                );

        /*
         * This payment will fail before account
         * validation because amount = 0.
         */
        PaymentValidationService validationService =
                new PaymentValidationService(
                        accountRepository,
                        clock,
                        5
                );

        TransactionStateService transactionStateService =
                new TransactionStateService();

        PaymentOrchestrationService orchestrationService =
                new PaymentOrchestrationService(
                        validationService,
                        transactionStateService
                );

        PaymentInstruction payment =
                new PaymentInstruction(
                        UUID.randomUUID(),
                        1L,
                        2L,
                        0,
                        "INR",
                        Instant.parse(
                                "2026-09-22T09:58:00Z"
                        ),
                        UUID.randomUUID().toString()
                );

        /*
         * Validation should reject the payment.
         */
        assertThrows(
                IllegalArgumentException.class,
                () -> orchestrationService.preparePayment(
                        payment
                )
        );

        /*
         * State must remain CREATED.
         */
        assertEquals(
                TransactionStatus.CREATED,
                payment.getStatus()
        );

        /*
         * Because amount validation fails before
         * account validation, repository should
         * not have been queried.
         */
        verifyNoInteractions(
                accountRepository
        );
    }


    @Test
    void shouldRejectExpiredPayment() {

        AccountRepository accountRepository =
                mock(AccountRepository.class);

        Clock clock =
                Clock.fixed(
                        Instant.parse("2026-09-22T10:00:00Z"),
                        ZoneOffset.UTC
                );

        PaymentValidationService validationService =
                new PaymentValidationService(
                        accountRepository,
                        clock,
                        5
                );

        TransactionStateService transactionStateService =
                new TransactionStateService();

        PaymentOrchestrationService orchestrationService =
                new PaymentOrchestrationService(
                        validationService,
                        transactionStateService
                );

        /*
         * Created 10 minutes ago.
         * Validity = 5 minutes.
         */
        PaymentInstruction payment =
                new PaymentInstruction(
                        UUID.randomUUID(),
                        1L,
                        2L,
                        500,
                        "INR",
                        Instant.parse(
                                "2026-09-22T09:50:00Z"
                        ),
                        UUID.randomUUID().toString()
                );

        /*
         * Expired payments must be rejected.
         */
        assertThrows(
                IllegalStateException.class,
                () -> orchestrationService.preparePayment(
                        payment
                )
        );

        /*
         * Payment must never enter forwarding.
         */
        assertEquals(
                TransactionStatus.CREATED,
                payment.getStatus()
        );
    }
}