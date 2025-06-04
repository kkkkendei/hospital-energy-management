package com.example.hospitalenergy.mapper;

import com.example.hospitalenergy.entity.EnergyData;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Mapper
public interface EnergyDataMapper {

    /**
     * 插入新的能源数据记录
     * @param energyData 能源数据对象
     * @return 影响行数
     */
    int insertEnergyData(EnergyData energyData);

    /**
     * 根据ID查询能源数据记录
     * @param id 记录ID
     * @return EnergyData 对象，或 null
     */
    EnergyData findEnergyDataById(Long id);

    /**
     * 根据设备ID查询其所有能源数据记录 (可考虑分页)
     * @param deviceId 设备ID
     * @return 该设备的能源数据列表
     */
    List<EnergyData> findEnergyDataByDeviceId(Long deviceId);

    /**
     * 根据时间范围查询所有设备的能源数据记录 (可考虑分页)
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 在指定时间范围内的能源数据列表
     */
    List<EnergyData> findEnergyDataByTimeRange(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);
    
    /**
     * 根据设备ID和时间范围查询能源数据记录 (可考虑分页)
     * @param deviceId 设备ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 指定设备在指定时间范围内的能源数据列表
     */
    List<EnergyData> findEnergyDataByDeviceAndTimeRange(@Param("deviceId") Long deviceId, 
                                                      @Param("startTime") LocalDateTime startTime, 
                                                      @Param("endTime") LocalDateTime endTime);

    /**
     * (可选) 查询所有能源数据记录 (主要用于测试或小型数据集，实际应用中应分页)
     * @return 所有能源数据列表
     */
    List<EnergyData> findAllEnergyData();

    /**
     * (可选) 更新能源数据记录
     * @param energyData 包含更新信息的能源数据对象 (ID必须存在)
     * @return 影响行数
     */
    int updateEnergyData(EnergyData energyData);

    /**
     * (可选) 根据ID删除能源数据记录
     * @param id 记录ID
     * @return 影响行数
     */
    int deleteEnergyData(Long id);

    // 新增的统计查询方法
    Map<String, Object> queryOverallEnergyStats(@Param("startTime") LocalDateTime startTime, 
                                                @Param("endTime") LocalDateTime endTime, 
                                                @Param("deviceIds") List<Long> deviceIds, 
                                                @Param("energyType") String energyType);

    List<Map<String, Object>> queryEnergyStatsByDevice(@Param("startTime") LocalDateTime startTime, 
                                                       @Param("endTime") LocalDateTime endTime, 
                                                       @Param("deviceIds") List<Long> deviceIds, 
                                                       @Param("energyType") String energyType);

    List<Map<String, Object>> queryEnergyTrendData(@Param("startTime") LocalDateTime startTime, 
                                                   @Param("endTime") LocalDateTime endTime, 
                                                   @Param("deviceIds") List<Long> deviceIds, 
                                                   @Param("energyType") String energyType, 
                                                   @Param("timeGranularity") String timeGranularity);
} 