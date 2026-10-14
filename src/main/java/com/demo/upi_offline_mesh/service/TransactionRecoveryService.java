package com.demo.upi_offline_mesh.service;

import com.demo.upi_offline_mesh.model.PaymentInstruction;
import com.demo.upi_offline_mesh.model.TransactionStatus;
import org.springframework.stereotype.Service;

@Service
public class TransactionRecoveryService {

    private final TransactionStateService transactionStateService;
    private final RetryService retryService;

    public TransactionRecoveryService(
            TransactionStateService transactionStateService,
            RetryService retryService
    ) {
        this.transactionStateService =
                transactionStateService;

        this.retryService =
                retryService;
    }

    public boolean recover(
            PaymentInstruction payment,
            String startingNode,
            com.demo.upi_offline_mesh.model.MeshPacket packet,
            int maxAttempts
    ) {

        /*
         * Payment must already be in FORWARDING state.
         */
        if (payment.getStatus()
                != TransactionStatus.FORWARDING) {

            throw new IllegalStateException(
                    "Payment must be in FORWARDING state before recovery"
            );
        }

        /*
         * Retry forwarding.
         */
        var result =
                retryService.forwardWithRetry(
                        startingNode,
                        packet,
                        maxAttempts
                );

        /*
         * Recovery succeeded.
         */
        if (result.isSuccess()) {

            transactionStateService.moveTo(
                    payment,
                    TransactionStatus.DELIVERED
            );

            return true;
        }

        /*
         * Recovery failed.
         *
         * At this point the retry mechanism has exhausted
         * its attempts or encountered a non-retryable failure.
         */
        transactionStateService.moveTo(
                payment,
                TransactionStatus.FAILED
        );

        return false;
    }
}