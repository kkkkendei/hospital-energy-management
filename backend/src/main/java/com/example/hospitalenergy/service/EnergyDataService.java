package com.example.hospitalenergy.service;

import com.example.hospitalenergy.entity.EnergyData;
import java.time.LocalDateTime;
import java.util.List;

public interface EnergyDataService {

    /**
     * 添加新的能源数据记录
     * @param energyData 要保存的能源数据对象
     * @return 保存后的能源数据对象 (包含ID)
     * @throws IllegalArgumentException 如果输入数据校验失败 (例如，关联的设备不存在)
     */
    EnergyData addEnergyData(EnergyData energyData);

    /**
     * 根据ID获取能源数据记录
     * @param id 记录ID
     * @return EnergyData 对象，如果找不到返回 null
     */
    EnergyData getEnergyDataById(Long id);

    /**
     * 根据设备ID获取其所有能源数据记录
     * @param deviceId 设备ID
     * @return 该设备的能源数据列表
     */
    List<EnergyData> getEnergyDataByDeviceId(Long deviceId);

    /**
     * 根据时间范围获取所有设备的能源数据记录
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 在指定时间范围内的能源数据列表
     */
    List<EnergyData> getEnergyDataByTimeRange(LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 根据设备ID和时间范围获取能源数据记录
     * @param deviceId 设备ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 指定设备在指定时间范围内的能源数据列表
     */
    List<EnergyData> getEnergyDataByDeviceAndTimeRange(Long deviceId, LocalDateTime startTime, LocalDateTime endTime);

    /**
     * (可选) 获取所有能源数据记录
     * @return 所有能源数据列表
     */
    List<EnergyData> getAllEnergyData();
    
    /**
     * (可选) 更新能源数据
     * @param id 要更新的能源数据ID
     * @param energyDataDetails 包含更新信息的对象
     * @return 更新后的对象，如果未找到则返回null
     */
    EnergyData updateEnergyData(Long id, EnergyData energyDataDetails);

    /**
     * (可选) 删除能源数据
     * @param id 要删除的能源数据ID
     * @return 是否删除成功
     */
    boolean deleteEnergyData(Long id);
} 