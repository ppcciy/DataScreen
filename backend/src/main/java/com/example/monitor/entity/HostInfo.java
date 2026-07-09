package com.example.monitor.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("host_info")
public class HostInfo {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String hostId;
    
    private String hostName;
    
    private String ip;
    
    private String room;
    
    private String serverType;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
}
