package com.cryptotradingsim.cryptotradingsim.model;

import java.math.BigDecimal;

public class Portfolio
{
    private int id;
    private String symbol;
    private BigDecimal quantity;

    public Portfolio(){}

    public Portfolio(int id, String symbol, BigDecimal quantity)
    {
        this.id = id;
        this.symbol = symbol;
        this.quantity = quantity;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public void setSymbol(String symbol)
    {
        this.symbol = symbol;
    }

    public void setQuantity(BigDecimal quantity)
    {
        this.quantity = quantity;
    }

    public int getId()
    {
        return id;
    }

    public String getSymbol()
    {
        return symbol;
    }

    public BigDecimal getQuantity()
    {
        return quantity;
    }
}