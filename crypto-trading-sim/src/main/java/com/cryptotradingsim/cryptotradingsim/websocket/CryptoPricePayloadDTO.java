package com.cryptotradingsim.cryptotradingsim.websocket;


public record CryptoPricePayloadDTO(String symbol,
                                    java.math.BigDecimal lastPrice)
{}
