package com.example.monitor.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("performance_data")
public class PerformanceData {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String hostId;
    
    private Long moduleId;
    
    private Long ts;
    
    private LocalDateTime datetime;
    
    private LocalDate date;
    
    private Integer hour;
    
    private Integer minute;
    
    private Integer second;
    
    private Integer weekday;
    
    private BigDecimal value;
    
    private LocalDateTime createdAt;
}
