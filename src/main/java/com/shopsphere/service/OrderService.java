package com.shopsphere.service;

import com.shopsphere.dto.OrderDtos.*;
import com.shopsphere.entity.Order;
import com.shopsphere.entity.OrderItem;
import com.shopsphere.entity.Product;
import com.shopsphere.entity.User;
import com.shopsphere.repository.OrderRepository;
import com.shopsphere.repository.ProductRepository;
import com.shopsphere.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public OrderService(OrderRepository orderRepository, ProductRepository productRepository,
                         UserRepository userRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    /**
     * The checkout flow. Wrapped in a single transaction so either the whole
     * order succeeds (order created + stock decremented) or nothing does.
     *
     * NOTE: In Week 3 you'll add @Version-based optimistic locking on Product
     * so that two customers can't both buy "the last item in stock" at the
     * same time. Right now this method just does the naive version - come
     * back and stress-test it with concurrent requests once locking is added.
     */
    @Transactional
    public OrderResponse checkout(CreateOrderRequest request) {
        User customer = userRepository.findById(request.customerId())
                .orElseThrow(() -> new IllegalArgumentException("Customer not found: " + request.customerId()));

        Order order = new Order();
        order.setCustomer(customer);

        BigDecimal total = BigDecimal.ZERO;

        for (OrderItemRequest itemReq : request.items()) {
            Product product = productRepository.findById(itemReq.productId())
                    .orElseThrow(() -> new IllegalArgumentException("Product not found: " + itemReq.productId()));

            if (product.getStock() < itemReq.quantity()) {
                throw new IllegalStateException("Insufficient stock for product: " + product.getName());
            }

            // Decrement stock now - the @Version field on Product will cause
            // a failure here if another transaction modified it concurrently
            product.setStock(product.getStock() - itemReq.quantity());
            productRepository.save(product);

            OrderItem orderItem = new OrderItem();
            orderItem.setProduct(product);
            orderItem.setQuantity(itemReq.quantity());
            orderItem.setPriceAtPurchase(product.getPrice());
            order.addItem(orderItem);

            total = total.add(product.getPrice().multiply(BigDecimal.valueOf(itemReq.quantity())));
        }

        order.setTotalAmount(total);
        Order saved = orderRepository.save(order);

        // TODO (Week 4): publish an "order.placed" event to Kafka here instead
        // of doing notification/inventory-sync work inline.

        return toResponse(saved);
    }

    public List<OrderResponse> getOrdersForCustomer(Long customerId) {
        return orderRepository.findByCustomerId(customerId)
                .stream().map(this::toResponse).toList();
    }

    private OrderResponse toResponse(Order order) {
        List<OrderItemResponse> items = order.getItems().stream()
                .map(i -> new OrderItemResponse(
                        i.getProduct().getId(), i.getProduct().getName(),
                        i.getQuantity(), i.getPriceAtPurchase()))
                .toList();

        return new OrderResponse(
                order.getId(), order.getCustomer().getId(), order.getTotalAmount(),
                order.getStatus().name(), items
        );
    }
}
