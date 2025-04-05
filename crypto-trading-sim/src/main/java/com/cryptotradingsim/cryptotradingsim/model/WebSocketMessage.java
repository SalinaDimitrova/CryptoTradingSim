package com.cryptotradingsim.cryptotradingsim.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record WebSocketMessage() {
}
