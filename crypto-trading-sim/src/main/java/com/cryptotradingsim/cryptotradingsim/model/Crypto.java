package com.cryptotradingsim.cryptotradingsim.model;

public class Crypto {
    private String name;
    private String symbol;
    private double price;
    private double volume;

    public Crypto(String name, String symbol, double price, double volume) {
        this.name = name;
        this.symbol = symbol;
        this.price = price;
        this.volume = volume;
    }

    // Getters and setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getVolume() {
        return volume;
    }

    public void setVolume(double volume) {
        this.volume = volume;
    }
}
