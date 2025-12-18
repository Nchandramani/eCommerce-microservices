package com.chadramani.order_service.service;

import com.chadramani.order_service.client.ProductClient;
import com.chadramani.order_service.client.UserClient;
import com.chadramani.order_service.dto.ProductDTO;
import com.chadramani.order_service.dto.UserDTO;
import com.chadramani.order_service.event.OrderPlacedEvent; // <--- Make sure this class exists!
import com.chadramani.order_service.model.Order;
import com.chadramani.order_service.repository.OrderRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate; // <--- Import for Kafka
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserClient userClient;
    private final ProductClient productClient;

    // Inject KafkaTemplate to send messages
    // Key = String (Topic Name/Key), Value = OrderPlacedEvent (Your Custom Object)
    private final KafkaTemplate<String, OrderPlacedEvent> kafkaTemplate;

    /**
     * Creates an order.
     * Protected by CircuitBreaker "productService".
     * If Product Service is down/slow, "fallbackCreateOrder" will be called.
     */
    @CircuitBreaker(name = "productService", fallbackMethod = "fallbackCreateOrder")
    public Order createOrder(Long userId, Long productId, Integer quantity) {
        // 1. Verify User
        UserDTO user = userClient.getUserById(userId);
        if (user == null) {
            throw new RuntimeException("User not found with ID: " + userId);
        }

        // 2. Verify Product (Remote Call - Prone to failure)
        ProductDTO product = productClient.getProductById(productId);
        if (product == null) {
            throw new RuntimeException("Product not found with ID: " + productId);
        }

        // 3. Calculate Total
        BigDecimal totalPrice = product.getPrice().multiply(BigDecimal.valueOf(quantity));

        // 4. Save Order to Database
        Order order = new Order();
        order.setUserId(userId);
        order.setProductId(productId);
        order.setQuantity(quantity);
        order.setTotalPrice(totalPrice);
        order.setOrderDate(LocalDateTime.now());

        Order savedOrder = orderRepository.save(order);

        // 5. Send Notification Event to Kafka (Async)
        // We send the Order ID so the Notification Service knows which order to process.
        try {
            kafkaTemplate.send("notificationTopic", new OrderPlacedEvent(savedOrder.getId().toString()));
            System.out.println("Message sent to Kafka topic 'notificationTopic'");
        } catch (Exception e) {
            // Log error but don't fail the order just because notification failed
            System.err.println("Failed to send Kafka message: " + e.getMessage());
        }

        return savedOrder;
    }

    /**
     * Fallback method for createOrder.
     * MUST have the same signature as the original method + Throwable.
     */
    public Order fallbackCreateOrder(Long userId, Long productId, Integer quantity, Throwable t) {
        System.err.println("Fallback triggered for Order. Reason: " + t.getMessage());

        // Return a dummy/safe response so the client doesn't get a 500 error
        Order fallbackOrder = new Order();
        fallbackOrder.setUserId(userId);
        fallbackOrder.setProductId(productId);
        fallbackOrder.setQuantity(quantity);
        fallbackOrder.setTotalPrice(BigDecimal.ZERO); // Indicate failure via 0.00 price
        fallbackOrder.setOrderDate(LocalDateTime.now());

        // Note: You might normally NOT save this to the DB, or save it with status "FAILED"
        return fallbackOrder;
    }

    // --- STANDARD METHODS ---

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public Order getOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found with ID: " + id));
    }
}