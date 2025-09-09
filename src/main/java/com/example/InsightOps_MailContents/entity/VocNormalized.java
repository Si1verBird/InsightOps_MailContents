package com.example.InsightOps_mailcontents.entity;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "voc_normalized")
@JsonIgnoreProperties(ignoreUnknown = true)
public class VocNormalized {

    @Id
    @Column(name = "category_small")
    private String categorySmall;

    @Column(name = "analysis_result", columnDefinition = "TEXT")
    private String analysisResult;

    // 기본 생성자
    public VocNormalized() {}

    // Getter/Setter
    public String getCategorySmall() {
        return categorySmall;
    }

    public void setCategorySmall(String categorySmall) {
        this.categorySmall = categorySmall;
    }

    public String getAnalysisResult() {
        return analysisResult;
    }

    public void setAnalysisResult(String analysisResult) {
        this.analysisResult = analysisResult;
    }
}