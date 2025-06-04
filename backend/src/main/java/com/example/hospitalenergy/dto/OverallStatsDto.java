package com.example.hospitalenergy.dto;

public class OverallStatsDto {
    private Double totalEnergyConsumed;
    private Double averageEnergyConsumedPerRecord;
    private String unit;
    private Long numberOfRecords;

    // Constructors, Getters, Setters
    public OverallStatsDto() {}

    public OverallStatsDto(Double totalEnergyConsumed, Double averageEnergyConsumedPerRecord, String unit, Long numberOfRecords) {
        this.totalEnergyConsumed = totalEnergyConsumed;
        this.averageEnergyConsumedPerRecord = averageEnergyConsumedPerRecord;
        this.unit = unit;
        this.numberOfRecords = numberOfRecords;
    }
    // Getters and Setters
    public Double getTotalEnergyConsumed() { return totalEnergyConsumed; }
    public void setTotalEnergyConsumed(Double totalEnergyConsumed) { this.totalEnergyConsumed = totalEnergyConsumed; }
    public Double getAverageEnergyConsumedPerRecord() { return averageEnergyConsumedPerRecord; }
    public void setAverageEnergyConsumedPerRecord(Double averageEnergyConsumedPerRecord) { this.averageEnergyConsumedPerRecord = averageEnergyConsumedPerRecord; }
    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }
    public Long getNumberOfRecords() { return numberOfRecords; }
    public void setNumberOfRecords(Long numberOfRecords) { this.numberOfRecords = numberOfRecords; }
} 