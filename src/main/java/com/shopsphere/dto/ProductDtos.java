package com.shopsphere.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

public class ProductDtos {

    // What a seller sends to create a product
    public record CreateProductRequest(
            @NotBlank String name,
            String description,
            @Positive BigDecimal price,
            @PositiveOrZero Integer stock,
            String category,
            Long sellerId
    ) {}

    // What the API returns - never expose the raw entity or seller's password etc.
    public record ProductResponse(
            Long id,
            String name,
            String description,
            BigDecimal price,
            Integer stock,
            String category,
            Long sellerId
    ) {}
}
