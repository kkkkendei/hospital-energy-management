package com.example.hospitalenergy.controller;

import com.example.hospitalenergy.entity.Device;
import com.example.hospitalenergy.mapper.DeviceMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/devices")
public class DeviceController {

    private static final Logger logger = LoggerFactory.getLogger(DeviceController.class);

    @Autowired
    private DeviceMapper deviceMapper;

    // GET all devices
    @GetMapping
    public ResponseEntity<List<Device>> getAllDevices() {
        logger.info("Fetching all devices");
        try {
            List<Device> devices = deviceMapper.findAllDevices();
            if (devices.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(devices);
        } catch (Exception e) {
            logger.error("Error fetching all devices: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // GET device by ID
    @GetMapping("/{id}")
    public ResponseEntity<Device> getDeviceById(@PathVariable Long id) {
        logger.info("Fetching device with ID: {}", id);
        try {
            Device device = deviceMapper.findDeviceById(id);
            if (device == null) {
                logger.warn("Device with ID: {} not found.", id);
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(device);
        } catch (Exception e) {
            logger.error("Error fetching device with ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // CREATE a new device
    @PostMapping
    public ResponseEntity<?> createDevice(@RequestBody Device device) {
        logger.info("Creating new device: {}", device.getName());
        try {
            // Basic validation example (can be expanded with @Valid and DTOs)
            if (device.getName() == null || device.getName().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Device name cannot be empty.");
            }
            // You might want to check for duplicate serial numbers if they should be unique before inserting

            deviceMapper.insertDevice(device); // ID will be set in the device object by useGeneratedKeys
            logger.info("Device created successfully with ID: {}", device.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(device);
        } catch (Exception e) {
            logger.error("Error creating device {}: {}", device.getName(), e.getMessage(), e);
            // Consider more specific error handling, e.g., for duplicate serial_number if it has a UNIQUE constraint
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to create device: " + e.getMessage());
        }
    }

    // UPDATE an existing device
    @PutMapping("/{id}")
    public ResponseEntity<?> updateDevice(@PathVariable Long id, @RequestBody Device deviceDetails) {
        logger.info("Updating device with ID: {}", id);
        try {
            Device existingDevice = deviceMapper.findDeviceById(id);
            if (existingDevice == null) {
                logger.warn("Cannot update. Device with ID: {} not found.", id);
                return ResponseEntity.notFound().build();
            }

            // Basic validation example
            if (deviceDetails.getName() != null && deviceDetails.getName().trim().isEmpty()){
                 return ResponseEntity.badRequest().body("Device name cannot be empty if provided for update.");
            }

            // Set ID for the update, ensure it matches the path variable
            deviceDetails.setId(id);
            
            int updatedRows = deviceMapper.updateDevice(deviceDetails);
            if (updatedRows == 0) {
                 // This might happen if the device was deleted between find and update, or if update logic didn't match any rows
                logger.warn("Device with ID: {} found but not updated. Maybe no changes or concurrent modification?", id);
                // Return the existing device or an appropriate message
                return ResponseEntity.status(HttpStatus.NOT_MODIFIED).body("Device not updated, no changes or data mismatch."); 
            }
            logger.info("Device with ID: {} updated successfully.", id);
            return ResponseEntity.ok(deviceMapper.findDeviceById(id)); // Return the updated device
        } catch (Exception e) {
            logger.error("Error updating device with ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to update device: " + e.getMessage());
        }
    }

    // DELETE a device
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDevice(@PathVariable Long id) {
        logger.info("Deleting device with ID: {}", id);
        try {
            Device existingDevice = deviceMapper.findDeviceById(id);
            if (existingDevice == null) {
                logger.warn("Cannot delete. Device with ID: {} not found.", id);
                return ResponseEntity.notFound().build();
            }
            deviceMapper.deleteDevice(id);
            logger.info("Device with ID: {} deleted successfully.", id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            logger.error("Error deleting device with ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
} 