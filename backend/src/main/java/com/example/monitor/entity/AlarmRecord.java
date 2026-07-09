package com.example.monitor.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("alarm_record")
public class AlarmRecord {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private LocalDateTime datetime;
    
    private String hostId;
    
    private String hostName;
    
    private Long moduleId;
    
    private String metricName;
    
    private BigDecimal currentValue;
    
    private String alarmLevel;
    
    private Integer isRecovered;
    
    private LocalDateTime recoveredAt;
    
    private LocalDateTime createdAt;
}
