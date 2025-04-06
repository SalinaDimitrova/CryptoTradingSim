package com.cryptotradingsim.cryptotradingsim.websocket;

import com.cryptotradingsim.cryptotradingsim.model.CryptoPrice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Service
public class BroadcastService {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final SimpMessagingTemplate messagingTemplate;

    public BroadcastService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void broadcastPrices(Map<String, BigDecimal> cryptoPrices) {

        List<CryptoPrice> prices = cryptoPrices.entrySet()
                                                .stream()
                                                .map(entry ->
                                                        new CryptoPrice(entry.getKey(),
                                                        entry.getValue()))
                                                .toList();

        messagingTemplate.convertAndSend("/topic/prices", prices);
        logger.info("Broadcasted price update: " + prices);
    }
}
