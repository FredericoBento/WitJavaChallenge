package com.fredericobento.RestAPI.controller;

import com.fredericobento.RestAPI.model.CalculationOperation;
import com.fredericobento.RestAPI.model.CalculationRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class RestApiController {

    @Autowired
    private final KafkaTemplate<String, String> kafkaTemplate;

    public RestApiController(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @GetMapping("/hello")
    public String hello(@RequestParam String name) {
        return "Hello " + name;
    }

    @GetMapping("/sum")
    public String sum(@RequestParam int a, @RequestParam int b) {
       CalculationRequest request = new CalculationRequest(CalculationOperation.SUM, a, b);
       kafkaTemplate.send("calc-requests", request.getRequestId(), request.toString());
       return request.toString();
    }

    @GetMapping("/subtraction")
    public String subtraction(@RequestParam int a, @RequestParam int b) {
        int result = a - b;
        return "result: " + result;
    }

    @GetMapping("/multiplication")
    public String multiplication(@RequestParam int a, @RequestParam int b) {
        int result = a * b;
        return "result: " + result;
    }

    @GetMapping("/division")
    public String division(@RequestParam int a, @RequestParam int b) {
        int result = a / b;
        return "result: " + result;
    }
}
