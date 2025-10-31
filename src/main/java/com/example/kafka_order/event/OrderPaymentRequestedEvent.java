package com.example.kafka_order.event;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
public class OrderPaymentRequestedEvent extends OrderEvent {
    private BigDecimal amount;
    private String paymentMethod;

    public OrderPaymentRequestedEvent() {
        super();
    }

    @Builder
    public OrderPaymentRequestedEvent(String orderId, String customerId,
                                      BigDecimal amount, String paymentMethod) {
        super();
        setOrderId(orderId);
        setCustomerId(customerId);
        this.amount = amount;
        this.paymentMethod = paymentMethod;
    }

    @Override
    public String getEventType() {
        return "ORDER_PAYMENT_REQUESTED";
    }
}

