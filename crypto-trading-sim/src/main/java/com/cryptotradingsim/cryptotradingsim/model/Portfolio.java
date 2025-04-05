package com.cryptotradingsim.cryptotradingsim.model;

import java.math.BigDecimal;

public record Portfolio(
        int id,
        String symbol,
        BigDecimal quantity
){}


