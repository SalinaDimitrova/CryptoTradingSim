package com.cryptotradingsim.cryptotradingsim;

import com.cryptotradingsim.cryptotradingsim.websocket.KrakenWebSocketClient;
import jakarta.annotation.PostConstruct;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;


@SpringBootApplication
public class CryptoTradingSimApplication {

    private final KrakenWebSocketClient krakenWebSocketClient;

    public CryptoTradingSimApplication(KrakenWebSocketClient krakenWebSocketClient) {
        this.krakenWebSocketClient = krakenWebSocketClient;
    }

    public static void main(String[] args) {
        SpringApplication.run(CryptoTradingSimApplication.class, args);
    }

    @PostConstruct
    public void init() {
        krakenWebSocketClient.connectToKraken();
    }
}
