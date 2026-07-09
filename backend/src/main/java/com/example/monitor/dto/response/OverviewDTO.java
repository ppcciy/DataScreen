package com.example.monitor.dto.response;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OverviewDTO {
    private Integer serverCount;
    private Integer onlineCount;
    private BigDecimal cpuAvg;
    private BigDecimal memoryAvg;
    private BigDecimal diskAvg;
    private BigDecimal networkAvg;
}
