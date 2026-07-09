package com.example.monitor.service;

import com.example.monitor.dto.response.OverviewDTO;
import com.example.monitor.dto.response.TopNResponseDTO;
import com.example.monitor.dto.response.TrendDTO;
import com.example.monitor.entity.AlarmRecord;
import com.example.monitor.entity.HourlyStatistics;

import java.util.List;
import java.util.Map;

public interface DashboardService {
    OverviewDTO getOverview();
    
    List<TrendDTO> getCpuTrend(Integer hours);
    
    List<TrendDTO> getMemoryTrend(Integer hours);
    
    List<TrendDTO> getDiskTrend(Integer hours);
    
    Map<String, List<TrendDTO>> getNetworkTrend(Integer hours);
    
    TopNResponseDTO getTopN(Integer hours, Integer n);
    
    List<HourlyStatistics> getHourlyStats();
    
    List<AlarmRecord> getRecentAlarms(Integer limit);
    
    Map<String, Integer> getAlarmStatistics();
}
