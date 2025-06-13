package com.example.hospitalenergy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.mybatis.spring.annotation.MapperScan; // 如果使用MyBatis, 取消此行注释

@SpringBootApplication
@MapperScan("com.example.hospitalenergy.mapper")
public class HospitalEnergyApplication {

    public static void main(String[] args) {
        SpringApplication.run(HospitalEnergyApplication.class, args);
    }

} 