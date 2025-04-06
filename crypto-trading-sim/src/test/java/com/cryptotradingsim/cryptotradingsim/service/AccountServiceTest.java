package com.cryptotradingsim.cryptotradingsim.service;

import com.cryptotradingsim.cryptotradingsim.repository.AccountRepository;
import com.cryptotradingsim.cryptotradingsim.repository.OrderRepository;
import com.cryptotradingsim.cryptotradingsim.repository.PortfolioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.math.BigDecimal;

import static org.mockito.Mockito.*;

class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private PortfolioRepository portfolioRepository;

    @InjectMocks
    private AccountService accountService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void resetAccount_shouldDeleteOrdersAndHoldingsAndResetBalance() {
        long accountId = 1L;

        accountService.resetAccount(accountId);

        verify(orderRepository).deleteOrdersByAccountId(accountId);
        verify(portfolioRepository).deleteAllHoldingsByAccountId(accountId);
        verify(accountRepository).resetAccount(accountId, new BigDecimal("10000.00"), "USD");

        verifyNoMoreInteractions(orderRepository, portfolioRepository, accountRepository);
    }
}
