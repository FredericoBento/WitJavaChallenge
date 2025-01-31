package com.fredericobento.RestAPI.config;

import com.fredericobento.RestAPI.model.CalculationRequest;
import com.fredericobento.RestAPI.model.CalculationResponse;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.support.converter.StringJsonMessageConverter;
import org.springframework.kafka.support.serializer.JsonSerializer;

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
    public ConsumerFactory<String, CalculationResponse> consumerFactory() {
        Map<String, Object> props = new HashMap<>(kafkaProperties.buildConsumerProperties());
        return new DefaultKafkaConsumerFactory<>(props);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, CalculationResponse> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String,CalculationResponse> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());

        return factory;
    }


    @Bean
    public ProducerFactory<String , CalculationRequest> producerFactory() {
        Map<String, Object> props = new HashMap<>(kafkaProperties.buildProducerProperties());
        /* props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        props.put(JsonSerializer.TYPE_MAPPINGS, "CalculationRequest:com.fredericobento.RestAPI.model.CalculationRequest");
         */
        return new DefaultKafkaProducerFactory<>(props);
    }

    @Bean KafkaTemplate<String, CalculationRequest> kafkaTemplate() {
        KafkaTemplate<String, CalculationRequest> template = new KafkaTemplate<>(producerFactory());
        template.setMessageConverter(new StringJsonMessageConverter());
        return template;
    }


}
