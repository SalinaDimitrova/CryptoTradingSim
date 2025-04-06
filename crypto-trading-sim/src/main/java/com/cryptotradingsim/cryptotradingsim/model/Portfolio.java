package com.cryptotradingsim.cryptotradingsim.model;

import java.math.BigDecimal;

public record Portfolio(
        long id,
        String symbol,
        BigDecimal quantity
){}


