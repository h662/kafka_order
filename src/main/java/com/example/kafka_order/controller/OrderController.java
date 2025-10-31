package com.example.kafka_order.controller;

import com.example.kafka_order.event.OrderCreatedEvent;
import com.example.kafka_order.service.OrderEventProducer;
import com.example.kafka_order.service.OrderService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderEventProducer orderEventProducer;
    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<String> createOrder(@RequestBody CreateOrderRequest request) {
        String orderId = "ORDER_" + UUID.randomUUID().toString().substring(0, 8);

        orderService.createOrder(orderId);

        List<OrderCreatedEvent.OrderItem> orderItems = request.getItems()
                .stream()
                .map(item -> OrderCreatedEvent.OrderItem.builder()
                        .productId(item.getProductId())
                        .productName(item.getProductName())
                        .quantity(item.getQuantity())
                        .price(item.getPrice())
                        .build())
                .toList();

        BigDecimal totalAmount = orderItems.stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        OrderCreatedEvent event = OrderCreatedEvent.builder()
                .orderId(orderId)
                .customerId(request.getCustomerId())
                .orderItems(orderItems)
                .totalAmount(totalAmount)
                .build();

        orderEventProducer.publishOrderEvent(event);

        return ResponseEntity.ok(orderId);
    }

    @GetMapping("/{orderId}/status")
    public ResponseEntity<String> getOrderStatus(@PathVariable String orderId) {
        String status = orderService.getOrderStatus(orderId);
        return ResponseEntity.ok(status);
    }

    @Getter
    @Setter
    public static class CreateOrderRequest {
        private String customerId;
        private List<OrderItemRequest> items;
    }

    @Getter
    @Setter
    public static class OrderItemRequest {
        private String productId;
        private String productName;
        private int quantity;
        private BigDecimal price;
    }
}
