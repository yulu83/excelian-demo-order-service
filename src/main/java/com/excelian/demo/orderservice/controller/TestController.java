package com.excelian.demo.orderservice.controller;

import com.excelian.demo.common.data.Order;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.time.Duration;

@RestController
public class TestController {

    @GetMapping(value = "/test", produces = MediaType.APPLICATION_STREAM_JSON_VALUE)
    Flux<Order> getOrders() {
        return Flux.interval(Duration.ofSeconds(2))
                .map(i -> {
                    return new Order("", i.intValue(),
                            Math.random());
                });
    }

}
