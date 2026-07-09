package com.example.monitor.dto.response;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class TopNResultDTO {
    private String hostId;
    private String hostName;
    private BigDecimal value;
}
