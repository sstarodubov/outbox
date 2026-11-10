package com.example.demo;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.boot.kafka.autoconfigure.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.Map;
import java.util.UUID;

@Configuration
public class KafkaConfig {

    @Bean
    public ProducerFactory<String, byte[]> producerFactory(
            KafkaProperties kafkaProperties) {

        final Map<String, Object> props = kafkaProperties.buildProducerProperties();
        props.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);
        props.put(ProducerConfig.ACKS_CONFIG, "all");

        final DefaultKafkaProducerFactory<String, byte[]> factory = new DefaultKafkaProducerFactory<>(props);
        factory.setTransactionIdPrefix( "outbox-" + UUID.randomUUID() + "-" ); //должен быть уникальный во всем кластере
        return factory;
    }

    @Bean
    public KafkaTemplate<String, byte[]> outboxKafkaTemplate(
            ProducerFactory<String, byte[]> producerFactory) {

        return new KafkaTemplate<>(producerFactory);
    }
}
