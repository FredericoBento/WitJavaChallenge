package com.fredericobento.RestAPI.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CalculationResponse {
    private String responseId;
    private String requestId;
    private int result;

    public CalculationResponse(String requestId, int result) {
        this.responseId = UUID.randomUUID().toString();
        this.requestId = requestId;
        this.result = result;
    }

    @Override
    public String toString() {
        return "CalculationResponse{" +
                "responseId='" + responseId+ '\'' +
                "requestId='" + requestId + '\'' +
                ", result=" + result+
                '}';

    }

}
