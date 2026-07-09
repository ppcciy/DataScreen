package com.example.monitor.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("hourly_statistics")
public class HourlyStatistics {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private LocalDate date;
    
    private Integer hour;
    
    private BigDecimal cpuAvg;
    
    private BigDecimal cpuMax;
    
    private BigDecimal cpuMin;
    
    private BigDecimal cpuMedian;
    
    private BigDecimal cpuStd;
    
    private BigDecimal memoryAvg;
    
    private BigDecimal memoryMax;
    
    private BigDecimal diskAvg;
    
    private BigDecimal diskMax;
    
    private BigDecimal networkInAvg;
    
    private BigDecimal networkOutAvg;
    
    private Integer sampleCount;
    
    private LocalDateTime createdAt;
}
