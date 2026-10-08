package com.shopsphere.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.List;

public class OrderDtos {

    public record OrderItemRequest(
            @NotNull Long productId,
            @Positive Integer quantity
    ) {}

    public record CreateOrderRequest(
            @NotNull Long customerId,
            @NotEmpty List<OrderItemRequest> items
    ) {}

    public record OrderResponse(
            Long id,
            Long customerId,
            BigDecimal totalAmount,
            String status,
            List<OrderItemResponse> items
    ) {}

    public record OrderItemResponse(
            Long productId,
            String productName,
            Integer quantity,
            BigDecimal priceAtPurchase
    ) {}
}
