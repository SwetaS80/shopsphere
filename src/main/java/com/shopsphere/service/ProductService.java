package com.shopsphere.service;

import com.shopsphere.dto.ProductDtos.CreateProductRequest;
import com.shopsphere.dto.ProductDtos.ProductResponse;
import com.shopsphere.entity.Product;
import com.shopsphere.entity.User;
import com.shopsphere.repository.ProductRepository;
import com.shopsphere.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public ProductService(ProductRepository productRepository, UserRepository userRepository) {
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    public ProductResponse createProduct(CreateProductRequest request) {
        User seller = userRepository.findById(request.sellerId())
                .orElseThrow(() -> new IllegalArgumentException("Seller not found: " + request.sellerId()));

        Product product = new Product();
        product.setName(request.name());
        product.setDescription(request.description());
        product.setPrice(request.price());
        product.setStock(request.stock());
        product.setCategory(request.category());
        product.setSeller(seller);

        Product saved = productRepository.save(product);
        return toResponse(saved);
    }

    public Page<ProductResponse> listProducts(String category, Pageable pageable) {
        Page<Product> page = (category == null || category.isBlank())
                ? productRepository.findAll(pageable)
                : productRepository.findByCategory(category, pageable);
        return page.map(this::toResponse);
    }

    public ProductResponse getProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Product not found: " + id));
        return toResponse(product);
    }

    private ProductResponse toResponse(Product p) {
        return new ProductResponse(
                p.getId(), p.getName(), p.getDescription(), p.getPrice(),
                p.getStock(), p.getCategory(), p.getSeller().getId()
        );
    }
}
