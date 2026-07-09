package com.example.monitor.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("module_info")
public class ModuleInfo {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String mod;
    
    private String moduleName;
    
    private String metricName;
    
    private String unit;
    
    private String description;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
}
