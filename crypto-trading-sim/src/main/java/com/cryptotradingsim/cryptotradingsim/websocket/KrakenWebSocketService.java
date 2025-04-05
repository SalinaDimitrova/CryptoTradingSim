package com.cryptotradingsim.cryptotradingsim.websocket;

import com.cryptotradingsim.cryptotradingsim.model.CryptoPrice;
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
import java.util.concurrent.ConcurrentHashMap;

@ClientEndpoint
@Service
public class KrakenWebSocketService {

    private static final Logger logger = LoggerFactory.getLogger(KrakenWebSocketService.class);
    private static final String DATA_JSON_PATH = "data";
    private static final String SYMBOL_JSON_PATH = "symbol";
    private static final String LAST_PRICE_JSON_PATH = "last";

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
            System.out.println("Message received: " + message);
            JsonNode jsonNode = objectMapper.readTree(message);
            JsonNode dataNode = jsonNode.path(DATA_JSON_PATH);

            if (dataNode.isArray() && dataNode.size() > 0) {
                JsonNode firstData = dataNode.get(0);

                String symbol = firstData.path(SYMBOL_JSON_PATH).asText(null);
                BigDecimal lastPrice = new BigDecimal(firstData.path(LAST_PRICE_JSON_PATH).asText());

                if (!StringUtils.isBlank(symbol) &&
                        BigDecimal.ZERO.compareTo(lastPrice) > 0) {
                    String normalizedSymbol = symbol.replace("/USD", "");
                    cryptoPrices.put(normalizedSymbol, lastPrice);
                    logger.info("Updated price for {}: {}", normalizedSymbol, lastPrice);

                    broadcastService.broadcastPriceUpdate(new CryptoPrice(normalizedSymbol, lastPrice));
                }
            }
        } catch (Exception e) {
            logger.warn("Error processing WebSocket message", e);
        }
    }

    public Map<String, BigDecimal> getCryptoPrices() {
        return Collections.unmodifiableMap(cryptoPrices);
    }
}
