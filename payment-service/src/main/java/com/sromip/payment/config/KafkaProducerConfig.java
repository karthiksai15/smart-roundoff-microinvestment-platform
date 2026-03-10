package com.sromip.payment.config;

import com.sromip.common.event.PaymentEvent;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.kafka.config.TopicBuilder;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaProducerConfig {

    private Map<String, Object> baseConfig() {

        Map<String, Object> config = new HashMap<>();

        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        config.put(JsonSerializer.ADD_TYPE_INFO_HEADERS, true);

        return config;
    }

    @Bean
    public KafkaTemplate<String, PaymentEvent> paymentKafkaTemplate() {

        ProducerFactory<String, PaymentEvent> factory =
                new DefaultKafkaProducerFactory<>(baseConfig());

        return new KafkaTemplate<>(factory);
    }

    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate() {

        ProducerFactory<String, Object> factory =
                new DefaultKafkaProducerFactory<>(baseConfig());

        return new KafkaTemplate<>(factory);
    }

    @Bean
    public NewTopic paymentTopic() {
        return TopicBuilder.name("payment-topic")
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
}