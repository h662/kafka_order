package com.example.kafka_order.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class OrderCreatedEvent extends OrderEvent {
    private List<OrderItem> orderItems;
    private BigDecimal totalAmount;

    public OrderCreatedEvent() {
        super();
    }

    @Builder
    public OrderCreatedEvent(String orderId, String customerId,
                             List<OrderItem> orderItems, BigDecimal totalAmount) {
        super();
        setOrderId(orderId);
        setCustomerId(customerId);
        this.orderItems = orderItems;
        this.totalAmount = totalAmount;
    }

    @Override
    public String getEventType() {
        return "ORDER_CREATED";
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderItem {
        private String productId;
        private String productName;
        private int quantity;
        private BigDecimal price;
    }
}
