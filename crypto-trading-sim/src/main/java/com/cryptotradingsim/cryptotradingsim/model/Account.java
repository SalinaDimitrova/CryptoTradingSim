package com.cryptotradingsim.cryptotradingsim.model;

import java.math.BigDecimal;

public record Account(
        long id,
        String firstName,
        String lastName,
        String email,
        BigDecimal balance,
        String currency
){}