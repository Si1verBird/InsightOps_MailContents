package com.example.InsightOps_mailcontents.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AdminApiResponse {
    
    @JsonProperty("success")
    private boolean success;
    
    @JsonProperty("message")
    private String message;
    
    @JsonProperty("data")
    private AdminApiData data;
    
    @JsonProperty("timestamp")
    private String timestamp;

    // 기본 생성자
    public AdminApiResponse() {}

    // Getter/Setter
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public AdminApiData getData() {
        return data;
    }

    public void setData(AdminApiData data) {
        this.data = data;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
