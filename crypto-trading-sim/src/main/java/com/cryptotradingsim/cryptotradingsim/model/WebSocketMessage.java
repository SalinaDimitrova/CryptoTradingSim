package com.cryptotradingsim.cryptotradingsim.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record WebSocketMessage(List<CryptoPrice> data) {
}
