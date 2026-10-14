package com.demo.upi_offline_mesh.service;

import com.demo.upi_offline_mesh.model.Account;
import com.demo.upi_offline_mesh.model.PaymentInstruction;
import com.demo.upi_offline_mesh.model.TransactionStatus;
import com.demo.upi_offline_mesh.repository.AccountRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SettlementService {

    private final AccountRepository accountRepository;

    public SettlementService(
            AccountRepository accountRepository
    ) {
        this.accountRepository = accountRepository;
    }

    @Transactional
    public void settle(
            PaymentInstruction payment
    ) {

        // Payment must reach the destination first
        if (payment.getStatus()
                != TransactionStatus.DELIVERED) {

            throw new IllegalStateException(
                    "Payment must be DELIVERED before settlement"
            );
        }

        Account sender =
                accountRepository.findById(
                        payment.getSenderId()
                ).orElseThrow(
                        () -> new IllegalStateException(
                                "Sender account not found"
                        )
                );

        Account receiver =
                accountRepository.findById(
                        payment.getReceiverId()
                ).orElseThrow(
                        () -> new IllegalStateException(
                                "Receiver account not found"
                        )
                );

        // Check sender has enough money
        if (sender.getBalance()
                < payment.getAmount()) {

            throw new IllegalStateException(
                    "Insufficient balance"
            );
        }

        // Debit sender
        sender.setBalance(
                sender.getBalance()
                        - payment.getAmount()
        );

        // Credit receiver
        receiver.setBalance(
                receiver.getBalance()
                        + payment.getAmount()
        );

        // Save both account changes
        accountRepository.save(sender);
        accountRepository.save(receiver);

        // Mark payment as settled
        payment.setStatus(
                TransactionStatus.SETTLED
        );
    }
}