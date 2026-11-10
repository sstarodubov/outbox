package com.example.demo;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class TestConsumer1 {
    Set<String> storage = Collections.synchronizedSet(new HashSet<>());
    AtomicInteger countDup = new AtomicInteger();
    AtomicInteger all = new AtomicInteger();

    @KafkaListener(
            topics = "outbox-topic-6",
            groupId = "test",
            properties = "isolation.level=read_committed"
    )
    public void consume(byte[] bytes) {
        final var s = new String(bytes);
        if (storage.contains(s)) {
            countDup.incrementAndGet();
            System.out.println("oops duplication: " + countDup.get());
        }
        storage.add(s);
        System.out.println(all.incrementAndGet());
    }
}
