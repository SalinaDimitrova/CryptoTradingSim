package com.cryptotradingsim.cryptotradingsim.service;

import com.cryptotradingsim.cryptotradingsim.model.OrderStatus;
import com.cryptotradingsim.cryptotradingsim.model.OrderType;
import com.cryptotradingsim.cryptotradingsim.model.*;
import com.cryptotradingsim.cryptotradingsim.repository.OrderRepository;
import com.cryptotradingsim.cryptotradingsim.repository.AccountRepository;
import com.cryptotradingsim.cryptotradingsim.repository.PortfolioRepository;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final AccountRepository accountRepository;
    private final PortfolioRepository portfolioRepository;

    public OrderService(OrderRepository orderRepository,
                        AccountRepository accountRepository,
                        PortfolioRepository portfolioRepository) {
        this.orderRepository = orderRepository;
        this.accountRepository = accountRepository;
        this.portfolioRepository = portfolioRepository;
    }

    public void placeOrder(long accountId, OrderType orderType, String symbol, BigDecimal quantity, BigDecimal frontendPrice) {
        Account account = accountRepository.getAccount();
        BigDecimal backendPrice = getBackendPrice(symbol); // Fetch price from BE
        comparePrices(backendPrice, frontendPrice); // Compare BE & FE prices

        BigDecimal cost = backendPrice.multiply(quantity);
        OrderStatus status = processOrder(orderType, account, symbol, quantity, backendPrice, cost);
        saveOrder(accountId, orderType, symbol, quantity, backendPrice, status);
    }

    private OrderStatus processOrder(OrderType orderType, Account account, String symbol, BigDecimal quantity, BigDecimal backendPrice, BigDecimal cost) {
        boolean success = false;

        if (orderType == OrderType.BUY) {
            success = processBuyOrder(account, symbol, quantity, cost);
        } else if (orderType == OrderType.SELL) {
            success = processSellOrder(account, symbol, quantity, backendPrice);
        }

        return success ? OrderStatus.EXECUTED : OrderStatus.FAILED;
    }

    private boolean processBuyOrder(Account account, String symbol, BigDecimal quantity, BigDecimal cost) {
        if (account.balance().compareTo(cost) >= 0) {
            accountRepository.updateBalance(account.id(), account.balance().subtract(cost));
            updatePortfolio(symbol, quantity);
            return true;
        }
        return false;
    }

    private boolean processSellOrder(Account account, String symbol, BigDecimal quantity, BigDecimal price) {
        try {
            Portfolio existing = portfolioRepository.findBySymbol(symbol);
            if (existing.quantity().compareTo(quantity) >= 0) {
                BigDecimal gain = quantity.multiply(price);
                accountRepository.updateBalance(account.id(), account.balance().add(gain));
                updatePortfolioAfterSale(symbol, existing, quantity);
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private void updatePortfolio(String symbol, BigDecimal quantity) {
        try {
            Portfolio existing = portfolioRepository.findBySymbol(symbol);
            BigDecimal newQty = existing.quantity().add(quantity);
            portfolioRepository.updateHolding(symbol, newQty.doubleValue());
        } catch (Exception e) {
            portfolioRepository.insertHolding(symbol, quantity.doubleValue());
        }
    }

    private void updatePortfolioAfterSale(String symbol, Portfolio existing, BigDecimal quantity) {
        BigDecimal remaining = existing.quantity().subtract(quantity);
        if (remaining.compareTo(BigDecimal.ZERO) == 0) {
            portfolioRepository.deleteHolding(symbol);
        } else {
            portfolioRepository.updateHolding(symbol, remaining.doubleValue());
        }
    }

    private void saveOrder(long accountId, OrderType orderType, String symbol, BigDecimal quantity, BigDecimal backendPrice, OrderStatus status) {
        Order order = new Order(
                0,
                accountId,
                symbol,
                quantity,
                backendPrice,
                orderType,
                status,
                LocalDateTime.now(),
                status == OrderStatus.EXECUTED ? LocalDateTime.now() : null,
                null
        );

        orderRepository.saveOrder(order);
    }

    private BigDecimal getBackendPrice(String symbol) {
        // Simulated backend price retrieval
        return BigDecimal.valueOf(100);
    }

    private void comparePrices(BigDecimal backendPrice, BigDecimal frontendPrice) {
        if (frontendPrice.compareTo(backendPrice) != 0) {
            System.out.println("⚠️ Warning: Frontend price (" + frontendPrice + ") differs from backend price (" + backendPrice + ")");
        }
    }

    public void executeOrder(int orderId) {
        orderRepository.markOrderExecuted(orderId, LocalDateTime.now());
    }

    public List<Order> getAllOrders() {
        return orderRepository.getAllOrders();
    }

    public List<Order> getOrdersByAccountId(long accountId) {
        return orderRepository.getOrdersByAccountId(accountId);
    }
}
