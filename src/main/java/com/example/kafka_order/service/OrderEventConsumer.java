package com.example.kafka_order.service;

import com.example.kafka_order.event.OrderCompletedEvent;
import com.example.kafka_order.event.OrderCreatedEvent;
import com.example.kafka_order.event.OrderEvent;
import com.example.kafka_order.event.OrderPaymentRequestedEvent;
import com.example.kafka_order.model.PaymentResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderEventConsumer {
    private final OrderService orderService;
    private final PaymentService paymentService;
    private final OrderEventProducer orderEventProducer;

    @KafkaListener(topics = "order-events", groupId = "order-processing-group")
    public void handleOrderEvent(@Payload OrderEvent event,
                                 @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                                 @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
                                 @Header(KafkaHeaders.OFFSET) long offset) {
        log.info("이벤트 수신: type={}, topic={}, partition={}, offset={}, orderId={}",
                event.getEventType(), topic, partition, offset, event.getOrderId());

        try {
            switch (event.getEventType()) {
                case "ORDER_CREATED":
                    handleOrderCreated((OrderCreatedEvent) event);
                    break;
                case "ORDER_PAYMENT_REQUESTED":
                    handlePaymentRequested((OrderPaymentRequestedEvent) event);
                    break;
                case "ORDER_COMPLETED":
                    handleOrderCompleted((OrderCompletedEvent) event);
                    break;
                default:
                    log.warn("알 수 없는 이벤트 타입: {}", event.getEventType());
            }
        } catch(Exception e) {
            log.error("이벤트 처리 실패: orderId={}", event.getOrderId(), e);
            throw e;
        }
    }

    private void handleOrderCreated(OrderCreatedEvent event) {
        log.info("주문 생성 이벤트 처리: orderId={}", event.getOrderId());

        orderService.updateOrderStatus(event.getOrderId(), "PROCESSING");

        boolean stockAvailable = orderService.checkStock(event.getOrderItems());

        if (stockAvailable) {
            OrderPaymentRequestedEvent paymentEvent = OrderPaymentRequestedEvent.builder()
                    .orderId(event.getOrderId())
                    .customerId(event.getCustomerId())
                    .amount(event.getTotalAmount())
                    .paymentMethod("CARD")
                    .build();

            orderEventProducer.publishOrderEvent(paymentEvent);
        } else {
            log.warn("재고 부족으로 주문 취소: orderId={}", event.getOrderId());
            orderService.cancelOrder(event.getOrderId(), "재고 부족");
        }
    }

    private void handlePaymentRequested(OrderPaymentRequestedEvent event) {
        log.info("결제 요청 이벤트 처리: orderId={}, amount={}", event.getOrderId(), event.getAmount());

        orderService.updateOrderStatus(event.getOrderId(), "PAYMENT_PROCESSING");

        PaymentResult result = paymentService.processPayment(
                event.getCustomerId(),
                event.getAmount()
        );

        if (result.isSuccess()) {
            OrderCompletedEvent completedEvent = OrderCompletedEvent.builder()
                    .orderId(event.getOrderId())
                    .customerId(event.getCustomerId())
                    .paymentId(result.getPaymentId())
                    .status("COMPLETED")
                    .build();

            orderEventProducer.publishOrderEvent(completedEvent);
        } else {
            log.error("결제 실패: orderId={}, reason={}",
                    event.getOrderId(), result.getFailureReason());
            orderService.cancelOrder(event.getOrderId(), "결제 실패: " + result.getFailureReason());
        }
    }

    private void handleOrderCompleted(OrderCompletedEvent event) {
        log.info("주문 완료 이벤트 처리: orderId={}, paymentId={}", event.getOrderId(), event.getPaymentId());

        orderService.completeOrder(event.getOrderId(), event.getPaymentId());

        log.info("주문 처리 완료: orderId={}", event.getOrderId());
    }
}
