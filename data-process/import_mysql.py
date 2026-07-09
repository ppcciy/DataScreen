import os
import sys
import pandas as pd
from dotenv import load_dotenv
from sqlalchemy import create_engine, text
from data_parser import (
    parse_host_detail,
    parse_mod_detail,
    parse_pref_tsar,
    parse_disk_tsar,
    merge_data
)
from data_statistics import calculate_hourly_stats, calculate_top_n, detect_alarms

CREATE_TABLES_SQL = """
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
    owner VARCHAR(64) COMMENT '负责人',
    model VARCHAR(128) COMMENT '服务器型号',
    room VARCHAR(64) COMMENT '机房位置',
    rack VARCHAR(64) COMMENT '机柜位置',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_host_id (host_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='服务器信息表';

CREATE TABLE module_info (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    `mod` VARCHAR(64) NOT NULL UNIQUE COMMENT '模块标识',
    type VARCHAR(64) COMMENT '类型',
    module_name VARCHAR(128) NOT NULL COMMENT '模块名称',
    unit VARCHAR(32) COMMENT '单位',
    description TEXT COMMENT '指标说明',
    tag VARCHAR(128) COMMENT '标签',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_mod (`mod`)
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
    INDEX idx_date_hour (date, hour)
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
    INDEX idx_date_hour (date, hour)
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
"""

def connect_db(db_name=None):
    load_dotenv()
    db_host = os.getenv('DB_HOST', 'localhost')
    db_port = os.getenv('DB_PORT', '3306')
    db_user = os.getenv('DB_USER', 'root')
    db_password = os.getenv('DB_PASSWORD', '')
    if db_name is None:
        db_name = os.getenv('DB_NAME', 'datacenter_monitor')
    
    connection_string = f'mysql+pymysql://{db_user}:{db_password}@{db_host}:{db_port}/{db_name}?charset=utf8mb4'
    engine = create_engine(connection_string)
    return engine

def load_dat_files(data_path):
    host_file = os.path.join(data_path, 'host_detail.dat')
    mod_file = os.path.join(data_path, 'mod_detail.dat')
    pref_file = os.path.join(data_path, 'pref_tsar.dat')
    disk_file = os.path.join(data_path, 'disk_tsar.dat')
    
    print(f"读取主机信息: {host_file}")
    host_df = parse_host_detail(host_file)
    print(f"主机数量: {len(host_df)}")
    
    print(f"读取模块信息: {mod_file}")
    mod_df = parse_mod_detail(mod_file)
    print(f"模块数量: {len(mod_df)}")
    
    print(f"读取性能数据: {pref_file}")
    pref_df = parse_pref_tsar(pref_file)
    print(f"性能数据条数: {len(pref_df)}")
    
    print(f"读取磁盘数据: {disk_file}")
    disk_df = parse_disk_tsar(disk_file)
    print(f"磁盘数据条数: {len(disk_df)}")
    
    return host_df, mod_df, pref_df, disk_df

def create_tables(engine):
    print("\n--- 创建表结构 ---")
    with engine.connect() as conn:
        for statement in CREATE_TABLES_SQL.strip().split(';'):
            statement = statement.strip()
            if statement:
                conn.execute(text(statement))
        conn.commit()
    print("表结构创建成功")

def import_to_db(engine, host_df, mod_df, pref_df, disk_df):
    print("\n--- 导入主机信息 ---")
    host_df.to_sql('host_info', engine, if_exists='append', index=False)
    print(f"已导入 {len(host_df)} 条主机信息")
    
    print("\n--- 导入模块信息 ---")
    mod_df.to_sql('module_info', engine, if_exists='append', index=False)
    print(f"已导入 {len(mod_df)} 条模块信息")
    
    print("\n--- 获取模块ID映射 ---")
    with engine.connect() as conn:
        mod_result = conn.execute(text("SELECT `mod`, id FROM module_info"))
        mod_id_map = {row[0]: row[1] for row in mod_result}
    
    pref_df['module_id'] = pref_df['mod'].map(mod_id_map)
    pref_df = pref_df.dropna(subset=['module_id'])
    
    print("\n--- 导入性能数据 ---")
    perf_cols = ['host_id', 'module_id', 'ts', 'datetime', 'date', 'hour', 'minute', 'second', 'weekday', 'value']
    perf_data = pref_df[perf_cols].copy()
    perf_data.to_sql('performance_data', engine, if_exists='append', index=False)
    print(f"已导入 {len(perf_data)} 条性能数据")
    
    disk_df['module_id'] = disk_df['mod'].map(mod_id_map)
    disk_df = disk_df.dropna(subset=['module_id'])
    
    print("\n--- 导入磁盘数据 ---")
    disk_cols = ['host_id', 'module_id', 'device', 'ts', 'datetime', 'date', 'hour', 'minute', 'second', 'weekday', 'value']
    disk_data = disk_df[disk_cols].copy()
    disk_data.to_sql('disk_data', engine, if_exists='append', index=False)
    print(f"已导入 {len(disk_data)} 条磁盘数据")
    
    print("\n--- 计算小时统计 ---")
    hourly_stats = calculate_hourly_stats(pref_df, disk_df)
    stats_df = pd.DataFrame(hourly_stats)
    stats_df.to_sql('hourly_statistics', engine, if_exists='append', index=False)
    print(f"已计算 {len(stats_df)} 条小时统计数据")
    
    print("\n--- 检测告警 ---")
    host_map = host_df.set_index('host_id')['host_name'].to_dict()
    alarm_list = detect_alarms(pref_df, disk_df, host_map, mod_id_map)
    if alarm_list:
        alarm_df = pd.DataFrame(alarm_list)
        alarm_df = alarm_df.rename(columns={
            'time': 'datetime',
            'hostname': 'host_name',
            'metric': 'metric_name',
            'value': 'current_value',
            'level': 'alarm_level'
        })
        alarm_df['is_recovered'] = 0
        alarm_df.to_sql('alarm_record', engine, if_exists='append', index=False)
        print(f"已检测到 {len(alarm_df)} 条告警")
    else:
        print("未检测到告警")
    
    print("\n--- 数据导入完成 ---")
    return mod_id_map

def main():
    try:
        data_path = os.getenv('DATA_PATH', '../')
        db_name = os.getenv('DB_NAME', 'datacenter_monitor')
        
        if not os.path.exists(data_path):
            print(f"数据目录不存在: {data_path}")
            sys.exit(1)
        
        host_df, mod_df, pref_df, disk_df = load_dat_files(data_path)
        
        print("\n--- 创建数据库 ---")
        root_engine = connect_db('mysql')
        with root_engine.connect() as conn:
            conn.execute(text(f"CREATE DATABASE IF NOT EXISTS {db_name} DEFAULT CHARACTER SET utf8mb4"))
            conn.commit()
        print(f"数据库 {db_name} 创建成功")
        
        engine = connect_db(db_name)
        
        create_tables(engine)
        
        import_to_db(engine, host_df, mod_df, pref_df, disk_df)
        
        print("\n✓ 所有数据已成功导入MySQL数据库!")
        
    except Exception as e:
        print(f"\n✗ 导入过程中发生错误: {e}")
        import traceback
        traceback.print_exc()
        sys.exit(1)

if __name__ == '__main__':
    main()