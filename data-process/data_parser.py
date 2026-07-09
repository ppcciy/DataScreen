import pandas as pd
import numpy as np

def parse_timestamp(ts):
    try:
        ts_int = int(ts)
        if len(str(ts_int)) == 13:
            dt = pd.to_datetime(ts_int, unit='ms')
        else:
            dt = pd.to_datetime(ts_int, unit='s')
        
        return {
            'ts': ts_int,
            'datetime': dt.strftime('%Y-%m-%d %H:%M:%S'),
            'date': dt.strftime('%Y-%m-%d'),
            'hour': dt.hour,
            'minute': dt.minute,
            'second': dt.second,
            'weekday': dt.weekday() + 1
        }
    except:
        return None

def read_dat_file(file_path):
    try:
        df = pd.read_csv(file_path, sep='\t', encoding='utf-8', dtype=str)
        return df
    except UnicodeDecodeError:
        df = pd.read_csv(file_path, sep='\t', encoding='gbk', dtype=str)
        return df

def parse_host_detail(file_path):
    df = read_dat_file(file_path)
    df.columns = df.columns.str.strip()
    df = df.rename(columns={
        'hostid': 'host_id',
        'hostname': 'host_name',
        'owner': 'owner',
        'model': 'model',
        'location1': 'room',
        'location2': 'rack'
    })
    df = df.drop_duplicates(subset=['host_id'])
    df = df.dropna(subset=['host_id', 'host_name'])
    return df

def parse_mod_detail(file_path):
    df = read_dat_file(file_path)
    df.columns = df.columns.str.strip()
    df = df.rename(columns={
        'mod': 'mod',
        'type': 'type',
        'desc': 'description',
        'unit': 'unit',
        'tag': 'tag'
    })
    df['module_name'] = df['description']
    df = df.drop_duplicates(subset=['mod'])
    df = df.dropna(subset=['mod'])
    return df

def parse_pref_tsar(file_path):
    df = read_dat_file(file_path)
    df.columns = df.columns.str.strip()
    
    df = df.rename(columns={
        'hostid': 'host_id',
        'mod': 'mod',
        'ts': 'ts_str',
        'value': 'value'
    })
    
    df['value'] = pd.to_numeric(df['value'], errors='coerce')
    
    df = df.dropna(subset=['host_id', 'mod', 'ts_str', 'value'])
    df = df.drop_duplicates()
    
    ts_data = df['ts_str'].apply(parse_timestamp)
    ts_df = pd.DataFrame(ts_data.tolist())
    
    df = df.drop(columns=['ts_str'])
    df = pd.concat([df, ts_df], axis=1)
    df = df.dropna(subset=['datetime'])
    df = df.sort_values('ts').reset_index(drop=True)
    
    return df

def parse_disk_tsar(file_path):
    df = read_dat_file(file_path)
    df.columns = df.columns.str.strip()
    
    df = df.rename(columns={
        'hostid': 'host_id',
        'mod': 'mod',
        'ts': 'ts_str',
        'value': 'value'
    })
    
    df['value'] = pd.to_numeric(df['value'], errors='coerce')
    
    df = df.dropna(subset=['host_id', 'mod', 'ts_str', 'value'])
    df = df.drop_duplicates()
    
    ts_data = df['ts_str'].apply(parse_timestamp)
    ts_df = pd.DataFrame(ts_data.tolist())
    
    df = df.drop(columns=['ts_str'])
    df = pd.concat([df, ts_df], axis=1)
    df = df.dropna(subset=['datetime'])
    df = df.sort_values('ts').reset_index(drop=True)
    
    df['device'] = df['mod'].str.split('_').str[0]
    
    return df

def merge_data(pref_df, disk_df, host_df):
    host_map = host_df.set_index('host_id')['host_name'].to_dict()
    
    pref_df['hostname'] = pref_df['host_id'].map(host_map)
    disk_df['hostname'] = disk_df['host_id'].map(host_map)
    
    return pref_df, disk_df