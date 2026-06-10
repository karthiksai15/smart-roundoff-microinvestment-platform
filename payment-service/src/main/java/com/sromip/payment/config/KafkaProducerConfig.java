package com.sromip.payment.config;

import com.sromip.common.event.PaymentEvent;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.*;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaProducerConfig {

    private Map<String, Object> config() {

        Map<String, Object> props = new HashMap<>();

        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        props.put(JsonSerializer.ADD_TYPE_INFO_HEADERS, true);

        props.put(ProducerConfig.ACKS_CONFIG, "all");
        props.put(ProducerConfig.RETRIES_CONFIG, 3);

        return props;
    }

    // ✅ OBJECT TEMPLATE
    @Bean(name = "kafkaTemplate")
    public KafkaTemplate<String, Object> kafkaTemplate() {
        return new KafkaTemplate<>(new DefaultKafkaProducerFactory<>(config()));
    }

    // ✅ PAYMENT TEMPLATE (FIXED)
    @Bean
    public KafkaTemplate<String, PaymentEvent> paymentKafkaTemplate() {
        return new KafkaTemplate<>(new DefaultKafkaProducerFactory<>(config()));
    }

    // 🔥 RETRY TEMPLATE
    @Bean(name = "defaultRetryTopicKafkaTemplate")
    public KafkaTemplate<String, Object> retryKafkaTemplate() {
        return new KafkaTemplate<>(new DefaultKafkaProducerFactory<>(config()));
    }

    // ✅ TOPICS
    @Bean
    public NewTopic trustDecisionTopic() {
        return TopicBuilder.name("trust-decision-topic")
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic otpRequestTopic() {
        return TopicBuilder.name("otp-request-topic")
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic paymentTopic() {
        return TopicBuilder.name("payment-topic")
                .partitions(3)
                .replicas(1)
                .build();
    }
    @Bean
    public NewTopic paymentDecisionRetryTopic() {
        return TopicBuilder.name("payment-decision-retry-topic")
                .partitions(3)
                .replicas(1)
                .build();
    }
}