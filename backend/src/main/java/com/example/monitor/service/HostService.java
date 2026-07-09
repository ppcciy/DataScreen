package com.example.monitor.service;

import com.example.monitor.dto.response.HostStatusDTO;
import com.example.monitor.entity.HostInfo;

import java.util.List;

public interface HostService {
    List<HostInfo> getHostList();
    
    HostInfo getHostById(Long id);
    
    List<HostStatusDTO> getHostStatus();
}
