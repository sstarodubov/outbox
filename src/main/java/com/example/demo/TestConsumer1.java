package com.example.demo;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class TestConsumer1 {

    @KafkaListener(topics = "save-db", groupId = "test")
    public void consume(byte[] bytes) {
        System.out.println(new String(bytes));
    }
}
