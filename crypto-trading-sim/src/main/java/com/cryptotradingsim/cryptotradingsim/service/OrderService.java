package com.cryptotradingsim.cryptotradingsim.service;

import com.cryptotradingsim.cryptotradingsim.model.Account;
import com.cryptotradingsim.cryptotradingsim.model.Order;
import com.cryptotradingsim.cryptotradingsim.model.Portfolio;
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

    public void placeOrder(long accountId, String type, String symbol, BigDecimal quantity, BigDecimal price) {

        Account account = accountRepository.getAccount();
        BigDecimal cost = price.multiply(quantity);
        LocalDateTime now = LocalDateTime.now();
        String status;

        boolean success = false;
        BigDecimal profitLoss = null; // ✅ NEW

        if ("BUY".equalsIgnoreCase(type)) {
            if (account.balance().compareTo(cost) >= 0) {
                accountRepository.updateBalance(account.id(), account.balance().subtract(cost));

                try {
                    Portfolio existing = portfolioRepository.findBySymbol(symbol);
                    BigDecimal newQty = existing.getQuantity().add(quantity);
                    portfolioRepository.updateHolding(symbol, newQty.doubleValue());
                } catch (Exception e) {
                    portfolioRepository.insertHolding(symbol, quantity.doubleValue());
                }

                success = true;
            }
        } else if ("SELL".equalsIgnoreCase(type)) {
            try {
                Portfolio existing = portfolioRepository.findBySymbol(symbol);
                if (existing.getQuantity().compareTo(quantity) >= 0) {

                    BigDecimal gain = quantity.multiply(price);
                    accountRepository.updateBalance(account.id(), account.balance().add(gain));

                    BigDecimal remaining = existing.getQuantity().subtract(quantity);
                    if (remaining.compareTo(BigDecimal.ZERO) == 0) {
                        portfolioRepository.deleteHolding(symbol);
                    } else {
                        portfolioRepository.updateHolding(symbol, remaining.doubleValue());
                    }

                    success = true;

                    // ✅ NEW: Calculate profit/loss
                    List<Order> pastBuys = orderRepository.getBuyOrdersBySymbol(symbol);
                    BigDecimal totalBought = BigDecimal.ZERO;
                    BigDecimal totalSpent = BigDecimal.ZERO;

                    for (Order buy : pastBuys) {
                        totalBought = totalBought.add(buy.getQuantity());
                        totalSpent = totalSpent.add(buy.getPrice().multiply(buy.getQuantity()));
                    }

                    if (totalBought.compareTo(BigDecimal.ZERO) > 0) {
                        BigDecimal avgBuyPrice = totalSpent.divide(totalBought, 2, BigDecimal.ROUND_HALF_UP);
                        profitLoss = price.subtract(avgBuyPrice).multiply(quantity);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        status = success ? "EXECUTED" : "FAILED";

        Order order = new Order(
                0,                     // orderId
                accountId,
                symbol,               // 👈 symbol comes BEFORE type in your working constructor
                quantity,
                price,
                type,
                status,
                now,
                success ? now : null,
                profitLoss
        );

        System.out.println("✅ Saving order with values:");
        System.out.println("accountId: " + accountId);
        System.out.println("type: " + type);
        System.out.println("symbol: " + symbol);
        System.out.println("quantity: " + quantity);
        System.out.println("price: " + price);
        System.out.println("status: " + status);
        System.out.println("timeOrdered: " + now);
        System.out.println("timeExecuted: " + (success ? now : null));

        try {
            orderRepository.saveOrder(order);
        } catch (Exception e) {
            System.out.println("❌ Failed to save order!");
            e.printStackTrace();
        }

        // ✅ NEW: Set profit/loss if available
        if (profitLoss != null) {
            order.setProfitLoss(profitLoss);
        }

        orderRepository.saveOrder(order);
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
