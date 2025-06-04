package com.example.hospitalenergy.service;

import com.example.hospitalenergy.entity.Device;
import java.util.List;

public interface DeviceService {

    /**
     * 获取所有设备列表
     * @return 设备列表
     */
    List<Device> getAllDevices();

    /**
     * 根据ID查找设备
     * @param id 设备ID
     * @return 设备对象，如果找不到返回 Optional.empty() 或 null (具体实现决定)
     */
    Device findDeviceById(Long id);

    /**
     * 创建新设备
     * @param device 要创建的设备对象
     * @return 创建成功后的设备对象 (包含ID)
     */
    Device createDevice(Device device);

    /**
     * 更新现有设备
     * @param id 要更新的设备ID
     * @param deviceDetails 包含更新信息的设备对象
     * @return 更新后的设备对象，如果设备不存在可能返回null或抛出异常
     */
    Device updateDevice(Long id, Device deviceDetails);

    /**
     * 根据ID删除设备
     * @param id 设备ID
     * @return 删除成功返回 true，否则 false (或 void)
     */
    boolean deleteDevice(Long id);
} 