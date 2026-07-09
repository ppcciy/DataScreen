CREATE DATABASE IF NOT EXISTS datacenter_monitor DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE datacenter_monitor;

DROP TABLE IF EXISTS alarm_record;
DROP TABLE IF EXISTS hourly_statistics;
DROP TABLE IF EXISTS disk_data;
DROP TABLE IF EXISTS performance_data;
DROP TABLE IF EXISTS module_info;
DROP TABLE IF EXISTS host_info;

CREATE TABLE host_info (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    host_id VARCHAR(64) NOT NULL UNIQUE COMMENT '主机唯一标识',
    host_name VARCHAR(128) NOT NULL COMMENT '主机名称',
    ip VARCHAR(32) COMMENT 'IP地址',
    room VARCHAR(64) COMMENT '机房位置',
    server_type VARCHAR(64) COMMENT '服务器类型',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_host_id (host_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='服务器信息表';

CREATE TABLE module_info (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    mod VARCHAR(64) NOT NULL UNIQUE COMMENT '模块标识',
    module_name VARCHAR(128) NOT NULL COMMENT '模块名称',
    metric_name VARCHAR(128) COMMENT '指标名称',
    unit VARCHAR(32) COMMENT '单位',
    description TEXT COMMENT '指标说明',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_mod (mod)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='指标说明表';

CREATE TABLE performance_data (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    host_id VARCHAR(64) NOT NULL COMMENT '主机ID',
    module_id BIGINT NOT NULL COMMENT '模块ID',
    ts BIGINT NOT NULL COMMENT '原始时间戳',
    datetime DATETIME NOT NULL COMMENT '转换后的日期时间',
    date DATE NOT NULL COMMENT '日期',
    hour INT NOT NULL COMMENT '小时',
    minute INT NOT NULL COMMENT '分钟',
    second INT NOT NULL COMMENT '秒',
    weekday INT NOT NULL COMMENT '星期几',
    value DECIMAL(20,4) NOT NULL COMMENT '指标值',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_host_id (host_id),
    INDEX idx_module_id (module_id),
    INDEX idx_datetime (datetime),
    INDEX idx_date_hour (date, hour),
    CONSTRAINT fk_perf_host FOREIGN KEY (host_id) REFERENCES host_info(host_id),
    CONSTRAINT fk_perf_module FOREIGN KEY (module_id) REFERENCES module_info(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='性能监控表';

CREATE TABLE disk_data (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    host_id VARCHAR(64) NOT NULL COMMENT '主机ID',
    module_id BIGINT NOT NULL COMMENT '模块ID',
    device VARCHAR(64) NOT NULL COMMENT '磁盘设备名',
    ts BIGINT NOT NULL COMMENT '原始时间戳',
    datetime DATETIME NOT NULL COMMENT '转换后的日期时间',
    date DATE NOT NULL COMMENT '日期',
    hour INT NOT NULL COMMENT '小时',
    minute INT NOT NULL COMMENT '分钟',
    second INT NOT NULL COMMENT '秒',
    weekday INT NOT NULL COMMENT '星期几',
    value DECIMAL(20,4) NOT NULL COMMENT '指标值',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_host_id (host_id),
    INDEX idx_module_id (module_id),
    INDEX idx_device (device),
    INDEX idx_datetime (datetime),
    INDEX idx_date_hour (date, hour),
    CONSTRAINT fk_disk_host FOREIGN KEY (host_id) REFERENCES host_info(host_id),
    CONSTRAINT fk_disk_module FOREIGN KEY (module_id) REFERENCES module_info(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='磁盘监控表';

CREATE TABLE hourly_statistics (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    date DATE NOT NULL COMMENT '日期',
    hour INT NOT NULL COMMENT '小时',
    cpu_avg DECIMAL(10,4) COMMENT 'CPU平均利用率',
    cpu_max DECIMAL(10,4) COMMENT 'CPU最大利用率',
    cpu_min DECIMAL(10,4) COMMENT 'CPU最小利用率',
    cpu_median DECIMAL(10,4) COMMENT 'CPU中位数',
    cpu_std DECIMAL(10,4) COMMENT 'CPU标准差',
    memory_avg DECIMAL(10,4) COMMENT '内存平均使用量',
    memory_max DECIMAL(10,4) COMMENT '内存最大使用量',
    disk_avg DECIMAL(10,4) COMMENT '磁盘平均IO',
    disk_max DECIMAL(10,4) COMMENT '磁盘最大IO',
    network_in_avg DECIMAL(10,4) COMMENT '网络入站平均',
    network_out_avg DECIMAL(10,4) COMMENT '网络出站平均',
    sample_count INT COMMENT '采样数量',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_date_hour (date, hour)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='小时统计结果表';

CREATE TABLE alarm_record (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    datetime DATETIME NOT NULL COMMENT '告警时间',
    host_id VARCHAR(64) NOT NULL COMMENT '主机ID',
    host_name VARCHAR(128) COMMENT '主机名称',
    module_id BIGINT NOT NULL COMMENT '模块ID',
    metric_name VARCHAR(128) COMMENT '指标名称',
    current_value DECIMAL(20,4) NOT NULL COMMENT '当前值',
    alarm_level VARCHAR(32) NOT NULL COMMENT '告警等级: WARNING, CRITICAL',
    is_recovered TINYINT(1) DEFAULT 0 COMMENT '是否已恢复: 0未恢复, 1已恢复',
    recovered_at DATETIME COMMENT '恢复时间',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_datetime (datetime),
    INDEX idx_host_id (host_id),
    INDEX idx_alarm_level (alarm_level),
    INDEX idx_is_recovered (is_recovered)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='告警记录表';
