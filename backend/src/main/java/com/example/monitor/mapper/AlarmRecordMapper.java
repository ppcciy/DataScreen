package com.example.monitor.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.monitor.entity.AlarmRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface AlarmRecordMapper extends BaseMapper<AlarmRecord> {
    @Select("SELECT * FROM alarm_record WHERE is_recovered = 0 ORDER BY datetime DESC LIMIT #{limit}")
    List<AlarmRecord> getRecentAlarms(@Param("limit") Integer limit);
    
    @Select("SELECT alarm_level, COUNT(*) as count FROM alarm_record WHERE is_recovered = 0 GROUP BY alarm_level")
    List<AlarmCountDTO> getAlarmStatistics();
    
    interface AlarmCountDTO {
        String getAlarmLevel();
        Integer getCount();
    }
}
