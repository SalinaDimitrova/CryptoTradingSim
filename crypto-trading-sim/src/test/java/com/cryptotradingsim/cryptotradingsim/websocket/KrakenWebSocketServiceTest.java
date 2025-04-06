package com.cryptotradingsim.cryptotradingsim.websocket;

import com.cryptotradingsim.cryptotradingsim.model.CryptoPrice;
import com.cryptotradingsim.cryptotradingsim.model.WebSocketMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class KrakenWebSocketServiceTest {

    @Mock
    private BroadcastService broadcastService;

    @InjectMocks
    private KrakenWebSocketService krakenWebSocketService;

    private final String testWsUrl = "wss://fake.kraken.test";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        krakenWebSocketService = new KrakenWebSocketService(testWsUrl, broadcastService);
    }

    @Test
    void getPriceForTicker_shouldReturnNull_whenSymbolNotCached() {
        assertNull(krakenWebSocketService.getPriceForTicker("BTC/USD"));
    }

    @Test
    void onMessage_shouldCachePriceAndBroadcast_whenValid() throws Exception {
        String symbol = "BTC/USD";
        BigDecimal price = new BigDecimal("62000.55");

        CryptoPrice cryptoPrice = new CryptoPrice(symbol, price);
        WebSocketMessage message = new WebSocketMessage(List.of(cryptoPrice));
        String json = new ObjectMapper().writeValueAsString(message);

        krakenWebSocketService.onMessage(json);

        assertEquals(price, krakenWebSocketService.getPriceForTicker(symbol));
        verify(broadcastService).broadcastPrices(argThat(map -> price.equals(map.get(symbol))));
    }

    @Test
    void onMessage_shouldIgnoreMalformedJson() {
        String invalidJson = "{ not: valid json }";
        assertDoesNotThrow(() -> krakenWebSocketService.onMessage(invalidJson));
    }

    @Test
    void onMessage_shouldSkipInvalidSymbolsAndZeroPrice() throws Exception {
        CryptoPrice nullSymbol = new CryptoPrice(null, new BigDecimal("1000"));
        CryptoPrice zeroPrice = new CryptoPrice("ETH/USD", BigDecimal.ZERO);
        CryptoPrice blankSymbol = new CryptoPrice("   ", new BigDecimal("500"));

        WebSocketMessage message = new WebSocketMessage(List.of(nullSymbol, zeroPrice, blankSymbol));
        String json = new ObjectMapper().writeValueAsString(message);

        krakenWebSocketService.onMessage(json);

        assertNull(krakenWebSocketService.getPriceForTicker("ETH/USD"));
        verifyNoInteractions(broadcastService);
    }
}
