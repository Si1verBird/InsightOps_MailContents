package com.example.InsightOps_mailcontents.entity;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "voc_normalized")
@JsonIgnoreProperties(ignoreUnknown = true)
public class VocNormalized {

    @Id
    @Column(name = "consulting_category")
    private String consultingCategory;

    @Column(name = "analysis_result", columnDefinition = "TEXT")
    private String analysisResult;

    // 기본 생성자
    public VocNormalized() {}

    // Getter/Setter
    public String getConsultingCategory() {
        return consultingCategory;
    }

    public void setConsultingCategory(String consultingCategory) {
        this.consultingCategory = consultingCategory;
    }

    public String getAnalysisResult() {
        return analysisResult;
    }

    public void setAnalysisResult(String analysisResult) {
        this.analysisResult = analysisResult;
    }
}