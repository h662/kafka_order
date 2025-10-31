package com.example.kafka_order.model;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class OrderCompletedEvent extends OrderEvent {
    private String paymentId;
    private String status;

    public OrderCompletedEvent() {
        super();
    }

    @Builder
    public OrderCompletedEvent(String orderId, String customerId,
                               String paymentId, String status) {
        super();
        setOrderId(orderId);
        setCustomerId(customerId);
        this.paymentId = paymentId;
        this.status = status;
    }

    @Override
    public String getEventType() {
        return "ORDER_COMPLETED";
    }
}
