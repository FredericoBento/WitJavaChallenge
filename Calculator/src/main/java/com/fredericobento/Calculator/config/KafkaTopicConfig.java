package com.fredericobento.Calculator.config;

import com.fredericobento.Calculator.service.CalculatorService;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaTopicConfig {

    private final KafkaProperties kafkaProperties;

    public KafkaTopicConfig(KafkaProperties kafkaProperties) {
        this.kafkaProperties = kafkaProperties;
    }

    @Bean
    public NewTopic requestTopic() {
        return new NewTopic("calc-requests", 1, (short) 1);
    }

    @Bean
    public NewTopic responseTopic() { return new NewTopic("calc-responses", 1, (short) 1); }

    @Bean
    public ConsumerFactory<String, String> consumerFactory() {
       Map<String, Object> config = new HashMap<>(kafkaProperties.buildConsumerProperties());
       return new DefaultKafkaConsumerFactory<>(config);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String,String> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());

        return factory;
    }

}
