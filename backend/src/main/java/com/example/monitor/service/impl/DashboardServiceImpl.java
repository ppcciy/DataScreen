package com.example.monitor.service.impl;

import com.example.monitor.dto.response.OverviewDTO;
import com.example.monitor.dto.response.TopNResponseDTO;
import com.example.monitor.dto.response.TopNResultDTO;
import com.example.monitor.dto.response.TrendDTO;
import com.example.monitor.entity.AlarmRecord;
import com.example.monitor.entity.HourlyStatistics;
import com.example.monitor.entity.HostInfo;
import com.example.monitor.mapper.*;
import com.example.monitor.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {
    
    private final HostInfoMapper hostInfoMapper;
    private final PerformanceDataMapper performanceDataMapper;
    private final DiskDataMapper diskDataMapper;
    private final HourlyStatisticsMapper hourlyStatisticsMapper;
    private final AlarmRecordMapper alarmRecordMapper;
    
    @Override
    public OverviewDTO getOverview() {
        OverviewDTO overview = new OverviewDTO();
        
        List<HostInfo> hosts = hostInfoMapper.selectList(null);
        overview.setServerCount(hosts.size());
        overview.setOnlineCount(hosts.size());
        
        BigDecimal cpuAvg = performanceDataMapper.getAvgCpu();
        overview.setCpuAvg(cpuAvg != null ? cpuAvg : BigDecimal.ZERO);
        
        BigDecimal memoryAvg = performanceDataMapper.getAvgMemory();
        overview.setMemoryAvg(memoryAvg != null ? memoryAvg : BigDecimal.ZERO);
        
        BigDecimal diskAvg = diskDataMapper.getAvgDisk();
        overview.setDiskAvg(diskAvg != null ? diskAvg : BigDecimal.ZERO);
        
        BigDecimal networkAvg = performanceDataMapper.getAvgNetwork();
        overview.setNetworkAvg(networkAvg != null ? networkAvg : BigDecimal.ZERO);
        
        return overview;
    }
    
    private LocalDateTime getLatestTime() {
        LocalDateTime latestTime = performanceDataMapper.getLatestTime();
        if (latestTime == null) {
            latestTime = LocalDateTime.now();
        }
        return latestTime;
    }
    
    @Override
    public List<TrendDTO> getCpuTrend(Integer hours) {
        LocalDateTime latestTime = getLatestTime();
        LocalDateTime startTime = latestTime.minusHours(hours);
        return performanceDataMapper.getCpuTrend(startTime);
    }
    
    @Override
    public List<TrendDTO> getMemoryTrend(Integer hours) {
        LocalDateTime latestTime = getLatestTime();
        LocalDateTime startTime = latestTime.minusHours(hours);
        return performanceDataMapper.getMemoryTrend(startTime);
    }
    
    @Override
    public List<TrendDTO> getDiskTrend(Integer hours) {
        LocalDateTime latestTime = getLatestTime();
        LocalDateTime startTime = latestTime.minusHours(hours);
        return diskDataMapper.getDiskTrend(startTime);
    }
    
    @Override
    public Map<String, List<TrendDTO>> getNetworkTrend(Integer hours) {
        LocalDateTime latestTime = getLatestTime();
        LocalDateTime startTime = latestTime.minusHours(hours);
        Map<String, List<TrendDTO>> result = new HashMap<>();
        result.put("inbound", performanceDataMapper.getNetworkInTrend(startTime));
        result.put("outbound", performanceDataMapper.getNetworkOutTrend(startTime));
        return result;
    }
    
    @Override
    public TopNResponseDTO getTopN(Integer hours, Integer n) {
        LocalDateTime latestTime = getLatestTime();
        LocalDateTime startTime = latestTime.minusHours(hours);
        TopNResponseDTO response = new TopNResponseDTO();
        
        response.setCpu(performanceDataMapper.getTopNCpu(startTime, n));
        response.setMemory(performanceDataMapper.getTopNMemory(startTime, n));
        response.setDisk(diskDataMapper.getTopNDisk(startTime, n));
        response.setNetwork(performanceDataMapper.getTopNNetwork(startTime, n));
        
        return response;
    }
    
    @Override
    public List<HourlyStatistics> getHourlyStats() {
        LocalDateTime latestTime = getLatestTime();
        LocalDate startDate = latestTime.toLocalDate().minusDays(1);
        return hourlyStatisticsMapper.getHourlyStats(startDate);
    }
    
    @Override
    public List<AlarmRecord> getRecentAlarms(Integer limit) {
        return alarmRecordMapper.getRecentAlarms(limit);
    }
    
    @Override
    public Map<String, Integer> getAlarmStatistics() {
        Map<String, Integer> result = new HashMap<>();
        result.put("CPU>80%", 0);
        result.put("Memory>80%", 0);
        result.put("Disk>90%", 0);
        result.put("Network异常", 0);
        
        List<AlarmRecordMapper.AlarmCountDTO> stats = alarmRecordMapper.getAlarmStatistics();
        for (AlarmRecordMapper.AlarmCountDTO stat : stats) {
            if ("WARNING".equals(stat.getAlarmLevel())) {
                result.put("CPU>80%", result.get("CPU>80%") + stat.getCount());
            } else if ("CRITICAL".equals(stat.getAlarmLevel())) {
                result.put("Disk>90%", result.get("Disk>90%") + stat.getCount());
            }
        }
        
        return result;
    }
}
