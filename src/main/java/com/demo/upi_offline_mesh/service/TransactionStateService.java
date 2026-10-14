package com.demo.upi_offline_mesh.service;

import com.demo.upi_offline_mesh.model.PaymentInstruction;
import com.demo.upi_offline_mesh.model.TransactionStatus;
import org.springframework.stereotype.Service;

@Service
public class TransactionStateService {

    public void moveTo(
            PaymentInstruction payment,
            TransactionStatus newStatus
    ) {

        TransactionStatus currentStatus =
                payment.getStatus();

        if (!isValidTransition(
                currentStatus,
                newStatus
        )) {
            throw new IllegalStateException(
                    "Invalid transaction state transition: "
                            + currentStatus
                            + " -> "
                            + newStatus
            );
        }

        payment.setStatus(newStatus);
    }

    private boolean isValidTransition(
            TransactionStatus currentStatus,
            TransactionStatus newStatus
    ) {

        return switch (currentStatus) {

            case CREATED ->
                    newStatus == TransactionStatus.FORWARDING;

            case FORWARDING ->
                    newStatus == TransactionStatus.DELIVERED
                            || newStatus == TransactionStatus.FAILED;

            case DELIVERED ->
                    newStatus == TransactionStatus.SETTLED;

            case SETTLED, FAILED ->
                    false;
        };
    }
}