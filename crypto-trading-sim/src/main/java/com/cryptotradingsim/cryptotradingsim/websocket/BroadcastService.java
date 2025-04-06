package com.cryptotradingsim.cryptotradingsim.websocket;

import com.cryptotradingsim.cryptotradingsim.model.CryptoPrice;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class BroadcastService {

    private final SimpMessagingTemplate messagingTemplate;

    public BroadcastService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void broadcastPriceUpdate(CryptoPrice cryptoPrice) {
        messagingTemplate.convertAndSend("/topic/prices", cryptoPrice);
        System.out.println("Broadcasted price update: " + cryptoPrice.toString());
    }
}
