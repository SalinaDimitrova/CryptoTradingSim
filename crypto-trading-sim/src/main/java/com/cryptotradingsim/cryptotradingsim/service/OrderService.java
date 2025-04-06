package com.cryptotradingsim.cryptotradingsim.service;

import com.cryptotradingsim.cryptotradingsim.model.OrderType;
import com.cryptotradingsim.cryptotradingsim.model.*;
import com.cryptotradingsim.cryptotradingsim.repository.OrderRepository;
import com.cryptotradingsim.cryptotradingsim.repository.PortfolioRepository;
import com.cryptotradingsim.cryptotradingsim.websocket.KrakenWebSocketService;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Service
public class OrderService {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final OrderRepository orderRepository;
    private final AccountService accountService;
    private final PortfolioRepository portfolioRepository;
    private final KrakenWebSocketService krakenWebSocketService;

    public OrderService(OrderRepository orderRepository,
                        AccountService accountService,
                        PortfolioRepository portfolioRepository,
                        KrakenWebSocketService krakenWebSocketService) {
        this.orderRepository = orderRepository;
        this.accountService = accountService;
        this.portfolioRepository = portfolioRepository;
        this.krakenWebSocketService = krakenWebSocketService;
    }

    public void placeOrder(long accountId, OrderRequest order) {

        BigDecimal backendPrice = krakenWebSocketService.getPriceForTicker(order.symbol());
        BigDecimal cost = backendPrice.multiply(order.quantity());
        BigDecimal accountBalance = accountService.getAccount(accountId).balance();

        validateOrder(order, backendPrice, cost, accountBalance);

        affectBalanceOnBuyOrder(accountId, order, accountBalance, cost);
        long id = saveOrder(accountId, order.type(), order.symbol(), order.quantity(), backendPrice);

        // Simulation of instant order execution
        executeOrder(id);
    }

    public List<Order> getOrdersByAccountId(long accountId) {
        return orderRepository.getOrdersByAccountId(accountId);
    }

    private void executeOrder(long orderId) {

        Order order = getOrderById(orderId);

        updatePortfolio(order);

        BigDecimal cost = calculateOrderCost(order);
        BigDecimal accountBalance = accountService.getAccount(order.accountId()).balance();

        affectBalanceOnSellOrder(order.accountId(), order, accountBalance, cost);

        BigDecimal profitLoss = calculateProfitAndLoss(order).orElse(null);

        orderRepository.markOrderExecuted(orderId, LocalDateTime.now(), profitLoss);

        logger.info("Successfully executed order with id: " + orderId);
    }

    private void validateOrder(OrderRequest order, BigDecimal currentPrice,
                               BigDecimal cost, BigDecimal accountBalance)
    {

        if(order.type() == null ) {
            throw new IllegalArgumentException("Order type is required");
        }

        comparePrices(currentPrice, order.price());

        if(OrderType.BUY == order.type() && accountBalance.compareTo(cost) < 0)
        {
            throw new RuntimeException("Insufficient balance");
        }

        if(OrderType.SELL == order.type()){
            Portfolio portfolio = portfolioRepository.findBySymbol(order.symbol()).orElseThrow();
            if (portfolio.quantity().compareTo(order.quantity()) < 0) {
                throw new RuntimeException("Insufficient quantity");
            }
        }
    }

    private void affectBalanceOnBuyOrder(long accountId, OrderRequest order, BigDecimal accountBalance, BigDecimal cost) {

        if (order.type() != OrderType.BUY) {
            return;
        }

        try {
            accountService.updateBalance(accountId, accountBalance.subtract(cost));
        }catch (Exception e) {
            logger.error("Failed to affect balance on buy order" + e.getMessage());
            throw e;
        }
    }

    private void affectBalanceOnSellOrder(long accountId, Order order, BigDecimal accountBalance, BigDecimal cost) {
        if(order.type() != OrderType.SELL) {
            return;
        }

        try{
            accountService.updateBalance(accountId, accountBalance.add(cost));
        }catch (Exception e) {
            logger.error("Failed to affect balance on sell order" + e.getMessage());
            throw e;
        }
    }

    private void updatePortfolio(Order order)
    {
        Portfolio existing = portfolioRepository.findBySymbol(order.symbol()).orElse(null);

        if(order.type() == OrderType.BUY) {
            updatePortfolioAfterBuy(order.accountId(), order.symbol(), order.quantity(), existing);
            return;
        }

        updatePortfolioAfterSale(order.symbol(), order.quantity(), existing);

    }

    private void updatePortfolioAfterBuy(long accountId, String symbol, BigDecimal quantity, Portfolio portfolio) {
        if(portfolio != null){
            BigDecimal newQty = portfolio.quantity().add(quantity);
            portfolioRepository.updateHolding(symbol, newQty.doubleValue());
        } else{
            portfolioRepository.insertHolding(accountId, symbol, quantity.doubleValue());
        }
    }

    private void updatePortfolioAfterSale(String symbol, BigDecimal quantity, Portfolio portfolio) {
        if(portfolio == null){
            throw new IllegalArgumentException("Portfolio is required");
        }

        BigDecimal remaining = portfolio.quantity().subtract(quantity);
        if (remaining.compareTo(BigDecimal.ZERO) == 0) {
            portfolioRepository.deleteHolding(symbol);
        } else {
            portfolioRepository.updateHolding(symbol, remaining.doubleValue());
        }
    }

    private long saveOrder(long accountId, OrderType orderType, String symbol, BigDecimal quantity, BigDecimal backendPrice) {
        OrderRequest order = new OrderRequest(
                symbol,
                quantity,
                backendPrice,
                orderType
        );

        return orderRepository.saveOrder(accountId, order);
    }

    private Order getOrderById(long orderId){
        try{
            return orderRepository.getOrderById(orderId);
        }catch (EmptyResultDataAccessException ex){
            throw new NoSuchElementException("Order not found for id: " + orderId);
        }
    }

    private void comparePrices(BigDecimal backendPrice, BigDecimal frontendPrice) {
        BigDecimal tolerance = new BigDecimal("0.1");
        BigDecimal difference = frontendPrice.subtract(backendPrice).abs();

        if (difference.compareTo(tolerance) > 0) {
            logger.warn("Warning: Frontend price (" + frontendPrice + ") differs from backend price (" + backendPrice + ") by more than " + tolerance);
            throw new IllegalArgumentException("Too big gap between frontend price and backend price");
        }
    }

    private Optional<BigDecimal> calculateProfitAndLoss(Order order){
        if(order.type() == OrderType.BUY){
            return Optional.empty();
        }

        List<Order> orders = orderRepository.getExecutedOrdersByAccountIdAndSymbol(order.accountId(), order.symbol());

        BigDecimal sellOrdersQuantity =
                orders.stream()
                .filter(o -> OrderType.SELL == o.type())
                        .map(Order::quantity)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<Order> activeBuyOrders = getActiveBuyOrders(sellOrdersQuantity, orders);

        BigDecimal buyOrdersCostForSellOrder = BigDecimal.ZERO;
        BigDecimal remainingSellOrderQuantity = order.quantity();

        for(Order activeBuyOrder : activeBuyOrders){

            BigDecimal matchedQuantity = remainingSellOrderQuantity.min(activeBuyOrder.quantity());
            buyOrdersCostForSellOrder = buyOrdersCostForSellOrder.add(matchedQuantity.multiply(activeBuyOrder.price()));

            remainingSellOrderQuantity =
                    remainingSellOrderQuantity.subtract(remainingSellOrderQuantity.min(activeBuyOrder.quantity()));

            if(remainingSellOrderQuantity.compareTo(BigDecimal.ZERO) <= 0){
                break;
            }
        }

        BigDecimal profitLoss = calculateOrderCost(order).subtract(buyOrdersCostForSellOrder);

        return Optional.of(profitLoss);
    }

    private static List<Order> getActiveBuyOrders(BigDecimal sellOrderQuantity, List<Order> orders) {

        BigDecimal remainingSellOrderQuantity = sellOrderQuantity;

        List<Order> activeBuyOrders = new ArrayList<>();
        List<Order> buyOrders = orders.stream()
                .filter(order -> OrderType.BUY == order.type())
                .toList();

        for (Order order : buyOrders) {
            if (remainingSellOrderQuantity.compareTo(BigDecimal.ZERO) <= 0) {
                // This buy order was not used to fulfill any previous sells,
                // so it's fully available for current matching
                activeBuyOrders.add(order);
                continue;
            }

            BigDecimal remainingQuantity = order.quantity().subtract(remainingSellOrderQuantity);
            BigDecimal adjustedQuantity = remainingQuantity.compareTo(BigDecimal.ZERO) > 0 ? remainingQuantity : BigDecimal.ZERO;

            remainingSellOrderQuantity = remainingSellOrderQuantity.subtract(order.quantity()).max(BigDecimal.ZERO);

            if (adjustedQuantity.compareTo(BigDecimal.ZERO) > 0) {
                activeBuyOrders.add(new Order(
                        order.orderId(),
                        order.accountId(),
                        order.symbol(),
                        adjustedQuantity,
                        order.price(),
                        order.type(),
                        order.status(),
                        order.timeOrdered(),
                        order.timeExecuted(),
                        order.profitLoss()
                ));
            }
        }

        return activeBuyOrders;
    }

    private static BigDecimal calculateOrderCost(Order order){
        return order.price().multiply(order.quantity());
    }
}
