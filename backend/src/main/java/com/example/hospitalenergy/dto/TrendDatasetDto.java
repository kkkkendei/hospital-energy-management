package com.example.hospitalenergy.dto;

import java.util.List;

public class TrendDatasetDto {
    private String label; // 数据系列的名称，例如 "设备A电耗" 或 "总水耗"
    private List<Double> data; // 对应 timeLabels 的数据点
    private String unit; // 该数据系列的单位

    // Constructors
    public TrendDatasetDto() {
    }

    public TrendDatasetDto(String label, List<Double> data, String unit) {
        this.label = label;
        this.data = data;
        this.unit = unit;
    }

    // Getters and Setters
    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public List<Double> getData() {
        return data;
    }

    public void setData(List<Double> data) {
        this.data = data;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }
} 