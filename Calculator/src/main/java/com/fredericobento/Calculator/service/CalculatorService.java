package com.fredericobento.Calculator.service;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class CalculatorService {

    @KafkaListener(topics = "calc-requests")
    public void listen(String message) {
       System.out.println("Got MsG: " + message);
    }
}
