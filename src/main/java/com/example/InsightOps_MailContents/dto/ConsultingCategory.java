package com.example.InsightOps_MailContents.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ConsultingCategory {
    
    @JsonProperty("category_id")
    private String categoryId;
    
    @JsonProperty("consulting_category")
    private String consultingCategory;
    
    @JsonProperty("description")
    private String description;
    
    @JsonProperty("created_at")
    private String createdAt;
    
    @JsonProperty("updated_at")
    private String updatedAt;

    // 기본 생성자
    public ConsultingCategory() {}

    // Getter/Setter
    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getConsultingCategory() {
        return consultingCategory;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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
