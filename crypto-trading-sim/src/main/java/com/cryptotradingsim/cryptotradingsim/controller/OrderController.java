package com.cryptotradingsim.cryptotradingsim.controller;

import com.cryptotradingsim.cryptotradingsim.model.Order;
import com.cryptotradingsim.cryptotradingsim.model.OrderRequest;
import com.cryptotradingsim.cryptotradingsim.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/accounts/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public List<Order> getOrdersByAccount() {
        return orderService.getOrdersByAccountId(1);
    }

    @PostMapping
    public ResponseEntity<String> placeOrder(
            @RequestBody OrderRequest order) {
        orderService.placeOrder(1, order);
        return ResponseEntity.status(HttpStatus.CREATED).body("Order placed successfully");
    }

    @PostMapping("/{id}/execute")
    public ResponseEntity<String> executeOrder(@PathVariable int id) {
        orderService.executeOrder(id);
        return ResponseEntity.ok("Order executed");
    }
}