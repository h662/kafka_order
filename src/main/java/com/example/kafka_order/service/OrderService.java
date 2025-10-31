package com.example.kafka_order.service;

import com.example.kafka_order.model.PaymentResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Service
@Slf4j
public class OrderService {
    private final Random random = new Random();

    public PaymentResult processPayment(String customerId, BigDecimal amount) {
        log.info("결제 처리 시작: customerId={}, amount={}", customerId, amount);

        try {
            Thread.sleep(5000);

            String paymentId = "PAY_" + UUID.randomUUID().toString().substring(0, 8);
            log.info("결제 성공: customerId={}, paymentId={}", customerId, paymentId);
            return PaymentResult.success(paymentId);
        } catch(InterruptedException e) {
            log.error("결제 처리 중 인터럽트 발생", e);
            return PaymentResult.failure("결제 처리 중 오류 발생");
        }
    }

    public void updateOrderStatus(String orderId, String status) {
        log.info("주문 상태 업데이트: order={}, status={}", orderId, status);

        try {
            Thread.sleep(100);
            log.info("주문 상태 업데이트 완료: orderId={}, newStatus={}", orderId, status);
        } catch(InterruptedException e) {
            log.error("주문 상태 업데이트 중 오류 발생", e);
            Thread.currentThread().interrupt();
        }
    }

    public boolean checkStock(List<?> orderItems) {
        log.info("재고 확인 시작: items={}", orderItems != null ? orderItems.size() : 0);

        try {
            Thread.sleep(200);

            boolean stockAvailable = random.nextDouble() < 0.8;

            log.info("재고 확인 완료: available={}", stockAvailable);
            return stockAvailable;
        } catch (InterruptedException e) {
            log.error("재고 확인 중 오류 발생", e);
            Thread.currentThread().interrupt();
            return false;
        }
    }

    public void cancelOrder(String orderId, String reason) {
        log.info("주문 취소 시작: orderId={}, reason={}", orderId, reason);

        try {
            updateOrderStatus(orderId, "CANCELLED");

            Thread.sleep(150);

            log.info("주문 취소 완료: orderId={}, reason={}", orderId, reason);
        } catch (InterruptedException e) {
            log.error("주문 취소 처리 중 오류 발생", e);
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            log.error("주문 취소 실패: orderId={}", orderId, e);
            throw new RuntimeException("주문 취소 실패", e);
        }
    }

    public void completeOrder(String orderId, String paymentId) {
        log.info("주문 완료 처리 시작: orderId={}, payment={}", orderId, paymentId);

        try {
            updateOrderStatus(orderId, "COMPLETED");

            Thread.sleep(100);

            log.info("주문 완료 처리 성공: orderId={}, payment={}", orderId, paymentId);
        } catch(InterruptedException e) {
            log.error("주문 완료 처리 중 오류 발생", e);
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            log.error("주문 완료 처리 실패: orderId={}", orderId, e);
            throw new RuntimeException("주문 완료 처리 실패", e);
        }
    }
}
