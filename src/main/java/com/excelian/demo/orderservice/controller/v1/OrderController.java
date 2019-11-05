package com.excelian.demo.orderservice.controller.v1;

import com.excelian.demo.common.data.Order;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api")
@Slf4j
public class OrderController {

    private final KafkaTemplate<String, Order> kafkaTemplate;
    private final String topic;

    public OrderController(KafkaTemplate<String, Order> kafkaTemplate,
                           @Value("${spring.kafka.topic}") String topic) {
        this.kafkaTemplate = kafkaTemplate;
        this.topic = topic;
    }

    @PostMapping("/v1/order")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<ResponseEntity<Order>> createOrder(@RequestBody Order order) {
        log.info("Create Order {}", order);
        // need to publish it into kafka
        ListenableFuture<SendResult<String, Order>> listenableFuture = kafkaTemplate.send (topic, order);
        listenableFuture.addCallback(
                sr -> log.info("Sent order=[" + order + "] with offset=[" + sr.getRecordMetadata().offset() + "]"),
                ex -> log.error("Unable to send order=[" + order + "] due to: " + ex.getMessage())
        );
        return Mono.just(order).map(o -> ResponseEntity.status(HttpStatus.OK).body(order));
    }
}
