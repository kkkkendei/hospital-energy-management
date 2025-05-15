package com.example.hospitalenergy.mapper;

import com.example.hospitalenergy.entity.Device;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface DeviceMapper {

    /**
     * 插入新设备
     * @param device 设备信息
     * @return 影响行数
     */
    int insertDevice(Device device);

    /**
     * 根据ID查询设备信息
     * @param id 设备ID
     * @return 设备对象，如果找不到返回null
     */
    Device findDeviceById(Long id);

    /**
     * 查询所有设备列表
     * @return 设备列表
     */
    List<Device> findAllDevices();

    /**
     * 更新设备信息
     * @param device 包含更新信息的设备对象 (ID必须存在)
     * @return 影响行数
     */
    int updateDevice(Device device);

    /**
     * 根据ID删除设备
     * @param id 设备ID
     * @return 影响行数
     */
    int deleteDevice(Long id);

    // 可根据需要添加其他查询方法，例如：
    // List<Device> findDevicesByType(String type);
    // List<Device> findDevicesByLocation(String location);
    // List<Device> findDevicesByStatus(String status);
} 