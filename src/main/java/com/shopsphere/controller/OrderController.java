package com.shopsphere.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shopsphere.dto.OrderDtos.CreateOrderRequest;
import com.shopsphere.dto.OrderDtos.OrderResponse;
import com.shopsphere.service.OrderService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/checkout")
    @org.springframework.security.access.prepost.PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<OrderResponse> checkout(@Valid @RequestBody CreateOrderRequest request) {
        OrderResponse order = orderService.checkout(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(order);
    }

    @GetMapping("/customer/{customerId}")
    public List<OrderResponse> getOrdersForCustomer(@PathVariable Long customerId) {
        return orderService.getOrdersForCustomer(customerId);
    }
}
