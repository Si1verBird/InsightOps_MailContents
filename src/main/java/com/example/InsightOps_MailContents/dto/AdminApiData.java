package com.example.InsightOps_mailcontents.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class AdminApiData {
    
    @JsonProperty("tableName")
    private String tableName;
    
    @JsonProperty("columns")
    private List<String> columns;
    
    @JsonProperty("data")
    private List<ConsultingCategory> data;
    
    @JsonProperty("totalCount")
    private int totalCount;
    
    @JsonProperty("timestamp")
    private String timestamp;

    // 기본 생성자
    public AdminApiData() {}

    // Getter/Setter
    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public List<String> getColumns() {
        return columns;
    }

    public void setColumns(List<String> columns) {
        this.columns = columns;
    }

    public List<ConsultingCategory> getData() {
        return data;
    }

    public void setData(List<ConsultingCategory> data) {
        this.data = data;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
