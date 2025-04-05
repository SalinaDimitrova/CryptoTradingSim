package com.cryptotradingsim.cryptotradingsim.model;

import java.math.BigDecimal;

public record Crypto (
        String name,
        String symbol,
        BigDecimal price,
        double quantity
){}


