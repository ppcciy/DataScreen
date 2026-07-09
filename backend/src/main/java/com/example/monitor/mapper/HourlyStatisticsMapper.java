package com.example.monitor.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.monitor.entity.HourlyStatistics;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface HourlyStatisticsMapper extends BaseMapper<HourlyStatistics> {
    @Select("SELECT * FROM hourly_statistics WHERE date >= #{startDate} ORDER BY date, hour")
    List<HourlyStatistics> getHourlyStats(@Param("startDate") LocalDate startDate);
}
