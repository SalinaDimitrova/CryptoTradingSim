package com.cryptotradingsim.cryptotradingsim.websocket;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class WebSocketController {

    private final WebSocketService webSocketService;

    public WebSocketController(WebSocketService webSocketService) {
        this.webSocketService = webSocketService;
    }

    // This endpoint listens for messages from the client (subscription requests)
    @MessageMapping("/subscribe")  // When a client subscribes to the /app/subscribe endpoint
    @SendTo("/topic/price-update") // This is where the server sends messages to the client
    public String subscribeToPriceUpdates(String cryptoSymbol) {
        // Listen to Kraken API or simulate with mock data
        return "Subscribed to " + cryptoSymbol;
    }

    // This is an example method to send updates to clients (to be called when price changes)
    public void sendPriceUpdate(String cryptoSymbol, String price) {
        webSocketService.sendPriceUpdate(cryptoSymbol, price);
    }
}
