package com.example.hospitalenergy.dto;

import java.util.List;

public class EnergyTrendDto {
    private List<String> timeLabels; // 例如 ["2023-10-01", "2023-10-02", ...] 或 ["00:00", "01:00", ...]
    private List<TrendDatasetDto> datasets; // 每个 TrendDatasetDto 代表一条线或一个柱状图系列

    // Constructors
    public EnergyTrendDto() {
    }

    public EnergyTrendDto(List<String> timeLabels, List<TrendDatasetDto> datasets) {
        this.timeLabels = timeLabels;
        this.datasets = datasets;
    }

    // Getters and Setters
    public List<String> getTimeLabels() {
        return timeLabels;
    }

    public void setTimeLabels(List<String> timeLabels) {
        this.timeLabels = timeLabels;
    }

    public List<TrendDatasetDto> getDatasets() {
        return datasets;
    }

    public void setDatasets(List<TrendDatasetDto> datasets) {
        this.datasets = datasets;
    }
} 