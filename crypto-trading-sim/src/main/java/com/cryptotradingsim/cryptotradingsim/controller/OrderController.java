package com.cryptotradingsim.cryptotradingsim.controller;

import com.cryptotradingsim.cryptotradingsim.model.Order;
import com.cryptotradingsim.cryptotradingsim.model.OrderRequest;
import com.cryptotradingsim.cryptotradingsim.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/accounts/{accountId}/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public List<Order> getOrdersByAccount(@PathVariable long accountId) {
        return orderService.getOrdersByAccountId(accountId);
    }

    @PostMapping
    public ResponseEntity<String> placeOrder(@PathVariable long accountId,
                                             @RequestBody OrderRequest order) {
        orderService.placeOrder(accountId, order);
        return ResponseEntity.status(HttpStatus.CREATED)
                             .body("Order placed successfully");
    }
}