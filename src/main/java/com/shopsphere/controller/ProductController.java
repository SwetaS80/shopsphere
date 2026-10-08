package com.shopsphere.controller;

import com.shopsphere.dto.ProductDtos.CreateProductRequest;
import com.shopsphere.dto.ProductDtos.ProductResponse;
import com.shopsphere.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    @org.springframework.security.access.prepost.PreAuthorize("hasRole('SELLER')") 
    public ResponseEntity<ProductResponse> create(@Valid @RequestBody CreateProductRequest request) {
        ProductResponse created = productService.createProduct(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping
    public Page<ProductResponse> list(
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return productService.listProducts(category, pageable);
    }

    @GetMapping("/{id}")
    public ProductResponse getOne(@PathVariable Long id) {
        return productService.getProduct(id);
    }
}
