package com.example.hospitalenergy.dto;

import java.time.LocalDateTime;
import java.util.List;

public class QueryParametersDto {
    private List<Long> deviceIds;
    private String energyType;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String timeGranularityForTrend;

    // Constructors, Getters, Setters
    public QueryParametersDto() {}

    public QueryParametersDto(List<Long> deviceIds, String energyType, LocalDateTime startTime, LocalDateTime endTime, String timeGranularityForTrend) {
        this.deviceIds = deviceIds;
        this.energyType = energyType;
        this.startTime = startTime;
        this.endTime = endTime;
        this.timeGranularityForTrend = timeGranularityForTrend;
    }

    // Getters and Setters
    public List<Long> getDeviceIds() { return deviceIds; }
    public void setDeviceIds(List<Long> deviceIds) { this.deviceIds = deviceIds; }
    public String getEnergyType() { return energyType; }
    public void setEnergyType(String energyType) { this.energyType = energyType; }
    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }
    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }
    public String getTimeGranularityForTrend() { return timeGranularityForTrend; }
    public void setTimeGranularityForTrend(String timeGranularityForTrend) { this.timeGranularityForTrend = timeGranularityForTrend; }
} 