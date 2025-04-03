package com.cryptotradingsim.cryptotradingsim.controller;

import com.cryptotradingsim.cryptotradingsim.model.Order;
import com.cryptotradingsim.cryptotradingsim.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public List<Order> getAllOrders() {
        return orderService.getAllOrders();
    }

    @GetMapping("/account/{accountId}")
    public List<Order> getOrdersByAccount(@PathVariable long accountId) {
        return orderService.getOrdersByAccountId(accountId);
    }

    @PostMapping
    public ResponseEntity<String> placeOrder(@RequestBody Order order) {
        orderService.placeOrder(
                order.getAccountId(),
                order.getType(),
                order.getSymbol(),
                order.getQuantity(),
                order.getPrice()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body("Order placed successfully");
    }

    @PostMapping("/{id}/execute")
    public ResponseEntity<String> executeOrder(@PathVariable int id) {
        orderService.executeOrder(id);
        return ResponseEntity.ok("Order executed");
    }
}