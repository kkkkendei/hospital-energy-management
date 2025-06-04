package com.example.hospitalenergy.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EnergyData {

    private Long id;                // 主键, 自增
    private Long deviceId;          // 关联的设备ID (外键，关联到 devices.id)
    private LocalDateTime recordTime;   // 数据记录时间 (例如: 2023-10-27T10:15:30)
    private Double energyValue;       // 能耗数值
    private String energyUnit;        // 能耗单位 (例如: kWh, m³, kg, L)
    private String notes;             // 备注 (可选)

    // 如果需要，可以添加一个字段来表示能源的具体类型，比如 电、水、气等。
    // private String energyType; // 若与 Device 中的 energyType 不同或需要更细化
} 