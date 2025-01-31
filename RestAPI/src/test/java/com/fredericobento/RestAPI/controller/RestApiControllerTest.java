package com.fredericobento.RestAPI.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fredericobento.RestAPI.model.CalculationOperation;
import com.fredericobento.RestAPI.model.CalculationRequest;
import com.fredericobento.RestAPI.model.CalculationResponse;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RestApiControllerTest {

    @Mock
    private KafkaTemplate<String, CalculationRequest> kafkaTemplate;

    private Map<String, CompletableFuture<ResponseEntity<String>>> pendingRequests;

    @InjectMocks
    private RestApiController restApiController;

    @BeforeEach
    void setUp() {
        pendingRequests = new HashMap<>();
        restApiController = new RestApiController(kafkaTemplate, pendingRequests);
    }

    @Test
    @SuppressWarnings("unchecked")
    void testSumEndpoint() {
        CompletableFuture<ResponseEntity<String>> future = restApiController.sum(5, 3);
        assertNotNull(future);
        assertFalse(pendingRequests.isEmpty());

        ArgumentCaptor<CalculationRequest> captor = ArgumentCaptor.forClass(CalculationRequest.class);
        verify(kafkaTemplate).send(eq("calc-requests"), anyString(), captor.capture());

        CalculationRequest sentRequest = captor.getValue();
        assertEquals(CalculationOperation.SUM, sentRequest.getOperation());
        assertEquals(5, sentRequest.getNum1());
        assertEquals(3, sentRequest.getNum2());
    }

    @Test
    @SuppressWarnings("unchecked")
    void testKafkaResponseProcessing() throws JsonProcessingException {
        String requestId = "test-request-id";
        CalculationResponse response = new CalculationResponse(requestId, 8);
        CompletableFuture<ResponseEntity<String>> future = new CompletableFuture<>();
        pendingRequests.put(requestId, future);

        ConsumerRecord<String, CalculationResponse> record = mock(ConsumerRecord.class);
        when(record.value()).thenReturn(response);

        restApiController.consumeCalculationRequests(record);

        assertTrue(future.isDone());
        ResponseEntity<String> entity = future.join();
        assertNotNull(entity);

        ObjectMapper objectMapper = new ObjectMapper();
        String expectedJson = objectMapper.writeValueAsString(Map.of("result", 8));
        assertEquals(expectedJson, entity.getBody());
    }
}
