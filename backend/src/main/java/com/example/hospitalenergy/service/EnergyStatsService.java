package com.example.hospitalenergy.service;

import com.example.hospitalenergy.dto.EnergyStatsResponse;
import com.example.hospitalenergy.dto.QueryParametersDto;

public interface EnergyStatsService {
    EnergyStatsResponse getEnergyStatistics(QueryParametersDto params);
} 