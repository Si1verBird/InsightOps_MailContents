package com.example.InsightOps_mailcontents.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class VocAnalysisResponse {
    
    @JsonProperty("analysis")
    private Analysis analysis;
    
    @JsonProperty("classification")
    private Classification classification;

    // 기본 생성자
    public VocAnalysisResponse() {}

    // Getter
    public Analysis getAnalysis() {
        return analysis;
    }

    public Classification getClassification() {
        return classification;
    }

    // Inner class for analysis
    public static class Analysis {
        @JsonProperty("expected_outcome")
        private String expectedOutcome;
        
        @JsonProperty("problem_situation")
        private String problemSituation;
        
        @JsonProperty("solution_approach")
        private String solutionApproach;

        // 기본 생성자
        public Analysis() {}

        // Getter
        public String getExpectedOutcome() {
            return expectedOutcome;
        }

        public String getProblemSituation() {
            return problemSituation;
        }

        public String getSolutionApproach() {
            return solutionApproach;
        }
    }

    // Inner class for classification
    public static class Classification {
        @JsonProperty("category")
        private String category;
        
        @JsonProperty("confidence")
        private double confidence;
        
        @JsonProperty("category_id")
        private String categoryId;

        // 기본 생성자
        public Classification() {}

        // Getter
        public String getCategory() {
            return category;
        }

        public double getConfidence() {
            return confidence;
        }

        public String getCategoryId() {
            return categoryId;
        }
    }
}