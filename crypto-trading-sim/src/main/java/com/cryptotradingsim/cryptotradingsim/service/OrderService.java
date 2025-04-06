package com.cryptotradingsim.cryptotradingsim.service;

import com.cryptotradingsim.cryptotradingsim.model.OrderStatus;
import com.cryptotradingsim.cryptotradingsim.model.OrderType;
import com.cryptotradingsim.cryptotradingsim.model.*;
import com.cryptotradingsim.cryptotradingsim.repository.OrderRepository;
import com.cryptotradingsim.cryptotradingsim.service.AccountService;
import com.cryptotradingsim.cryptotradingsim.repository.PortfolioRepository;
import com.cryptotradingsim.cryptotradingsim.websocket.KrakenWebSocketService;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final AccountService accountService;
    private final PortfolioRepository portfolioRepository;
    private final KrakenWebSocketService krakenWebSocketService;

    public OrderService(OrderRepository orderRepository,
                        AccountService accountService,
                        PortfolioRepository portfolioRepository, KrakenWebSocketService krakenWebSocketService) {
        this.orderRepository = orderRepository;
        this.accountService = accountService;
        this.portfolioRepository = portfolioRepository;
        this.krakenWebSocketService = krakenWebSocketService;
    }

    public void placeOrder(long accountId, OrderRequest order) {

        BigDecimal backendPrice = krakenWebSocketService.getPriceForTicker(order.symbol());
        BigDecimal cost = backendPrice.multiply(order.quantity());
        BigDecimal accountBalance = accountService.getAccount(accountId).balance();

        validateOrder(order, backendPrice, accountId, cost, accountBalance);

        OrderStatus status = processOrder(order, accountId, accountBalance, backendPrice, cost);
        saveOrder(accountId, order.type(), order.symbol(), order.quantity(), backendPrice, status);
    }

    private void validateOrder(OrderRequest order, BigDecimal currentPrice, long accountId, BigDecimal cost, BigDecimal accountBalance)
    {
        comparePrices(currentPrice, order.price());

        if(accountBalance.compareTo(cost) < 0)
        {
            throw new RuntimeException("Insufficient balance");
        }
    }

    private OrderStatus processOrder(OrderRequest order, long accountId, BigDecimal accountBalance, BigDecimal backendPrice, BigDecimal cost) {
        boolean success = false;

        if (order.type() == OrderType.BUY) {
            success = processBuyOrder(accountId, accountBalance, order.symbol(), order.quantity(), cost);
        } else if (order.type() == OrderType.SELL) {
            success = processSellOrder(accountId, accountBalance, order.symbol(), order.quantity(), backendPrice);
        }

        return success ? OrderStatus.EXECUTED : OrderStatus.FAILED;
    }

    private boolean processBuyOrder(long accountId, BigDecimal accountBalance, String symbol, BigDecimal quantity, BigDecimal cost) {
        if (accountBalance.compareTo(cost) >= 0) {
            accountService.updateBalance(accountId, accountBalance.subtract(cost));
            updatePortfolio(symbol, quantity);
            return true;
        }
        return false;
    }

    private boolean processSellOrder(long accountId, BigDecimal accountBalance, String symbol, BigDecimal quantity, BigDecimal price) {
        try {
            Portfolio existing = portfolioRepository.findBySymbol(symbol);
            if (existing.quantity().compareTo(quantity) >= 0) {
                BigDecimal gain = quantity.multiply(price);
                accountService.updateBalance(accountId, accountBalance.add(gain));
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
        OrderRequest order = new OrderRequest(
                symbol,
                quantity,
                backendPrice,
                orderType
        );

        orderRepository.saveOrder(accountId, order, backendPrice);
    }

    private void comparePrices(BigDecimal backendPrice, BigDecimal frontendPrice) {
        BigDecimal tolerance = new BigDecimal("0.01");
        BigDecimal difference = frontendPrice.subtract(backendPrice).abs();

        if (difference.compareTo(tolerance) > 0) {
            System.out.println("⚠️ Warning: Frontend price (" + frontendPrice + ") differs from backend price (" + backendPrice + ") by more than " + tolerance);
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
