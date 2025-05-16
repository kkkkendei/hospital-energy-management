package com.example.hospitalenergy.service.impl;

import com.example.hospitalenergy.dto.*;
import com.example.hospitalenergy.mapper.EnergyDataMapper;
import com.example.hospitalenergy.service.EnergyStatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class EnergyStatsServiceImpl implements EnergyStatsService {

    @Autowired
    private EnergyDataMapper energyDataMapper;

    @Override
    public EnergyStatsResponse getEnergyStatistics(QueryParametersDto params) {
        EnergyStatsResponse response = new EnergyStatsResponse();
        response.setQueryParameters(params);

        // 1. 获取总体统计数据
        Map<String, Object> overallStatsMap = energyDataMapper.queryOverallEnergyStats(
                params.getStartTime(),
                params.getEndTime(),
                params.getDeviceIds(),
                params.getEnergyType()
        );

        if (overallStatsMap != null) {
            OverallStatsDto overallStats = new OverallStatsDto();
            overallStats.setTotalEnergyConsumed(convertToDouble(overallStatsMap.get("total_consumption")));
            overallStats.setAverageEnergyConsumedPerRecord(convertToDouble(overallStatsMap.get("average_consumption")));
            overallStats.setNumberOfRecords(convertToLong(overallStatsMap.get("record_count")));
            overallStats.setUnit((String) overallStatsMap.get("unit"));
            response.setOverallStats(overallStats);
        } else {
            response.setOverallStats(new OverallStatsDto(0.0, 0.0, "", 0L)); // Default if no data
        }

        // 2. 获取按设备细分的统计数据
        List<Map<String, Object>> deviceStatsListMap = energyDataMapper.queryEnergyStatsByDevice(
                params.getStartTime(),
                params.getEndTime(),
                params.getDeviceIds(), // Pass deviceIds here if you want to filter by them for breakdown
                params.getEnergyType()
        );

        List<DeviceEnergySummaryDto> deviceBreakdown = new ArrayList<>();
        if (deviceStatsListMap != null) {
            for (Map<String, Object> deviceMap : deviceStatsListMap) {
                DeviceEnergySummaryDto summaryDto = new DeviceEnergySummaryDto();
                summaryDto.setDeviceId(convertToLong(deviceMap.get("device_id")));
                summaryDto.setDeviceName((String) deviceMap.get("device_name"));
                summaryDto.setTotalEnergyConsumed(convertToDouble(deviceMap.get("total_consumption")));
                summaryDto.setAverageEnergyConsumedPerRecord(convertToDouble(deviceMap.get("average_consumption")));
                summaryDto.setNumberOfRecords(convertToLong(deviceMap.get("record_count")));
                summaryDto.setUnit((String) deviceMap.get("unit"));
                deviceBreakdown.add(summaryDto);
            }
        }
        response.setDeviceBreakdown(deviceBreakdown);

        // 3. 获取能耗趋势数据
        List<Map<String, Object>> trendDataListMap = energyDataMapper.queryEnergyTrendData(
                params.getStartTime(),
                params.getEndTime(),
                params.getDeviceIds(),
                params.getEnergyType(),
                params.getTimeGranularityForTrend()
        );

        EnergyTrendDto energyTrend = new EnergyTrendDto();
        if (trendDataListMap != null && !trendDataListMap.isEmpty()) {
            List<String> timeLabels = trendDataListMap.stream()
                    .map(m -> (String) m.get("time_label"))
                    .collect(Collectors.toList());
            energyTrend.setTimeLabels(timeLabels);

            List<Double> consumptionData = trendDataListMap.stream()
                    .map(m -> convertToDouble(m.get("total_consumption")))
                    .collect(Collectors.toList());
            
            String trendUnit = (String) trendDataListMap.get(0).get("unit"); // Assume unit is consistent

            TrendDatasetDto dataset = new TrendDatasetDto("能耗趋势", consumptionData, trendUnit);
            List<TrendDatasetDto> datasets = new ArrayList<>();
            datasets.add(dataset);
            energyTrend.setDatasets(datasets);
        } else {
            energyTrend.setTimeLabels(new ArrayList<>());
            energyTrend.setDatasets(new ArrayList<>());
        }
        response.setEnergyTrend(energyTrend);

        return response;
    }

    // Helper methods for safe type conversion
    private Double convertToDouble(Object obj) {
        if (obj == null) return 0.0; // Return 0.0 if null to avoid NPE in DTO setters expecting primitives or if default needed
        if (obj instanceof BigDecimal) return ((BigDecimal) obj).doubleValue();
        if (obj instanceof Number) return ((Number) obj).doubleValue();
        try {
            return Double.parseDouble(obj.toString());
        } catch (NumberFormatException e) {
            return 0.0; // Default value or consider throwing an exception
        }
    }

    private Long convertToLong(Object obj) {
        if (obj == null) return 0L; // Return 0L if null
        if (obj instanceof BigInteger) return ((BigInteger) obj).longValue();
        if (obj instanceof Number) return ((Number) obj).longValue();
        try {
            return Long.parseLong(obj.toString());
        } catch (NumberFormatException e) {
            return 0L; // Default value or consider throwing an exception
        }
    }
} 