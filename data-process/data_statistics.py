import pandas as pd
import numpy as np

def calculate_hourly_stats(pref_df, disk_df):
    stats = []
    
    pref_df['hour_key'] = pref_df['date'] + '-' + pref_df['hour'].astype(str)
    disk_df['hour_key'] = disk_df['date'] + '-' + disk_df['hour'].astype(str)
    
    for hour_key, group in pref_df.groupby('hour_key'):
        parts = hour_key.rsplit('-', 1)
        date = parts[0]
        hour = int(parts[1])
        
        cpu_data = group[group['mod'] == 'cpu_usage']['value']
        mem_data = group[group['mod'] == 'mem_used']['value']
        net_in_data = group[group['mod'] == 'net_in']['value']
        net_out_data = group[group['mod'] == 'net_out']['value']
        
        stat = {
            'date': date,
            'hour': hour,
            'cpu_avg': cpu_data.mean() if len(cpu_data) > 0 else None,
            'cpu_max': cpu_data.max() if len(cpu_data) > 0 else None,
            'cpu_min': cpu_data.min() if len(cpu_data) > 0 else None,
            'cpu_median': cpu_data.median() if len(cpu_data) > 0 else None,
            'cpu_std': cpu_data.std() if len(cpu_data) > 1 else None,
            'memory_avg': mem_data.mean() if len(mem_data) > 0 else None,
            'memory_max': mem_data.max() if len(mem_data) > 0 else None,
            'network_in_avg': net_in_data.mean() if len(net_in_data) > 0 else None,
            'network_out_avg': net_out_data.mean() if len(net_out_data) > 0 else None,
            'sample_count': len(group)
        }
        
        disk_group = disk_df[disk_df['hour_key'] == hour_key]
        if len(disk_group) > 0:
            stat['disk_avg'] = disk_group['value'].mean()
            stat['disk_max'] = disk_group['value'].max()
        else:
            stat['disk_avg'] = None
            stat['disk_max'] = None
        
        stats.append(stat)
    
    stats.sort(key=lambda x: (x['date'], x['hour']))
    return stats

def calculate_top_n(pref_df, disk_df, top_n=10):
    top = {}
    
    cpu_top = pref_df[pref_df['mod'] == 'cpu_usage'].groupby('host_id')['value'].mean().nlargest(top_n)
    top['cpu'] = [{'host_id': str(h), 'value': float(v)} for h, v in cpu_top.items()]
    
    mem_top = pref_df[pref_df['mod'] == 'mem_used'].groupby('host_id')['value'].mean().nlargest(top_n)
    top['memory'] = [{'host_id': str(h), 'value': float(v)} for h, v in mem_top.items()]
    
    disk_top = disk_df.groupby('host_id')['value'].mean().nlargest(top_n)
    top['disk'] = [{'host_id': str(h), 'value': float(v)} for h, v in disk_top.items()]
    
    net_top = pref_df[pref_df['mod'].isin(['net_in', 'net_out'])].groupby('host_id')['value'].mean().nlargest(top_n)
    top['network'] = [{'host_id': str(h), 'value': float(v)} for h, v in net_top.items()]
    
    return top

def detect_alarms(pref_df, disk_df, host_map, mod_id_map):
    alarms = []
    
    cpu_data = pref_df[pref_df['mod'] == 'cpu_usage']
    for _, row in cpu_data.iterrows():
        value = float(row['value'])
        host_id = str(row['host_id'])
        level = None
        
        if value >= 90:
            level = 'Critical'
        elif value >= 80:
            level = 'Warning'
        
        if level:
            alarms.append({
                'time': row['datetime'],
                'host_id': host_id,
                'hostname': host_map.get(host_id, host_id),
                'metric': 'CPU',
                'value': value,
                'level': level,
                'module_id': mod_id_map.get('cpu_usage')
            })
    
    mem_data = pref_df[pref_df['mod'] == 'mem_used']
    for _, row in mem_data.iterrows():
        value = float(row['value'])
        host_id = str(row['host_id'])
        level = None
        
        if value >= 90:
            level = 'Critical'
        elif value >= 80:
            level = 'Warning'
        
        if level:
            alarms.append({
                'time': row['datetime'],
                'host_id': host_id,
                'hostname': host_map.get(host_id, host_id),
                'metric': 'Memory',
                'value': value,
                'level': level,
                'module_id': mod_id_map.get('mem_used')
            })
    
    for _, row in disk_df.iterrows():
        value = float(row['value'])
        host_id = str(row['host_id'])
        if value >= 90:
            alarms.append({
                'time': row['datetime'],
                'host_id': host_id,
                'hostname': host_map.get(host_id, host_id),
                'metric': 'Disk',
                'value': value,
                'level': 'Critical',
                'module_id': row.get('module_id')
            })
    
    net_data = pref_df[pref_df['mod'].isin(['net_in', 'net_out'])]
    if len(net_data) > 0:
        net_mean = net_data['value'].mean()
        for _, row in net_data.iterrows():
            value = float(row['value'])
            host_id = str(row['host_id'])
            if value > net_mean * 2:
                alarms.append({
                    'time': row['datetime'],
                    'host_id': host_id,
                    'hostname': host_map.get(host_id, host_id),
                    'metric': 'Network',
                    'value': value,
                    'level': 'Warning',
                    'module_id': mod_id_map.get(row['mod'])
                })
    
    alarms.sort(key=lambda x: x['time'], reverse=True)
    return alarms