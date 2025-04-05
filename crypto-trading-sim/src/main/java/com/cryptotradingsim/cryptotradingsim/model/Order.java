package com.cryptotradingsim.cryptotradingsim.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record Order(
    int orderId,
    long accountId,
    String symbol,
    BigDecimal quantity,
    BigDecimal price,
    OrderType type,
    OrderStatus status,
    LocalDateTime timeOrdered,
    LocalDateTime timeExecuted,
    BigDecimal profitLoss
){}