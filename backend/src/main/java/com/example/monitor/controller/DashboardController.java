package com.example.monitor.controller;

import com.example.monitor.dto.response.Result;
import com.example.monitor.dto.response.OverviewDTO;
import com.example.monitor.dto.response.TopNResponseDTO;
import com.example.monitor.dto.response.TrendDTO;
import com.example.monitor.entity.AlarmRecord;
import com.example.monitor.entity.HourlyStatistics;
import com.example.monitor.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class DashboardController {
    
    private final DashboardService dashboardService;
    
    @GetMapping("/overview")
    public Result<OverviewDTO> getOverview() {
        return Result.success(dashboardService.getOverview());
    }
    
    @GetMapping("/cpuTrend")
    public Result<List<TrendDTO>> getCpuTrend(
            @RequestParam(defaultValue = "6") Integer hours) {
        return Result.success(dashboardService.getCpuTrend(hours));
    }
    
    @GetMapping("/memoryTrend")
    public Result<List<TrendDTO>> getMemoryTrend(
            @RequestParam(defaultValue = "6") Integer hours) {
        return Result.success(dashboardService.getMemoryTrend(hours));
    }
    
    @GetMapping("/diskTrend")
    public Result<List<TrendDTO>> getDiskTrend(
            @RequestParam(defaultValue = "6") Integer hours) {
        return Result.success(dashboardService.getDiskTrend(hours));
    }
    
    @GetMapping("/networkTrend")
    public Result<Map<String, List<TrendDTO>>> getNetworkTrend(
            @RequestParam(defaultValue = "6") Integer hours) {
        return Result.success(dashboardService.getNetworkTrend(hours));
    }
    
    @GetMapping("/top")
    public Result<TopNResponseDTO> getTopN(
            @RequestParam(defaultValue = "1") Integer hours,
            @RequestParam(defaultValue = "10") Integer n) {
        return Result.success(dashboardService.getTopN(hours, n));
    }
    
    @GetMapping("/hourly")
    public Result<List<HourlyStatistics>> getHourlyStats() {
        return Result.success(dashboardService.getHourlyStats());
    }
    
    @GetMapping("/alarmStats")
    public Result<Map<String, Integer>> getAlarmStatistics() {
        return Result.success(dashboardService.getAlarmStatistics());
    }
}
