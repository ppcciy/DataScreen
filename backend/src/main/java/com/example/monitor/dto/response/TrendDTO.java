package com.example.monitor.dto.response;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class TrendDTO {
    private LocalDateTime datetime;
    private BigDecimal value;
}
