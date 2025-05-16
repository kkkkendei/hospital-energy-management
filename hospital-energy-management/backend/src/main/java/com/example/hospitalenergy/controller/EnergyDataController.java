package com.example.hospitalenergy.controller;

import com.example.hospitalenergy.entity.EnergyData;
import com.example.hospitalenergy.service.EnergyDataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/energy-data")
public class EnergyDataController {

    private static final Logger logger = LoggerFactory.getLogger(EnergyDataController.class);

    @Autowired
    private EnergyDataService energyDataService;

    // CREATE new energy data record
    @PostMapping
    public ResponseEntity<?> createEnergyData(@RequestBody EnergyData energyData) {
        logger.info("Controller: Received request to create energy data for device ID: {}", energyData.getDeviceId());
        try {
            // 基本校验可以放在Controller，更复杂的业务校验在Service层
            if (energyData.getDeviceId() == null) {
                return ResponseEntity.badRequest().body("Device ID is required.");
            }
            if (energyData.getEnergyValue() == null) {
                return ResponseEntity.badRequest().body("Energy value is required.");
            }
            if (energyData.getEnergyUnit() == null || energyData.getEnergyUnit().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Energy unit is required.");
            }
            // recordTime 可以在service层设默认值，或要求前端传递

            EnergyData createdEnergyData = energyDataService.addEnergyData(energyData);
            logger.info("Controller: Energy data created successfully with ID: {}", createdEnergyData.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(createdEnergyData);
        } catch (IllegalArgumentException iae) {
            logger.warn("Controller: Bad request creating energy data - {}", iae.getMessage());
            return ResponseEntity.badRequest().body(iae.getMessage());
        } catch (Exception e) {
            logger.error("Controller: Error creating energy data for device ID {}: {}", energyData.getDeviceId(), e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to create energy data: " + e.getMessage());
        }
    }

    // GET energy data by its ID
    @GetMapping("/{id}")
    public ResponseEntity<EnergyData> getEnergyDataById(@PathVariable Long id) {
        logger.info("Controller: Fetching energy data with ID: {}", id);
        EnergyData energyData = energyDataService.getEnergyDataById(id);
        if (energyData == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(energyData);
    }

    // GET all energy data (use with caution, consider pagination for large datasets)
    @GetMapping
    public ResponseEntity<List<EnergyData>> getAllEnergyData() {
        logger.info("Controller: Fetching all energy data");
        List<EnergyData> energyDataList = energyDataService.getAllEnergyData();
        if (energyDataList == null || energyDataList.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(energyDataList);
    }

    // GET energy data for a specific device
    @GetMapping("/device/{deviceId}")
    public ResponseEntity<List<EnergyData>> getEnergyDataByDevice(@PathVariable Long deviceId) {
        logger.info("Controller: Fetching energy data for device ID: {}", deviceId);
        List<EnergyData> energyDataList = energyDataService.getEnergyDataByDeviceId(deviceId);
        if (energyDataList == null || energyDataList.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(energyDataList);
    }

    // GET energy data within a time range for all devices
    @GetMapping("/range")
    public ResponseEntity<List<EnergyData>> getEnergyDataByTimeRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        logger.info("Controller: Fetching energy data between {} and {}", startTime, endTime);
        try {
            List<EnergyData> energyDataList = energyDataService.getEnergyDataByTimeRange(startTime, endTime);
            if (energyDataList == null || energyDataList.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(energyDataList);
        } catch (IllegalArgumentException iae) {
            logger.warn("Controller: Bad request for time range - {}", iae.getMessage());
            return ResponseEntity.badRequest().body(iae.getMessage());
        } catch (Exception e) {
            logger.error("Controller: Error fetching energy data by time range: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // GET energy data for a specific device within a time range
    @GetMapping("/device/{deviceId}/range")
    public ResponseEntity<List<EnergyData>> getEnergyDataByDeviceAndTimeRange(
            @PathVariable Long deviceId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        logger.info("Controller: Fetching energy data for device ID: {} between {} and {}", deviceId, startTime, endTime);
        try {
            List<EnergyData> energyDataList = energyDataService.getEnergyDataByDeviceAndTimeRange(deviceId, startTime, endTime);
            if (energyDataList == null || energyDataList.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(energyDataList);
        } catch (IllegalArgumentException iae) {
            logger.warn("Controller: Bad request for device time range - {}", iae.getMessage());
            return ResponseEntity.badRequest().body(iae.getMessage());
        } catch (Exception e) {
            logger.error("Controller: Error fetching energy data by device and time range: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // UPDATE an existing energy data record
    @PutMapping("/{id}")
    public ResponseEntity<?> updateEnergyDataRecord(@PathVariable Long id, @RequestBody EnergyData energyDataDetails) {
        logger.info("Controller: Request to update energy data with ID: {}", id);
        try {
            EnergyData updatedData = energyDataService.updateEnergyData(id, energyDataDetails);
            if (updatedData == null) {
                return ResponseEntity.notFound().build(); // Or based on service logic, if not found vs not modified
            }
            return ResponseEntity.ok(updatedData);
        } catch (IllegalArgumentException iae) {
            logger.warn("Controller: Bad request updating energy data - {}", iae.getMessage());
            return ResponseEntity.badRequest().body(iae.getMessage());
        } catch (Exception e) {
            logger.error("Controller: Error updating energy data with ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to update energy data: " + e.getMessage());
        }
    }

    // DELETE an energy data record
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEnergyDataRecord(@PathVariable Long id) {
        logger.info("Controller: Request to delete energy data with ID: {}", id);
        try {
            boolean deleted = energyDataService.deleteEnergyData(id);
            if (!deleted) {
                // This could mean not found, or deletion failed for other reasons (handled in service)
                return ResponseEntity.notFound().build(); 
            }
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException iae) {
             logger.warn("Controller: Bad request deleting energy data - {}", iae.getMessage());
            return ResponseEntity.badRequest().body(iae.getMessage());
        } catch (Exception e) {
            logger.error("Controller: Error deleting energy data with ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
} 