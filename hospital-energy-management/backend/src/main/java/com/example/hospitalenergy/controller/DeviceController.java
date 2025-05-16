package com.example.hospitalenergy.controller;

import com.example.hospitalenergy.entity.Device;
import com.example.hospitalenergy.service.DeviceService;
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
    private DeviceService deviceService;

    // GET all devices
    @GetMapping
    public ResponseEntity<List<Device>> getAllDevices() {
        logger.info("Controller: Fetching all devices");
        try {
            List<Device> devices = deviceService.getAllDevices();
            if (devices == null || devices.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(devices);
        } catch (Exception e) {
            logger.error("Controller: Error fetching all devices: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // GET device by ID
    @GetMapping("/{id}")
    public ResponseEntity<Device> getDeviceById(@PathVariable Long id) {
        logger.info("Controller: Fetching device with ID: {}", id);
        try {
            Device device = deviceService.findDeviceById(id);
            if (device == null) {
                logger.warn("Controller: Device with ID: {} not found.", id);
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(device);
        } catch (Exception e) {
            logger.error("Controller: Error fetching device with ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // CREATE a new device
    @PostMapping
    public ResponseEntity<?> createDevice(@RequestBody Device device) {
        logger.info("Controller: Creating new device: {}", device.getName());
        try {
            if (device.getName() == null || device.getName().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Device name cannot be empty.");
            }
            
            Device createdDevice = deviceService.createDevice(device);
            logger.info("Controller: Device created successfully with ID: {}", createdDevice.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(createdDevice);
        } catch (IllegalArgumentException iae) {
            logger.warn("Controller: Bad request creating device: {}", iae.getMessage());
            return ResponseEntity.badRequest().body(iae.getMessage());
        } catch (Exception e) {
            logger.error("Controller: Error creating device {}: {}", device.getName(), e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to create device: " + e.getMessage());
        }
    }

    // UPDATE an existing device
    @PutMapping("/{id}")
    public ResponseEntity<?> updateDevice(@PathVariable Long id, @RequestBody Device deviceDetails) {
        logger.info("Controller: Updating device with ID: {}", id);
        try {
            if (deviceDetails.getName() != null && deviceDetails.getName().trim().isEmpty()){
                 return ResponseEntity.badRequest().body("Device name cannot be empty if provided for update.");
            }

            Device updatedDevice = deviceService.updateDevice(id, deviceDetails);
            if (updatedDevice == null) {
                logger.warn("Controller: Cannot update. Device with ID: {} not found or not modified.", id);
                Device checkDevice = deviceService.findDeviceById(id);
                if (checkDevice == null) return ResponseEntity.notFound().build();
                return ResponseEntity.ok(checkDevice);
            }
            logger.info("Controller: Device with ID: {} updated successfully.", id);
            return ResponseEntity.ok(updatedDevice);
        } catch (IllegalArgumentException iae) {
            logger.warn("Controller: Bad request updating device: {}", iae.getMessage());
            return ResponseEntity.badRequest().body(iae.getMessage());
        } catch (Exception e) {
            logger.error("Controller: Error updating device with ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to update device: " + e.getMessage());
        }
    }

    // DELETE a device
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDevice(@PathVariable Long id) {
        logger.info("Controller: Deleting device with ID: {}", id);
        try {
            boolean deleted = deviceService.deleteDevice(id);
            if (!deleted) {
                logger.warn("Controller: Cannot delete. Device with ID: {} not found or not deleted.", id);
                return ResponseEntity.notFound().build();
            }
            logger.info("Controller: Device with ID: {} deleted successfully.", id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            logger.error("Controller: Error deleting device with ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
} 