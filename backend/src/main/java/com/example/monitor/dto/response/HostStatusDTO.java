package com.example.monitor.dto.response;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class HostStatusDTO {
    private String hostId;
    private String hostname;
    private String ip;
    private BigDecimal cpu;
    private BigDecimal memory;
    private BigDecimal disk;
    private BigDecimal network;
    private String status;
}
