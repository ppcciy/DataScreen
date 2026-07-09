package com.example.monitor.controller;

import com.example.monitor.dto.response.HostStatusDTO;
import com.example.monitor.dto.response.Result;
import com.example.monitor.entity.AlarmRecord;
import com.example.monitor.entity.HostInfo;
import com.example.monitor.mapper.AlarmRecordMapper;
import com.example.monitor.service.HostService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/host")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class HostController {
    
    private final HostService hostService;
    private final AlarmRecordMapper alarmRecordMapper;
    
    @GetMapping("/list")
    public Result<List<HostInfo>> getHostList() {
        return Result.success(hostService.getHostList());
    }
    
    @GetMapping("/{id}")
    public Result<HostInfo> getHostById(@PathVariable Long id) {
        return Result.success(hostService.getHostById(id));
    }
    
    @GetMapping("/status")
    public Result<List<HostStatusDTO>> getHostStatus() {
        return Result.success(hostService.getHostStatus());
    }
}
