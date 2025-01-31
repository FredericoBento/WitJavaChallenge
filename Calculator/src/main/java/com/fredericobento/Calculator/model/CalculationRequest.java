package com.fredericobento.Calculator.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CalculationRequest {
    private String requestId;
    private CalculationOperation operation;
    private int num1;
    private int num2;

    public CalculationRequest(CalculationOperation operation, int num1, int num2) {
        this.requestId = UUID.randomUUID().toString();
        this.operation = operation;
        this.num1 = num1;
        this.num2 = num2;
    }

    @Override
    public String toString() {
        return "CalculationRequest{" +
                "requestId='" + requestId + '\'' +
                ", operation=" + operation +
                ", num1=" + num1 +
                ", num2=" + num2 +
                '}';

    }
}

