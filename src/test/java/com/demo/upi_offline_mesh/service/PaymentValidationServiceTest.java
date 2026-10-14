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

class PaymentValidationServiceTest {

    private static final Instant FIXED_TIME =
            Instant.parse(
                    "2026-09-22T10:00:00Z"
            );

    private Clock testClock() {

        return Clock.fixed(
                FIXED_TIME,
                ZoneOffset.UTC
        );
    }

    private PaymentValidationService createService(
            AccountRepository repository
    ) {

        return new PaymentValidationService(
                repository,
                testClock(),
                5
        );
    }


    @Test
    void shouldAcceptValidPayment() {

        AccountRepository repository =
                mock(AccountRepository.class);

        when(repository.existsById(1L))
                .thenReturn(true);

        when(repository.existsById(2L))
                .thenReturn(true);

        PaymentValidationService service =
                createService(repository);

        PaymentInstruction payment =
                createPayment(
                        FIXED_TIME.minusSeconds(60)
                );

        assertDoesNotThrow(
                () -> service.validate(payment)
        );
    }


    @Test
    void shouldRejectNullPayment() {

        AccountRepository repository =
                mock(AccountRepository.class);

        PaymentValidationService service =
                createService(repository);

        assertThrows(
                IllegalArgumentException.class,
                () -> service.validate(null)
        );
    }


    @Test
    void shouldRejectZeroAmount() {

        AccountRepository repository =
                mock(AccountRepository.class);

        PaymentValidationService service =
                createService(repository);

        PaymentInstruction payment =
                createPayment(
                        FIXED_TIME.minusSeconds(60)
                );

        payment.setAmount(0);

        assertThrows(
                IllegalArgumentException.class,
                () -> service.validate(payment)
        );
    }


    @Test
    void shouldRejectNegativeAmount() {

        AccountRepository repository =
                mock(AccountRepository.class);

        PaymentValidationService service =
                createService(repository);

        PaymentInstruction payment =
                createPayment(
                        FIXED_TIME.minusSeconds(60)
                );

        payment.setAmount(-500);

        assertThrows(
                IllegalArgumentException.class,
                () -> service.validate(payment)
        );
    }


    @Test
    void shouldRejectSameSenderAndReceiver() {

        AccountRepository repository =
                mock(AccountRepository.class);

        PaymentValidationService service =
                createService(repository);

        PaymentInstruction payment =
                createPayment(
                        FIXED_TIME.minusSeconds(60)
                );

        payment.setReceiverId(
                payment.getSenderId()
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> service.validate(payment)
        );
    }


    @Test
    void shouldRejectUnsupportedCurrency() {

        AccountRepository repository =
                mock(AccountRepository.class);

        PaymentValidationService service =
                createService(repository);

        PaymentInstruction payment =
                createPayment(
                        FIXED_TIME.minusSeconds(60)
                );

        payment.setCurrency("USD");

        assertThrows(
                IllegalArgumentException.class,
                () -> service.validate(payment)
        );
    }


    @Test
    void shouldRejectUnknownSender() {

        AccountRepository repository =
                mock(AccountRepository.class);

        when(repository.existsById(1L))
                .thenReturn(false);

        PaymentValidationService service =
                createService(repository);

        PaymentInstruction payment =
                createPayment(
                        FIXED_TIME.minusSeconds(60)
                );

        assertThrows(
                IllegalArgumentException.class,
                () -> service.validate(payment)
        );
    }


    @Test
    void shouldRejectUnknownReceiver() {

        AccountRepository repository =
                mock(AccountRepository.class);

        when(repository.existsById(1L))
                .thenReturn(true);

        when(repository.existsById(2L))
                .thenReturn(false);

        PaymentValidationService service =
                createService(repository);

        PaymentInstruction payment =
                createPayment(
                        FIXED_TIME.minusSeconds(60)
                );

        assertThrows(
                IllegalArgumentException.class,
                () -> service.validate(payment)
        );
    }


    @Test
    void shouldRejectAlreadySettledPayment() {

        AccountRepository repository =
                mock(AccountRepository.class);

        PaymentValidationService service =
                createService(repository);

        PaymentInstruction payment =
                createPayment(
                        FIXED_TIME.minusSeconds(60)
                );

        payment.setStatus(
                TransactionStatus.SETTLED
        );

        assertThrows(
                IllegalStateException.class,
                () -> service.validate(payment)
        );
    }


    @Test
    void shouldRejectFailedPayment() {

        AccountRepository repository =
                mock(AccountRepository.class);

        PaymentValidationService service =
                createService(repository);

        PaymentInstruction payment =
                createPayment(
                        FIXED_TIME.minusSeconds(60)
                );

        payment.setStatus(
                TransactionStatus.FAILED
        );

        assertThrows(
                IllegalStateException.class,
                () -> service.validate(payment)
        );
    }


    @Test
    void shouldRejectPaymentWithNoCreationTime() {

        AccountRepository repository =
                mock(AccountRepository.class);

        PaymentValidationService service =
                createService(repository);

        PaymentInstruction payment =
                createPayment(null);

        assertThrows(
                IllegalArgumentException.class,
                () -> service.validate(payment)
        );
    }


    @Test
    void shouldRejectFuturePayment() {

        AccountRepository repository =
                mock(AccountRepository.class);

        PaymentValidationService service =
                createService(repository);

        PaymentInstruction payment =
                createPayment(
                        FIXED_TIME.plusSeconds(60)
                );

        assertThrows(
                IllegalArgumentException.class,
                () -> service.validate(payment)
        );
    }


    @Test
    void shouldRejectExpiredPayment() {

        AccountRepository repository =
                mock(AccountRepository.class);

        PaymentValidationService service =
                createService(repository);

        PaymentInstruction payment =
                createPayment(
                        FIXED_TIME.minusSeconds(6 * 60)
                );

        assertThrows(
                IllegalStateException.class,
                () -> service.validate(payment)
        );
    }


    @Test
    void shouldAcceptPaymentExactlyAtExpiryLimit() {

        AccountRepository repository =
                mock(AccountRepository.class);

        when(repository.existsById(1L))
                .thenReturn(true);

        when(repository.existsById(2L))
                .thenReturn(true);

        PaymentValidationService service =
                createService(repository);

        PaymentInstruction payment =
                createPayment(
                        FIXED_TIME.minusSeconds(5 * 60)
                );

        assertDoesNotThrow(
                () -> service.validate(payment)
        );
    }


    private PaymentInstruction createPayment(
            Instant createdAt
    ) {

        return new PaymentInstruction(
                UUID.randomUUID(),
                1L,
                2L,
                500,
                "INR",
                createdAt,
                UUID.randomUUID().toString()
        );
    }
}