package com.cryptotradingsim.cryptotradingsim.websocket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class WebSocketService {

    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    public WebSocketService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    // This method will send price updates to all connected clients
    public void sendPriceUpdate(String cryptoSymbol, String price) {
        messagingTemplate.convertAndSend("/topic/price-update/" + cryptoSymbol, price);
    }
}
