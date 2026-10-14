package com.demo.upi_offline_mesh.service;


import com.demo.upi_offline_mesh.model.Account;
import com.demo.upi_offline_mesh.repository.AccountRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AccountService {

    private final AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public Account createAccount(String name, long balance) {
        Account account = new Account(name, balance);
        return accountRepository.save(account);
    }

    public List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }
}