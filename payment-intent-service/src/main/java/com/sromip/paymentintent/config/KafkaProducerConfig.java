package com.sromip.paymentintent.config;

import com.sromip.common.event.PaymentIntentCreatedEvent;
import com.sromip.common.event.FraudDecisionEvent;
import com.sromip.common.event.FraudCheckRequest;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.kafka.core.*;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaProducerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String kafkaServer;

    private Map<String, Object> commonConfig() {

        Map<String, Object> config = new HashMap<>();

        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaServer);
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        config.put(ProducerConfig.ACKS_CONFIG, "all");
        config.put(ProducerConfig.RETRIES_CONFIG, 3);
        config.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);

        return config;
    }

    @Bean
    public KafkaTemplate<String, FraudDecisionEvent> fraudDecisionKafkaTemplate() {
        return new KafkaTemplate<>(new DefaultKafkaProducerFactory<>(commonConfig()));
    }

    @Bean
    public KafkaTemplate<String, FraudCheckRequest> fraudRetryKafkaTemplate() {
        return new KafkaTemplate<>(new DefaultKafkaProducerFactory<>(commonConfig()));
    }

    @Bean
    public KafkaTemplate<String, PaymentIntentCreatedEvent> paymentIntentKafkaTemplate() {
        return new KafkaTemplate<>(new DefaultKafkaProducerFactory<>(commonConfig()));
    }
}