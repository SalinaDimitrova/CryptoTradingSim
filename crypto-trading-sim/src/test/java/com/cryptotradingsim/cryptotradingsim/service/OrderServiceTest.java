package com.cryptotradingsim.cryptotradingsim.service;

import com.cryptotradingsim.cryptotradingsim.model.*;
import com.cryptotradingsim.cryptotradingsim.repository.OrderRepository;
import com.cryptotradingsim.cryptotradingsim.repository.PortfolioRepository;
import com.cryptotradingsim.cryptotradingsim.websocket.KrakenWebSocketService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private AccountService accountService;

    @Mock
    private PortfolioRepository portfolioRepository;

    @Mock
    private KrakenWebSocketService krakenWebSocketService;

    @InjectMocks
    private OrderService orderService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void placeOrder_shouldPlaceBuyOrderSuccessfully() {
        long accountId = 1L;
        String symbol = "BTC/USD";
        BigDecimal quantity = new BigDecimal("0.5");
        BigDecimal price = new BigDecimal("50000");
        BigDecimal balance = new BigDecimal("30000");

        OrderRequest orderRequest = new OrderRequest(symbol, quantity, price, OrderType.BUY);

        when(krakenWebSocketService.getPriceForTicker(symbol)).thenReturn(price);
        when(accountService.getAccount(accountId)).thenReturn(
                new Account(accountId, "John", "Doe", "john@example.com", balance, "USD")
        );
        when(orderRepository.saveOrder(eq(accountId), any())).thenReturn(100L);
        when(orderRepository.getOrderById(100L)).thenReturn(
                new Order(100L, accountId, symbol, quantity, price, OrderType.BUY, OrderStatus.ORDERED, LocalDateTime.now(), null, null)
        );

        orderService.placeOrder(accountId, orderRequest);

        verify(accountService).updateBalance(accountId, balance.subtract(price.multiply(quantity)));
        verify(orderRepository).markOrderExecuted(eq(100L), any(), isNull());
    }

    @Test
    void placeOrder_shouldThrowException_whenBalanceIsInsufficient() {
        long accountId = 2L;
        String symbol = "BTC/USD";
        BigDecimal quantity = new BigDecimal("1");
        BigDecimal price = new BigDecimal("50000");
        BigDecimal balance = new BigDecimal("10000");

        OrderRequest orderRequest = new OrderRequest(symbol, quantity, price, OrderType.BUY);

        when(krakenWebSocketService.getPriceForTicker(symbol)).thenReturn(price);
        when(accountService.getAccount(accountId)).thenReturn(
                new Account(accountId, "Jane", "Smith", "jane@example.com", balance, "USD")
        );

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            orderService.placeOrder(accountId, orderRequest);
        });

        assertEquals("Insufficient balance", exception.getMessage());
    }

    @Test
    void placeOrder_shouldThrowException_whenPortfolioQuantityIsInsufficient() {
        long accountId = 3L;
        String symbol = "BTC/USD";
        BigDecimal quantity = new BigDecimal("2");
        BigDecimal price = new BigDecimal("50000");

        OrderRequest orderRequest = new OrderRequest(symbol, quantity, price, OrderType.SELL);

        when(krakenWebSocketService.getPriceForTicker(symbol)).thenReturn(price);
        when(accountService.getAccount(accountId)).thenReturn(
                new Account(accountId, "Bob", "Brown", "bob@example.com", new BigDecimal("100000"), "USD")
        );
        when(portfolioRepository.findBySymbol(symbol)).thenReturn(
                Optional.of(new Portfolio(accountId, symbol, new BigDecimal("1")))
        );

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            orderService.placeOrder(accountId, orderRequest);
        });

        assertEquals("Insufficient quantity", exception.getMessage());
    }

    @Test
    void getOrdersByAccountId_shouldReturnOrderList() {
        long accountId = 4L;
        List<Order> mockOrders = List.of(
                new Order(1, accountId, "ETH/USD", new BigDecimal("0.2"), new BigDecimal("2000"),
                        OrderType.BUY, OrderStatus.EXECUTED, LocalDateTime.now(), LocalDateTime.now(), null)
        );

        when(orderRepository.getOrdersByAccountId(accountId)).thenReturn(mockOrders);

        List<Order> orders = orderService.getOrdersByAccountId(accountId);

        assertNotNull(orders);
        assertEquals(1, orders.size());
        assertEquals("ETH/USD", orders.get(0).symbol());
    }
}
