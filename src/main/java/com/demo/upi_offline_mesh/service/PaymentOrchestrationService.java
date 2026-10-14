package com.demo.upi_offline_mesh.service;

import com.demo.upi_offline_mesh.model.PaymentInstruction;
import org.springframework.stereotype.Service;

@Service
public class PaymentOrchestrationService {

    private final PaymentValidationService validationService;
    private final TransactionStateService transactionStateService;

    public PaymentOrchestrationService(
            PaymentValidationService validationService,
            TransactionStateService transactionStateService
    ) {
        this.validationService = validationService;
        this.transactionStateService = transactionStateService;
    }

    public PaymentInstruction preparePayment(
            PaymentInstruction payment
    ) {

        /*
         * Step 1:
         * Validate the payment before it enters
         * the mesh network.
         */
        validationService.validate(payment);

        /*
         * Step 2:
         * Move the transaction into FORWARDING state.
         */
        transactionStateService.moveTo(
                payment,
                com.demo.upi_offline_mesh.model.TransactionStatus.FORWARDING
        );

        return payment;
    }
}