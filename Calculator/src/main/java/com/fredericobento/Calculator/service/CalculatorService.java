package com.fredericobento.Calculator.service;

import com.fredericobento.Calculator.model.CalculationRequest;
import com.fredericobento.Calculator.model.CalculationResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class CalculatorService {

    KafkaTemplate<String, CalculationResponse> kafkaTemplate;


    public CalculatorService(KafkaTemplate<String, CalculationResponse> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @KafkaListener(topics = "calc-requests", containerFactory = "kafkaListenerContainerFactory")
    public void consumeCalculationRequests(ConsumerRecord<String, CalculationRequest> record) {
        CalculationRequest request = record.value();
        log.info("Received Calculation Request: " + request.toString());
        int result = 0;
        switch (request.getOperation()) {
            case SUM:
                result = request.getNum1() + request.getNum2();
                break;
            case SUBTRACTION:
                result = request.getNum1() - request.getNum2();
                break;
            case MULTIPLICATION:
                result = request.getNum1() * request.getNum2();
                break;
            case DIVISION:
                if (request.getNum2() == 0) {
                    log.error("Got division by zero" + request);
                    break;
                }
                result = request.getNum1() / request.getNum2();
                break;
        }

        CalculationResponse response = new CalculationResponse(request.getRequestId(), result);
        log.info("Sending Calculation Response: " + response + " through topic calc-responses...");
        kafkaTemplate.send("calc-responses", response.getResponseId(), response);
        log.info("Sent! " + response.getResponseId());
    }
}
