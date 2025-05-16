package com.example.hospitalenergy.controller;

import com.example.hospitalenergy.dto.EnergyStatsResponse;
import com.example.hospitalenergy.dto.QueryParametersDto;
import com.example.hospitalenergy.service.EnergyStatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600) // 允许跨域请求
@RestController
@RequestMapping("/api/energy-stats") // API基础路径
public class EnergyStatsController {

    @Autowired
    private EnergyStatsService energyStatsService;

    @GetMapping
    public ResponseEntity<EnergyStatsResponse> getEnergyStatistics(
            @RequestParam(required = false) List<Long> deviceIds,
            @RequestParam(required = false) String energyType,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime,
            @RequestParam(required = false, defaultValue = "daily") String timeGranularityForTrend // 默认为 daily
    ) {
        QueryParametersDto params = new QueryParametersDto(
                deviceIds,
                energyType,
                startTime,
                endTime,
                timeGranularityForTrend
        );

        EnergyStatsResponse response = energyStatsService.getEnergyStatistics(params);
        return ResponseEntity.ok(response);
    }
} 