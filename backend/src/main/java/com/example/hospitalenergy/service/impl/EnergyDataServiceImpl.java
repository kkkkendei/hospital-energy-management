package com.example.hospitalenergy.service.impl;

import com.example.hospitalenergy.entity.Device;
import com.example.hospitalenergy.entity.EnergyData;
import com.example.hospitalenergy.mapper.EnergyDataMapper;
import com.example.hospitalenergy.service.DeviceService; // 使用 DeviceService 进行设备校验
import com.example.hospitalenergy.service.EnergyDataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Service
public class EnergyDataServiceImpl implements EnergyDataService {

    private static final Logger logger = LoggerFactory.getLogger(EnergyDataServiceImpl.class);

    @Autowired
    private EnergyDataMapper energyDataMapper;

    @Autowired
    private DeviceService deviceService; // 用于校验 deviceId 是否有效

    @Override
    @Transactional
    public EnergyData addEnergyData(EnergyData energyData) {
        if (energyData == null) {
            throw new IllegalArgumentException("EnergyData cannot be null.");
        }
        if (energyData.getDeviceId() == null) {
            throw new IllegalArgumentException("Device ID cannot be null in EnergyData.");
        }
        // 校验关联的设备是否存在
        Device device = deviceService.findDeviceById(energyData.getDeviceId());
        if (device == null) {
            logger.warn("Attempted to add energy data for non-existent device ID: {}", energyData.getDeviceId());
            throw new IllegalArgumentException("Device with ID " + energyData.getDeviceId() + " not found.");
        }

        // 可以在此设置默认值或进一步校验，例如 recordTime, energyValue, energyUnit
        if (energyData.getRecordTime() == null) {
            energyData.setRecordTime(LocalDateTime.now()); // 如果未提供，默认为当前时间
        }
        if (energyData.getEnergyValue() == null || energyData.getEnergyValue() < 0) {
            throw new IllegalArgumentException("Energy value must be a non-negative number.");
        }
        if (energyData.getEnergyUnit() == null || energyData.getEnergyUnit().trim().isEmpty()) {
            throw new IllegalArgumentException("Energy unit cannot be empty.");
        }

        energyDataMapper.insertEnergyData(energyData);
        logger.info("Service: Energy data added successfully with ID: {}", energyData.getId());
        return energyData;
    }

    @Override
    public EnergyData getEnergyDataById(Long id) {
        logger.info("Service: Fetching energy data by ID: {}", id);
        return energyDataMapper.findEnergyDataById(id);
    }

    @Override
    public List<EnergyData> getEnergyDataByDeviceId(Long deviceId) {
        if (deviceId == null) {
            return Collections.emptyList();
        }
        logger.info("Service: Fetching energy data for device ID: {}", deviceId);
        return energyDataMapper.findEnergyDataByDeviceId(deviceId);
    }

    @Override
    public List<EnergyData> getEnergyDataByTimeRange(LocalDateTime startTime, LocalDateTime endTime) {
        if (startTime == null || endTime == null || startTime.isAfter(endTime)) {
            throw new IllegalArgumentException("Invalid time range provided.");
        }
        logger.info("Service: Fetching energy data between {} and {}", startTime, endTime);
        return energyDataMapper.findEnergyDataByTimeRange(startTime, endTime);
    }

    @Override
    public List<EnergyData> getEnergyDataByDeviceAndTimeRange(Long deviceId, LocalDateTime startTime, LocalDateTime endTime) {
        if (deviceId == null) {
             throw new IllegalArgumentException("Device ID cannot be null.");
        }
        if (startTime == null || endTime == null || startTime.isAfter(endTime)) {
            throw new IllegalArgumentException("Invalid time range provided.");
        }
        logger.info("Service: Fetching energy data for device ID: {} between {} and {}", deviceId, startTime, endTime);
        return energyDataMapper.findEnergyDataByDeviceAndTimeRange(deviceId, startTime, endTime);
    }

    @Override
    public List<EnergyData> getAllEnergyData() {
        logger.info("Service: Fetching all energy data");
        return energyDataMapper.findAllEnergyData();
    }
    
    @Override
    @Transactional
    public EnergyData updateEnergyData(Long id, EnergyData energyDataDetails) {
        if (id == null || energyDataDetails == null) {
            throw new IllegalArgumentException("ID and EnergyData details cannot be null for update.");
        }
        EnergyData existingData = energyDataMapper.findEnergyDataById(id);
        if (existingData == null) {
            logger.warn("Service: EnergyData with ID {} not found for update.", id);
            return null;
        }
        // 校验 deviceId 是否更改，如果更改了，新 deviceId 是否有效
        if (energyDataDetails.getDeviceId() != null && !energyDataDetails.getDeviceId().equals(existingData.getDeviceId())) {
            Device device = deviceService.findDeviceById(energyDataDetails.getDeviceId());
            if (device == null) {
                throw new IllegalArgumentException("New Device ID " + energyDataDetails.getDeviceId() + " not found.");
            }
        }
        energyDataDetails.setId(id); //确保ID正确
        int updatedRows = energyDataMapper.updateEnergyData(energyDataDetails);
        if (updatedRows > 0) {
            logger.info("Service: EnergyData with ID {} updated.", id);
            return energyDataMapper.findEnergyDataById(id);
        }
        logger.warn("Service: EnergyData with ID {} found but not updated (no changes or issue).", id);
        return existingData; // or re-fetch: energyDataMapper.findEnergyDataById(id);
    }

    @Override
    @Transactional
    public boolean deleteEnergyData(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null for deletion.");
        }
        EnergyData existingData = energyDataMapper.findEnergyDataById(id);
        if (existingData == null) {
            logger.warn("Service: EnergyData with ID {} not found for deletion.", id);
            return false;
        }
        int deletedRows = energyDataMapper.deleteEnergyData(id);
        return deletedRows > 0;
    }
} 