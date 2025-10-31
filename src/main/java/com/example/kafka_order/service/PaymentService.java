package com.example.kafka_order.service;

import com.example.kafka_order.model.PaymentResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Service
@Slf4j
public class PaymentService {
    public PaymentResult processPayment(String customerId, BigDecimal amount) {
        log.info("결제 처리 시작: customerId={}, amount={}", customerId, amount);

        try {
            Thread.sleep(2000);

            boolean success = ThreadLocalRandom.current().nextDouble() > 0.2;

            if (success) {
                String paymentId = "PAY_" + UUID.randomUUID().toString().substring(0, 8);
                log.info("결제 성공: customerId={}, paymentId={}", customerId, paymentId);
                return PaymentResult.success(paymentId);
            } else {
                String reason = "카드 한도 초과";
                log.error("결제 실패: customerId={}, reason={}", customerId, reason);
                return PaymentResult.failure(reason);
            }
        } catch(InterruptedException e) {
            log.error("결제 처리 중 인터럽트 발생", e);
            return PaymentResult.failure("결제 처리 중 오류 발생");
        }
    }
}
