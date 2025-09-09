package com.example.InsightOps_mailcontents.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AnalysisResult {
    
    @JsonProperty("problem_situation")
    private String problemSituation;
    
    @JsonProperty("solution_approach")
    private String solutionApproach;
    
    @JsonProperty("expected_outcome")
    private String expectedOutcome;

    // 기본 생성자
    public AnalysisResult() {}

    // Getter/Setter
    public String getProblemSituation() {
        return problemSituation;
    }

    public void setProblemSituation(String problemSituation) {
        this.problemSituation = problemSituation;
    }

    public String getSolutionApproach() {
        return solutionApproach;
    }

    public void setSolutionApproach(String solutionApproach) {
        this.solutionApproach = solutionApproach;
    }

    public String getExpectedOutcome() {
        return expectedOutcome;
    }

    public void setExpectedOutcome(String expectedOutcome) {
        this.expectedOutcome = expectedOutcome;
    }
}
