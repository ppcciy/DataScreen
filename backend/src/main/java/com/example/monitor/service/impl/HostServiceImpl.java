package com.example.monitor.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.monitor.dto.response.HostStatusDTO;
import com.example.monitor.entity.HostInfo;
import com.example.monitor.entity.PerformanceData;
import com.example.monitor.mapper.HostInfoMapper;
import com.example.monitor.mapper.ModuleInfoMapper;
import com.example.monitor.mapper.PerformanceDataMapper;
import com.example.monitor.service.HostService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class HostServiceImpl implements HostService {
    
    private final HostInfoMapper hostInfoMapper;
    private final PerformanceDataMapper performanceDataMapper;
    
    @Override
    public List<HostInfo> getHostList() {
        return hostInfoMapper.selectList(null);
    }
    
    @Override
    public HostInfo getHostById(Long id) {
        return hostInfoMapper.selectById(id);
    }
    
    @Override
    public List<HostStatusDTO> getHostStatus() {
        List<HostInfo> hosts = hostInfoMapper.selectList(null);
        LocalDateTime startTime = LocalDateTime.now().minusHours(1);
        
        Map<String, BigDecimal> cpuMap = new HashMap<>();
        Map<String, BigDecimal> memoryMap = new HashMap<>();
        Map<String, BigDecimal> networkMap = new HashMap<>();
        
        List<PerformanceData> perfData = performanceDataMapper.selectList(
            new LambdaQueryWrapper<PerformanceData>()
                .ge(PerformanceData::getDatetime, startTime)
        );
        
        for (PerformanceData data : perfData) {
            String hostId = data.getHostId();
            BigDecimal value = data.getValue();
            
            cpuMap.merge(hostId, value, BigDecimal::add);
            memoryMap.merge(hostId, value, BigDecimal::add);
            networkMap.merge(hostId, value, BigDecimal::add);
        }
        
        List<HostStatusDTO> statusList = new ArrayList<>();
        for (HostInfo host : hosts) {
            HostStatusDTO status = new HostStatusDTO();
            status.setHostId(host.getHostId());
            status.setHostname(host.getHostName());
            status.setIp(host.getIp());
            
            status.setCpu(cpuMap.getOrDefault(host.getHostId(), BigDecimal.ZERO));
            status.setMemory(memoryMap.getOrDefault(host.getHostId(), BigDecimal.ZERO));
            status.setDisk(BigDecimal.ZERO);
            status.setNetwork(networkMap.getOrDefault(host.getHostId(), BigDecimal.ZERO));
            
            BigDecimal cpu = status.getCpu();
            if (cpu != null && cpu.compareTo(new BigDecimal("90")) >= 0) {
                status.setStatus("danger");
            } else if (cpu != null && cpu.compareTo(new BigDecimal("80")) >= 0) {
                status.setStatus("warning");
            } else {
                status.setStatus("normal");
            }
            
            statusList.add(status);
        }
        
        return statusList;
    }
}
