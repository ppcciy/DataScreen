package com.example.monitor.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.monitor.entity.DiskData;
import com.example.monitor.dto.response.TrendDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface DiskDataMapper extends BaseMapper<DiskData> {
    @Select("SELECT d.datetime, AVG(d.value) as value " +
            "FROM disk_data d " +
            "JOIN module_info m ON d.module_id = m.id " +
            "WHERE d.datetime >= #{startTime} " +
            "GROUP BY d.datetime ORDER BY d.datetime")
    List<TrendDTO> getDiskTrend(@Param("startTime") LocalDateTime startTime);
    
    @Select("SELECT AVG(d.value) FROM disk_data d")
    BigDecimal getAvgDisk();
    
    @Select("SELECT d.host_id, h.host_name, AVG(d.value) as value " +
            "FROM disk_data d " +
            "JOIN host_info h ON d.host_id = h.host_id " +
            "WHERE d.datetime >= #{startTime} " +
            "GROUP BY d.host_id, h.host_name ORDER BY value DESC LIMIT #{n}")
    List<com.example.monitor.dto.response.TopNResultDTO> getTopNDisk(@Param("startTime") LocalDateTime startTime, @Param("n") Integer n);
}
