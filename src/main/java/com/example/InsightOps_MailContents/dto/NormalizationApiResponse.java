package com.example.InsightOps_mailcontents.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class NormalizationApiResponse {
    
    @JsonProperty("success")
    private boolean success;
    
    @JsonProperty("data")
    private List<VocAnalysisResponse> data;

    // 기본 생성자
    public NormalizationApiResponse() {}

    // Getter/Setter
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public List<VocAnalysisResponse> getData() {
        return data;
    }

    public void setData(List<VocAnalysisResponse> data) {
        this.data = data;
    }
}
