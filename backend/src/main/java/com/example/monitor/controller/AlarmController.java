package com.example.monitor.controller;

import com.example.monitor.dto.response.Result;
import com.example.monitor.entity.AlarmRecord;
import com.example.monitor.mapper.AlarmRecordMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/alarm")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AlarmController {
    
    private final AlarmRecordMapper alarmRecordMapper;
    
    @GetMapping("/list")
    public Result<List<AlarmRecord>> getAlarmList(
            @RequestParam(defaultValue = "20") Integer limit) {
        return Result.success(alarmRecordMapper.getRecentAlarms(limit));
    }
}
