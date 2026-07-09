package com.example.monitor;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.example.monitor.mapper")
public class DataCenterMonitorApplication {
    public static void main(String[] args) {
        SpringApplication.run(DataCenterMonitorApplication.class, args);
    }
}
