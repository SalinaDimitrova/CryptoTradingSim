package com.cryptotradingsim.cryptotradingsim.service;

import com.cryptotradingsim.cryptotradingsim.model.Account;
import com.cryptotradingsim.cryptotradingsim.repository.AccountRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class AccountService {

    private final AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public Account getAccount(long accountId) {
        return accountRepository.getAccount(accountId);
    }

    public void updateBalance(long accountId, BigDecimal newBalance) {
        accountRepository.updateBalance(accountId, newBalance);
    }

    public void resetAccount(long accountId) {
        accountRepository.resetAccount( accountId,
                new BigDecimal("10000.00"),
                "USD"
        );
    }
}
