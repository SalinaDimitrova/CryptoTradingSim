package com.cryptotradingsim.cryptotradingsim.websocket;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.common.util.StringUtils;
import jakarta.annotation.PostConstruct;
import jakarta.websocket.*;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.net.URI;
import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

@ClientEndpoint
@Service
public class KrakenWebSocketService
{

    private static final String krakenWebSocketUri = "wss://ws.kraken.com/v2";

    private final ObjectMapper objectMapper;
    private final Map<String, BigDecimal> cryptoPrices = new ConcurrentHashMap<>();

    public KrakenWebSocketService()
    {
        this.objectMapper = new ObjectMapper();
    }

    @PostConstruct
    public void init()
    {
        connectToKrakenWebSocket();
    }

    private void connectToKrakenWebSocket()
    {
        try
        {
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            container.connectToServer(this, new URI(krakenWebSocketUri));
        }
        catch (Exception e)
        {
            System.out.println("Error connecting to Kraken WebSocket: " + e.getMessage());
        }
    }

    @OnOpen
    public void onOpen(Session session)
    {
        try
        {
            SubscribeMessage subscribeMessage = SubscribeMessage.createDefaultSubscription();
            String jsonMessage = objectMapper.writeValueAsString(subscribeMessage);
            session.getAsyncRemote().sendText(jsonMessage);
            System.out.println("Subscribed to Kraken ticker channel: " + jsonMessage);
        }
        catch (Exception e)
        {
            System.out.println("Error sending subscription message: " + e.getMessage());
        }
    }

    @OnMessage
    public void onMessage(String message)
    {
        try
        {
            System.out.println("Received message: " + message);

            JsonNode jsonNode = objectMapper.readTree(message);
            JsonNode dataNode = jsonNode.path("data");

            if (dataNode.isArray() && dataNode.size() > 0)
            {
                JsonNode firstData = dataNode.get(0);

                String symbol = firstData.path("symbol").asText(null);
                BigDecimal lastPrice = new BigDecimal(firstData.path("last").toString());

                if (StringUtils.isBlank(symbol))
                {
                    CryptoPricePayloadDTO payload = new CryptoPricePayloadDTO(symbol, lastPrice);


                    cryptoPrices.put(symbol, lastPrice);
                    System.out.println("Updated price for " + symbol + ": " + lastPrice);
                }
            }
        }
        catch (Exception e)
        {
            System.out.println("Error processing WebSocket message: " + e.getMessage());
        }
    }


    public Map<String, BigDecimal> getCryptoPrices()
    {
        return Collections.unmodifiableMap(cryptoPrices);
    }
}
