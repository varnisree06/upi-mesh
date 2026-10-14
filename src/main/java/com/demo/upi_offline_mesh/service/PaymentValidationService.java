package com.demo.upi_offline_mesh.service;

import com.demo.upi_offline_mesh.model.PaymentInstruction;
import com.demo.upi_offline_mesh.model.TransactionStatus;
import com.demo.upi_offline_mesh.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;

@Service
public class PaymentValidationService {

    private final AccountRepository accountRepository;
    private final Clock clock;
    private final Duration paymentValidity;

    public PaymentValidationService(
            AccountRepository accountRepository,
            Clock clock,
            @Value("${payment.validity-minutes:5}")
            long validityMinutes
    ) {
        this.accountRepository = accountRepository;
        this.clock = clock;
        this.paymentValidity =
                Duration.ofMinutes(validityMinutes);
    }

    public void validate(
            PaymentInstruction payment
    ) {

        // 1. Payment must exist
        if (payment == null) {

            throw new IllegalArgumentException(
                    "Payment cannot be null"
            );
        }

        // 2. Payment must not already be completed or failed
        TransactionStatus status =
                payment.getStatus();

        if (status == TransactionStatus.SETTLED) {

            throw new IllegalStateException(
                    "Payment has already been settled"
            );
        }

        if (status == TransactionStatus.FAILED) {

            throw new IllegalStateException(
                    "Payment has already failed"
            );
        }

        // 3. Amount must be positive
        if (payment.getAmount() <= 0) {

            throw new IllegalArgumentException(
                    "Payment amount must be greater than zero"
            );
        }

        // 4. Sender and receiver must be different
        if (payment.getSenderId()
                .equals(payment.getReceiverId())) {

            throw new IllegalArgumentException(
                    "Sender and receiver cannot be the same"
            );
        }

        // 5. Currency must be INR
        if (!"INR".equalsIgnoreCase(
                payment.getCurrency()
        )) {

            throw new IllegalArgumentException(
                    "Only INR payments are supported"
            );
        }

        // 6. Validate timestamp
        validateTimestamp(payment);

        // 7. Sender must exist
        if (!accountRepository.existsById(
                payment.getSenderId()
        )) {

            throw new IllegalArgumentException(
                    "Sender account does not exist"
            );
        }

        // 8. Receiver must exist
        if (!accountRepository.existsById(
                payment.getReceiverId()
        )) {

            throw new IllegalArgumentException(
                    "Receiver account does not exist"
            );
        }
    }

    private void validateTimestamp(
            PaymentInstruction payment
    ) {

        Instant createdAt =
                payment.getCreatedAt();

        if (createdAt == null) {

            throw new IllegalArgumentException(
                    "Payment creation time is required"
            );
        }

        Instant now =
                Instant.now(clock);

        // Payment cannot come from the future
        if (createdAt.isAfter(now)) {

            throw new IllegalArgumentException(
                    "Payment creation time cannot be in the future"
            );
        }

        Duration age =
                Duration.between(
                        createdAt,
                        now
                );

        // Payment has expired
        if (age.compareTo(paymentValidity) > 0) {

            throw new IllegalStateException(
                    "Payment has expired"
            );
        }
    }
}