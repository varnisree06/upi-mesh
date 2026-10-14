package com.demo.upi_offline_mesh.service;

import com.demo.upi_offline_mesh.model.Account;
import com.demo.upi_offline_mesh.model.PaymentRequest;
import com.demo.upi_offline_mesh.repository.AccountRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PaymentService {

    private final AccountRepository accountRepository;

    public PaymentService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Transactional
    public void transfer(PaymentRequest request) {

        Account sender = accountRepository
                .findById(request.getSenderId())
                .orElseThrow(() -> new RuntimeException("Sender not found"));

        Account receiver = accountRepository
                .findById(request.getReceiverId())
                .orElseThrow(() -> new RuntimeException("Receiver not found"));

        if (sender.getBalance() < request.getAmount()) {
            throw new RuntimeException("Insufficient balance");
        }

        sender.setBalance(
                sender.getBalance() - request.getAmount()
        );

        receiver.setBalance(
                receiver.getBalance() + request.getAmount()
        );

        accountRepository.save(sender);
        accountRepository.save(receiver);
    }
}