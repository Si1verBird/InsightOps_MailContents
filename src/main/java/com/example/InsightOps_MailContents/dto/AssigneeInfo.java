package com.example.InsightOps_MailContents.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AssigneeInfo {
    
    @JsonProperty("assignee_id")
    private String assigneeId;
    
    @JsonProperty("assignee_email")
    private String assigneeEmail;
    
    @JsonProperty("assignee_name")
    private String assigneeName;
    
    @JsonProperty("assignee_team")
    private String assigneeTeam;
    
    @JsonProperty("assignee_phone")
    private String assigneePhone;
    
    @JsonProperty("created_at")
    private String createdAt;
    
    @JsonProperty("updated_at")
    private String updatedAt;

    // 기본 생성자
    public AssigneeInfo() {}

    // Getter/Setter
    public String getAssigneeId() {
        return assigneeId;
    }

    public void setAssigneeId(String assigneeId) {
        this.assigneeId = assigneeId;
    }

    public String getAssigneeEmail() {
        return assigneeEmail;
    }

    public void setAssigneeEmail(String assigneeEmail) {
        this.assigneeEmail = assigneeEmail;
    }

    public String getAssigneeName() {
        return assigneeName;
    }

    public void setAssigneeName(String assigneeName) {
        this.assigneeName = assigneeName;
    }

    public String getAssigneeTeam() {
        return assigneeTeam;
    }

    public void setAssigneeTeam(String assigneeTeam) {
        this.assigneeTeam = assigneeTeam;
    }

    public String getAssigneePhone() {
        return assigneePhone;
    }

    public void setAssigneePhone(String assigneePhone) {
        this.assigneePhone = assigneePhone;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }
}
