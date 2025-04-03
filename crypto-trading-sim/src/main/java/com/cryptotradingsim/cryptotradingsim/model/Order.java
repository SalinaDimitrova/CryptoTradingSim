package com.cryptotradingsim.cryptotradingsim.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Order
{
    private int orderId;
    private long accountId;
    private String symbol;
    private BigDecimal quantity;
    private BigDecimal price;
    private String type;
    private String status;
    private LocalDateTime timeOrdered;
    private LocalDateTime timeExecuted;
    private BigDecimal profitLoss;

    public Order() {}

    public Order(int orderId, long accountId,
                 String symbol, BigDecimal quantity,
                 BigDecimal price, String type, String status,
                 LocalDateTime timeOrdered, LocalDateTime timeExecuted, BigDecimal profitLoss)
    {
        this.orderId = orderId;
        this.accountId = accountId;
        this.symbol = symbol;
        this.quantity = quantity;
        this.price = price;
        this.type = type;
        this.status = status;
        this.timeOrdered = timeOrdered;
        this.timeExecuted = timeExecuted;
        this.profitLoss = profitLoss;
    }

    public void setOrderId(int orderId)
    {
        this.orderId = orderId;
    }

    public void setAccountId(long accountId)
    {
        this.accountId = accountId;
    }

    public void setSymbol(String symbol)
    {
        this.symbol = symbol;
    }

    public void setQuantity(BigDecimal quantity)
    {
        this.quantity = quantity;
    }

    public void setPrice(BigDecimal price)
    {
        this.price = price;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public void setTimeOrdered(LocalDateTime timeOrdered)
    {
        this.timeOrdered = timeOrdered;
    }

    public void setTimeExecuted(LocalDateTime timeExecuted)
    {
        this.timeExecuted = timeExecuted;
    }

    public void setProfitLoss(BigDecimal profitLoss)
    {
        this.profitLoss = profitLoss;
    }

    public int getOrderId()
    {
        return orderId;
    }

    public long getAccountId()
    {
        return accountId;
    }

    public String getSymbol()
    {
        return symbol;
    }

    public BigDecimal getQuantity()
    {
        return quantity;
    }

    public BigDecimal getPrice()
    {
        return price;
    }

    public String getType()
    {
        return type;
    }

    public String getStatus()
    {
        return status;
    }

    public LocalDateTime getTimeOrdered()
    {
        return timeOrdered;
    }

    public LocalDateTime getTimeExecuted()
    {
        return timeExecuted;
    }

    public BigDecimal getProfitLoss()
    {
        return profitLoss;
    }
}
