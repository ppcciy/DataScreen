package com.example.monitor.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.monitor.entity.PerformanceData;
import com.example.monitor.dto.response.TrendDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface PerformanceDataMapper extends BaseMapper<PerformanceData> {
    @Select("SELECT MAX(datetime) FROM performance_data")
    LocalDateTime getLatestTime();
    
    @Select("SELECT p.datetime, AVG(p.value) as value " +
            "FROM performance_data p " +
            "JOIN module_info m ON p.module_id = m.id " +
            "WHERE m.mod = 'cpu_usage' AND p.datetime >= #{startTime} " +
            "GROUP BY p.datetime ORDER BY p.datetime")
    List<TrendDTO> getCpuTrend(@Param("startTime") LocalDateTime startTime);
    
    @Select("SELECT p.datetime, AVG(p.value) as value " +
            "FROM performance_data p " +
            "JOIN module_info m ON p.module_id = m.id " +
            "WHERE m.mod LIKE '%memory%' AND p.datetime >= #{startTime} " +
            "GROUP BY p.datetime ORDER BY p.datetime")
    List<TrendDTO> getMemoryTrend(@Param("startTime") LocalDateTime startTime);
    
    @Select("SELECT p.datetime, AVG(p.value) as value " +
            "FROM performance_data p " +
            "JOIN module_info m ON p.module_id = m.id " +
            "WHERE m.mod LIKE '%net_in%' AND p.datetime >= #{startTime} " +
            "GROUP BY p.datetime ORDER BY p.datetime")
    List<TrendDTO> getNetworkInTrend(@Param("startTime") LocalDateTime startTime);
    
    @Select("SELECT p.datetime, AVG(p.value) as value " +
            "FROM performance_data p " +
            "JOIN module_info m ON p.module_id = m.id " +
            "WHERE m.mod LIKE '%net_out%' AND p.datetime >= #{startTime} " +
            "GROUP BY p.datetime ORDER BY p.datetime")
    List<TrendDTO> getNetworkOutTrend(@Param("startTime") LocalDateTime startTime);
    
    @Select("SELECT AVG(p.value) FROM performance_data p " +
            "JOIN module_info m ON p.module_id = m.id " +
            "WHERE m.mod = 'cpu_usage'")
    BigDecimal getAvgCpu();
    
    @Select("SELECT AVG(p.value) FROM performance_data p " +
            "JOIN module_info m ON p.module_id = m.id " +
            "WHERE m.mod LIKE '%memory%'")
    BigDecimal getAvgMemory();
    
    @Select("SELECT AVG(p.value) FROM performance_data p " +
            "JOIN module_info m ON p.module_id = m.id " +
            "WHERE m.mod LIKE '%net%'")
    BigDecimal getAvgNetwork();
    
    @Select("SELECT p.host_id, h.host_name, AVG(p.value) as value " +
            "FROM performance_data p " +
            "JOIN module_info m ON p.module_id = m.id " +
            "JOIN host_info h ON p.host_id = h.host_id " +
            "WHERE m.mod = 'cpu_usage' AND p.datetime >= #{startTime} " +
            "GROUP BY p.host_id, h.host_name ORDER BY value DESC LIMIT #{n}")
    List<com.example.monitor.dto.response.TopNResultDTO> getTopNCpu(@Param("startTime") LocalDateTime startTime, @Param("n") Integer n);
    
    @Select("SELECT p.host_id, h.host_name, AVG(p.value) as value " +
            "FROM performance_data p " +
            "JOIN module_info m ON p.module_id = m.id " +
            "JOIN host_info h ON p.host_id = h.host_id " +
            "WHERE m.mod LIKE '%memory%' AND p.datetime >= #{startTime} " +
            "GROUP BY p.host_id, h.host_name ORDER BY value DESC LIMIT #{n}")
    List<com.example.monitor.dto.response.TopNResultDTO> getTopNMemory(@Param("startTime") LocalDateTime startTime, @Param("n") Integer n);
    
    @Select("SELECT p.host_id, h.host_name, AVG(p.value) as value " +
            "FROM performance_data p " +
            "JOIN module_info m ON p.module_id = m.id " +
            "JOIN host_info h ON p.host_id = h.host_id " +
            "WHERE m.mod LIKE '%net%' AND p.datetime >= #{startTime} " +
            "GROUP BY p.host_id, h.host_name ORDER BY value DESC LIMIT #{n}")
    List<com.example.monitor.dto.response.TopNResultDTO> getTopNNetwork(@Param("startTime") LocalDateTime startTime, @Param("n") Integer n);
}
