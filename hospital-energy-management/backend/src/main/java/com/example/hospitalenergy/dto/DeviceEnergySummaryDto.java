package com.example.hospitalenergy.dto;

public class DeviceEnergySummaryDto {
    private Long deviceId;
    private String deviceName; // 假设设备有名称，方便前端展示
    private Double totalEnergyConsumed;
    private Double averageEnergyConsumedPerRecord;
    private Long numberOfRecords;
    private String unit; // 能源单位，例如 kWh, m³

    // Constructors
    public DeviceEnergySummaryDto() {
    }

    public DeviceEnergySummaryDto(Long deviceId, String deviceName, Double totalEnergyConsumed, Double averageEnergyConsumedPerRecord, Long numberOfRecords, String unit) {
        this.deviceId = deviceId;
        this.deviceName = deviceName;
        this.totalEnergyConsumed = totalEnergyConsumed;
        this.averageEnergyConsumedPerRecord = averageEnergyConsumedPerRecord;
        this.numberOfRecords = numberOfRecords;
        this.unit = unit;
    }

    // Getters and Setters
    public Long getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(Long deviceId) {
        this.deviceId = deviceId;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public Double getTotalEnergyConsumed() {
        return totalEnergyConsumed;
    }

    public void setTotalEnergyConsumed(Double totalEnergyConsumed) {
        this.totalEnergyConsumed = totalEnergyConsumed;
    }

    public Double getAverageEnergyConsumedPerRecord() {
        return averageEnergyConsumedPerRecord;
    }

    public void setAverageEnergyConsumedPerRecord(Double averageEnergyConsumedPerRecord) {
        this.averageEnergyConsumedPerRecord = averageEnergyConsumedPerRecord;
    }

    public Long getNumberOfRecords() {
        return numberOfRecords;
    }

    public void setNumberOfRecords(Long numberOfRecords) {
        this.numberOfRecords = numberOfRecords;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }
} 