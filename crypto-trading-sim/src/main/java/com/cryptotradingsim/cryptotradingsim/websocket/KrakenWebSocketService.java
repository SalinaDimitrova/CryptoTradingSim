package com.cryptotradingsim.cryptotradingsim.websocket;

import com.cryptotradingsim.cryptotradingsim.model.CryptoPrice;
import com.cryptotradingsim.cryptotradingsim.model.WebSocketMessage;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.common.util.StringUtils;
import jakarta.annotation.PostConstruct;
import jakarta.websocket.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.net.URI;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@ClientEndpoint
@Service
public class KrakenWebSocketService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final String krakenWebSocketUrl;
    private final ObjectMapper objectMapper;
    private final Map<String, BigDecimal> cryptoPrices = new ConcurrentHashMap<>();
    private final BroadcastService broadcastService;

    public KrakenWebSocketService(@Value("${kraken.websocket.endpoint}")
                                  String krakenWebSocketUrl,
                                  BroadcastService broadcastService) {
        this.krakenWebSocketUrl = krakenWebSocketUrl;
        this.objectMapper = new ObjectMapper();
        this.broadcastService = broadcastService;
    }

    @PostConstruct
    public void init() {
        connectToKrakenWebSocket();
    }

    private void connectToKrakenWebSocket() {
        try {
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            container.connectToServer(this, new URI(krakenWebSocketUrl));
        } catch (Exception e) {
            logger.error("Error connecting to Kraken WebSocket", e);
        }
    }

    @OnOpen
    public void onOpen(Session session) {
        try {
            String jsonMessage = objectMapper.writeValueAsString(SubscribeMessage.SUBSCRIBE_MESSAGE);
            session.getAsyncRemote().sendText(jsonMessage);
            logger.info("Subscribed to Kraken ticker channel: {}", jsonMessage);
        } catch (Exception e) {
            logger.error("Error sending subscription message", e);
        }
    }

    @OnMessage
    public void onMessage(String message) {

        try {
            logger.info("Received message from Kraken WebSocket: {}", message);
            WebSocketMessage payload = objectMapper.readValue(message, WebSocketMessage.class);

            if(payload.data() == null){
                return;}

            payload.data()
                    .stream()
                    .map(Optional::ofNullable)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .filter(priceUpdate ->
                            !StringUtils.isBlank(priceUpdate.symbol()) &&
                                    BigDecimal.ZERO.compareTo(priceUpdate.price()) < 0)
                    .forEach(data -> {
                        cryptoPrices.put(data.symbol(), data.price());
                        broadcastService.broadcastPriceUpdate(data);
                    });

        } catch (Exception e) {
            logger.warn("Error processing WebSocket message", e);
        }
    }

    public BigDecimal getPriceForTicker(String symbol) {
        return cryptoPrices.get(symbol);
    }
}
