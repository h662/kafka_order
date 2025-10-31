package com.example.kafka_order.service;

import com.example.kafka_order.event.OrderEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private static final String TOPIC = "order-events";

    public void publishOrderEvent(OrderEvent event) {
        String key = event.getCustomerId();

        log.info("주문 이벤트 발행: type={}, orderId={}, customerId={}",
                event.getEventType(), event.getOrderId(), event.getCustomerId());

        CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(TOPIC, key, event);

        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("이벤트 발행 성공: topic={}, partition={}, offset={}",
                        result.getRecordMetadata().topic(),
                        result.getRecordMetadata().partition(),
                        result.getRecordMetadata().offset());
            } else {
                log.error("이벤트 발행 실패: orderId={}", event.getOrderId());
            }
        });
    }
}
