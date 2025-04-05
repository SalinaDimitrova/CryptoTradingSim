package com.cryptotradingsim.cryptotradingsim.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CryptoPrice(String symbol,
                          @JsonProperty("last") BigDecimal price)
{
}
