package com.example.InsightOps_MailContents.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MailGenerateResponse {
    
    @JsonProperty("subject")
    private String subject;
    
    @JsonProperty("content")
    private String content;

    // 기본 생성자
    public MailGenerateResponse() {}

    // 생성자
    public MailGenerateResponse(String subject, String content) {
        this.subject = subject;
        this.content = content;
    }

    // Getter/Setter
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
