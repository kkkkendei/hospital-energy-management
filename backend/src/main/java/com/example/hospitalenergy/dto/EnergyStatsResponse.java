package com.example.hospitalenergy.dto;

import java.util.List;

public class EnergyStatsResponse {
    private QueryParametersDto queryParameters;
    private OverallStatsDto overallStats;
    private List<DeviceEnergySummaryDto> deviceBreakdown; // 按设备细分的统计
    private EnergyTrendDto energyTrend; // 能耗趋势数据

    // Constructors
    public EnergyStatsResponse() {
    }

    public EnergyStatsResponse(QueryParametersDto queryParameters, OverallStatsDto overallStats, List<DeviceEnergySummaryDto> deviceBreakdown, EnergyTrendDto energyTrend) {
        this.queryParameters = queryParameters;
        this.overallStats = overallStats;
        this.deviceBreakdown = deviceBreakdown;
        this.energyTrend = energyTrend;
    }

    // Getters and Setters
    public QueryParametersDto getQueryParameters() {
        return queryParameters;
    }

    public void setQueryParameters(QueryParametersDto queryParameters) {
        this.queryParameters = queryParameters;
    }

    public OverallStatsDto getOverallStats() {
        return overallStats;
    }

    public void setOverallStats(OverallStatsDto overallStats) {
        this.overallStats = overallStats;
    }

    public List<DeviceEnergySummaryDto> getDeviceBreakdown() {
        return deviceBreakdown;
    }

    public void setDeviceBreakdown(List<DeviceEnergySummaryDto> deviceBreakdown) {
        this.deviceBreakdown = deviceBreakdown;
    }

    public EnergyTrendDto getEnergyTrend() {
        return energyTrend;
    }

    public void setEnergyTrend(EnergyTrendDto energyTrend) {
        this.energyTrend = energyTrend;
    }
} 