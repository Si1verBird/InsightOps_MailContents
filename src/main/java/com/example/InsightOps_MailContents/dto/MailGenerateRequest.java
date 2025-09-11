package com.example.InsightOps_MailContents.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MailGenerateRequest {
    
    @JsonProperty("category_id")
    private String categoryId;

    // 기본 생성자
    public MailGenerateRequest() {}

    // 생성자
    public MailGenerateRequest(String categoryId) {
        this.categoryId = categoryId;
    }

    // Getter/Setter
    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }
}
