package com.example.InsightOps_MailContents.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MailSendRequest {
    
    @JsonProperty("category_id")
    private String categoryId;
    
    @JsonProperty("subject")
    private String subject;
    
    @JsonProperty("content")
    private String content;

    // 기본 생성자
    public MailSendRequest() {}

    // 전체 생성자
    public MailSendRequest(String categoryId, String subject, String content) {
        this.categoryId = categoryId;
        this.subject = subject;
        this.content = content;
    }

    // Getter/Setter
    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
