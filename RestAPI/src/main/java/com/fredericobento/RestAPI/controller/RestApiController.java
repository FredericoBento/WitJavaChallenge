package com.fredericobento.RestAPI.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.util.JSONPObject;
import com.fredericobento.RestAPI.model.CalculationOperation;
import com.fredericobento.RestAPI.model.CalculationRequest;
import com.fredericobento.RestAPI.model.CalculationResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Slf4j
@RestController
@RequestMapping("/")
public class RestApiController {

    @Autowired
    private final KafkaTemplate<String, CalculationRequest> kafkaTemplate;

    private final Map<String, CompletableFuture<ResponseEntity<String>>> pendingRequests;

    public RestApiController(KafkaTemplate<String, CalculationRequest> kafkaTemplate, Map<String,  CompletableFuture<ResponseEntity<String>>> pendingResponses) {
        this.kafkaTemplate = kafkaTemplate;
        this.pendingRequests = pendingResponses;
    }

    @GetMapping(value = "/sum", produces = MediaType.APPLICATION_JSON_VALUE)
    public CompletableFuture<ResponseEntity<String>> sum(@RequestParam int a, @RequestParam int b) {
       log.info("Received /sum request with a=" + a + " and b=" + b);
       CalculationRequest request = new CalculationRequest(CalculationOperation.SUM, a, b);
       return makeCalculationRequest(request);
    }

    @GetMapping(value = "/subtraction", produces = MediaType.APPLICATION_JSON_VALUE)
    public CompletableFuture<ResponseEntity<String>> subtraction(@RequestParam int a, @RequestParam int b) {
        log.info("Received /subtraction request with a=" + a + " and b=" + b);
        CalculationRequest request = new CalculationRequest(CalculationOperation.SUBTRACTION, a, b);
        return makeCalculationRequest(request);
    }

    @GetMapping(value = "/multiplication", produces = MediaType.APPLICATION_JSON_VALUE)
    public CompletableFuture<ResponseEntity<String>> multiplication(@RequestParam int a, @RequestParam int b) {
        log.info("Received /multiplication request with a=" + a + " and b=" + b);
        CalculationRequest request = new CalculationRequest(CalculationOperation.MULTIPLICATION, a, b);
        return makeCalculationRequest(request);
    }

    @GetMapping(value = "/division", produces = MediaType.APPLICATION_JSON_VALUE)
    public CompletableFuture<ResponseEntity<String>> division(@RequestParam int a, @RequestParam int b) {
        log.info("Received /division request with a=" + a + " and b=" + b);
        CalculationRequest request = new CalculationRequest(CalculationOperation.DIVISION, a, b);
        return makeCalculationRequest(request);
    }


    private CompletableFuture<ResponseEntity<String>> makeCalculationRequest(CalculationRequest request) {
        CompletableFuture<ResponseEntity<String>> futureResponse = new CompletableFuture<>();
        pendingRequests.put(request.getRequestId(), futureResponse);
        log.info("Sending Calculation Request: " + request + " through topic calc-requests...");
        kafkaTemplate.send("calc-requests", request.getRequestId(), request);
        log.info("Sent! " + request.getRequestId());
        return futureResponse;
    }

    @KafkaListener(topics = "calc-responses", containerFactory = "kafkaListenerContainerFactory")
    public void consumeCalculationRequests(ConsumerRecord<String, CalculationResponse> record) {
        CalculationResponse response = record.value();
        log.info("Received Calculation Response: " + response);
        CompletableFuture<ResponseEntity<String>> future = pendingRequests.get(response.getRequestId());

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("X-Request-ID", response.getRequestId());

        // To avoid sending response.requestId and response.responseId on json body
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Integer> jsonMap = new HashMap<>();
        jsonMap.put("result", response.getResult());

        try {
            String jsonResult = objectMapper.writeValueAsString(jsonMap);
            ResponseEntity<String> responseResponseEntity = ResponseEntity.ok()
                    .headers(responseHeaders)
                    .body(jsonResult);

            if (future != null) {
                future.complete(responseResponseEntity);
                pendingRequests.remove(response.getRequestId());
                log.info("Successfuly resolved pending request: " + response.getRequestId());
            }

        }catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }

    }

}
