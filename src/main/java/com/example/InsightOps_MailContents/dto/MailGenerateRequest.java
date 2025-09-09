package com.example.InsightOps_mailcontents.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MailGenerateRequest {
    
    @JsonProperty("category_small")
    private String categorySmall;

    // 기본 생성자
    public MailGenerateRequest() {}

    // 생성자
    public MailGenerateRequest(String categorySmall) {
        this.categorySmall = categorySmall;
    }

    // Getter/Setter
    public String getCategorySmall() {
        return categorySmall;
    }

    // public void setCategorySmall(String categorySmall) {
    //     this.categorySmall = categorySmall;
    // } 
}
