package com.sromip.investment.config;

import com.sromip.common.event.InvestmentCompletedEvent;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaProducerConfig {

    private Map<String, Object> baseConfig() {

        Map<String, Object> props = new HashMap<>();

        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        props.put(JsonSerializer.ADD_TYPE_INFO_HEADERS, true);

        return props;
    }

    @Bean
    public ProducerFactory<String, InvestmentCompletedEvent> producerFactory() {
        return new DefaultKafkaProducerFactory<>(baseConfig());
    }

    @Bean
    public KafkaTemplate<String, InvestmentCompletedEvent> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

    // 🔥 Topic Auto Creation

    @Bean
    public NewTopic investmentTriggerTopic() {
        return TopicBuilder.name("investment-trigger-topic")
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic investmentCompletedTopic() {
        return TopicBuilder.name("investment-completed-topic")
                .partitions(3)
                .replicas(1)
                .build();
    }
}