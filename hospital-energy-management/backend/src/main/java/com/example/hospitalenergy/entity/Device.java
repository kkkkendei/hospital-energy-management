package com.example.hospitalenergy.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate; // 使用 Java 8 Date/Time API

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Device {
    private Long id;                // 主键, 自增
    private String name;              // 设备名称
    private String model;             // 设备型号
    private String type;              // 设备类型 (例如：照明、空调、泵、风机、医疗设备等)
    private String location;          // 安装位置
    private LocalDate installationDate; // 安装日期
    private String status;            // 设备状态 (例如：运行中、停止、故障、维护中)
    private String manufacturer;      // 生产厂家 (可选)
    private String serialNumber;      // 设备序列号 (可选, 唯一性看需求)
    private Double ratedPower;        // 额定功率 (kW, 可选)
    private String energyType;        // 消耗能源类型 (例如：电、水、蒸汽, 可选)
    private String notes;             // 备注 (可选)

    // 可以根据实际需求增删字段或调整类型
    // 例如，如果 type 和 status 的值是固定的，可以考虑使用枚举类型替代 String
} 