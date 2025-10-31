package com.example.kafka_order.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PaymentResult {
    private boolean success;
    private String paymentId;
    private String failureReason;

    public static PaymentResult success(String paymentId) {
        return PaymentResult.builder()
                .success(true)
                .paymentId(paymentId)
                .build();
    }

    public static PaymentResult failure(String reason) {
        return PaymentResult.builder()
                .success(false)
                .failureReason(reason)
                .build();
    }
}
