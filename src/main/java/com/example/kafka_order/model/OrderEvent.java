package com.example.kafka_order.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Data;

import java.time.LocalDateTime;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "eventType")
@JsonSubTypes({
        @JsonSubTypes.Type(value = OrderCreatedEvent.class, name = "ORDER_CREATED"),
        @JsonSubTypes.Type(value = OrderPaymentRequestedEvent.class, name = "ORDER_PAYMENT_REQUESTED"),
        @JsonSubTypes.Type(value = OrderCompletedEvent.class, name = "ORDER_COMPLETED")
})
@Data
public abstract class OrderEvent {
    private String orderId;
    private String customerId;
    private LocalDateTime eventTime;

    public OrderEvent() {
        this.eventTime = LocalDateTime.now();
    }

    public abstract String getEventType();
}


