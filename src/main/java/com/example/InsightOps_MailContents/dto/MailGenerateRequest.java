package com.example.InsightOps_mailcontents.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MailGenerateRequest {
    
    @JsonProperty("consulting_category")
    private String consultingCategory;

    // 기본 생성자
    public MailGenerateRequest() {}

    // 생성자
    public MailGenerateRequest(String consultingCategory) {
        this.consultingCategory = consultingCategory;
    }

    // Getter/Setter
    public String getConsultingCategory() {
        return consultingCategory;
    }

    // public void setConsultingCategory(String consultingCategory) {
    //     this.consultingCategory = consultingCategory;
    // } 
}
