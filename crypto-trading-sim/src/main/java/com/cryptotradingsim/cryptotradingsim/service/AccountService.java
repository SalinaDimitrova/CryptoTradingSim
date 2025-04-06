package com.cryptotradingsim.cryptotradingsim.service;

import com.cryptotradingsim.cryptotradingsim.model.Account;
import com.cryptotradingsim.cryptotradingsim.repository.AccountRepository;
import com.cryptotradingsim.cryptotradingsim.repository.OrderRepository;
import com.cryptotradingsim.cryptotradingsim.repository.PortfolioRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final OrderRepository orderRepository;
    private final PortfolioRepository portfolioRepository;

    public AccountService(AccountRepository accountRepository,
                          OrderRepository orderRepository,
                          PortfolioRepository portfolioRepository) {
        this.accountRepository = accountRepository;
        this.orderRepository = orderRepository;
        this.portfolioRepository = portfolioRepository;
    }

    public Account getAccount(long accountId) {
        return accountRepository.getAccount(accountId);
    }

    public void updateBalance(long accountId, BigDecimal newBalance) {
        accountRepository.updateBalance(accountId, newBalance);
    }

    public void resetAccount(long accountId) {
        // Delete orders and holdings first
        orderRepository.deleteOrdersByAccountId(accountId);
        portfolioRepository.deleteAllHoldingsByAccountId(accountId);

        // Reset balance and currency
        accountRepository.resetAccount(accountId, new BigDecimal("10000.00"), "USD");
    }

}
