package com.cryptotradingsim.cryptotradingsim.model;

import java.math.BigDecimal;

public record OrderRequest(
        String symbol,
        BigDecimal quantity,
        BigDecimal price,
        OrderType type
) {
}
