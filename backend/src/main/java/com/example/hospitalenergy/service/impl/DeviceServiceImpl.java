package com.example.hospitalenergy.service.impl;

import com.example.hospitalenergy.entity.Device;
import com.example.hospitalenergy.mapper.DeviceMapper;
import com.example.hospitalenergy.service.DeviceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DeviceServiceImpl implements DeviceService {

    private static final Logger logger = LoggerFactory.getLogger(DeviceServiceImpl.class);

    @Autowired
    private DeviceMapper deviceMapper;

    @Override
    public List<Device> getAllDevices() {
        logger.info("Service: Fetching all devices");
        return deviceMapper.findAllDevices();
    }

    @Override
    public Device findDeviceById(Long id) {
        logger.info("Service: Fetching device with ID: {}", id);
        return deviceMapper.findDeviceById(id);
    }

    @Override
    @Transactional // 确保操作的原子性
    public Device createDevice(Device device) {
        logger.info("Service: Creating new device: {}", device.getName());
        // 可以在这里添加更复杂的业务校验逻辑，如果需要的话
        // 例如：检查设备名称或序列号是否已存在（如果业务要求唯一性且数据库层面未做严格约束）
        /*
        if (deviceMapper.findByName(device.getName()) != null) {
            throw new IllegalArgumentException("Device with name '" + device.getName() + "' already exists.");
        }
        */
        deviceMapper.insertDevice(device); // ID will be set in the device object by useGeneratedKeys
        logger.info("Service: Device created successfully with ID: {}", device.getId());
        return device; // 返回包含ID的device对象
    }

    @Override
    @Transactional
    public Device updateDevice(Long id, Device deviceDetails) {
        logger.info("Service: Updating device with ID: {}", id);
        Device existingDevice = deviceMapper.findDeviceById(id);
        if (existingDevice == null) {
            logger.warn("Service: Cannot update. Device with ID: {} not found.", id);
            return null; // 或者抛出自定义异常 e.g., ResourceNotFoundException
        }

        // 更新现有对象的属性
        // 注意：这里是部分更新的逻辑，只更新 deviceDetails 中非null的字段，或者可以采取全量更新
        // 为了保持和原Controller逻辑一致，我们先按MyBatis XML中的<set>动态更新逻辑来
        // 确保ID正确设置以供Mapper使用
        deviceDetails.setId(id);

        int updatedRows = deviceMapper.updateDevice(deviceDetails);
        if (updatedRows == 0) {
            logger.warn("Service: Device with ID: {} found but not updated. Maybe no changes or concurrent modification?", id);
            // 可以选择返回existingDevice，或者null，或者更新后的deviceDetails（如果mapper.updateDevice修改了它）
            // 最安全的是重新查询一次以获取最新状态
            return deviceMapper.findDeviceById(id); // 或者返回 deviceDetails，取决于updateDevice是否修改了传入对象
        }
        logger.info("Service: Device with ID: {} updated successfully.", id);
        return deviceMapper.findDeviceById(id); // 返回更新后的完整设备信息
    }

    @Override
    @Transactional
    public boolean deleteDevice(Long id) {
        logger.info("Service: Deleting device with ID: {}", id);
        Device existingDevice = deviceMapper.findDeviceById(id);
        if (existingDevice == null) {
            logger.warn("Service: Cannot delete. Device with ID: {} not found.", id);
            return false;
        }
        int deletedRows = deviceMapper.deleteDevice(id);
        if (deletedRows > 0) {
            logger.info("Service: Device with ID: {} deleted successfully.", id);
            return true;
        }
        logger.warn("Service: Device with ID: {} was not deleted. Rows affected: {}", id, deletedRows);
        return false;
    }
} 