const express = require('express');
const mysql = require('mysql2/promise');
const cors = require('cors');
require('dotenv').config();

const app = express();
app.use(cors());
app.use(express.json());

const config = {
  host: 'localhost',
  port: 3306,
  user: 'root',
  password: process.env.DB_PASSWORD || '050519',
  database: 'datacenter_monitor'
};

let pool;

async function initDb() {
  pool = mysql.createPool(config);
  try {
    const [rows] = await pool.query('SELECT 1');
    console.log('✅ 数据库连接成功');
  } catch (err) {
    console.error('❌ 数据库连接失败:', err.message);
    process.exit(1);
  }
}

app.get('/api/dashboard/overview', async (req, res) => {
  try {
    const [hosts] = await pool.query('SELECT COUNT(*) as count FROM host_info');
    const [cpuAvg] = await pool.query(
      'SELECT AVG(p.value) as value FROM performance_data p JOIN module_info m ON p.module_id = m.id WHERE m.mod = ?',
      ['cpu_usage']
    );
    const [memAvg] = await pool.query(
      'SELECT AVG(p.value) as value FROM performance_data p JOIN module_info m ON p.module_id = m.id WHERE m.mod = ?',
      ['mem_used']
    );
    const [diskAvg] = await pool.query('SELECT AVG(value) as value FROM disk_data');
    const [netAvg] = await pool.query(
      'SELECT AVG(p.value) as value FROM performance_data p JOIN module_info m ON p.module_id = m.id WHERE m.mod IN (?, ?)',
      ['net_in', 'net_out']
    );

    res.json({
      code: 200,
      msg: 'success',
      data: {
        serverCount: hosts[0].count || 0,
        onlineCount: hosts[0].count || 0,
        cpuAvg: parseFloat(cpuAvg[0].value || 0).toFixed(2),
        memoryAvg: parseFloat(memAvg[0].value || 0).toFixed(2),
        diskAvg: parseFloat(diskAvg[0].value || 0).toFixed(2),
        networkAvg: parseFloat(netAvg[0].value || 0).toFixed(2)
      }
    });
  } catch (err) {
    console.error('获取概览数据失败:', err);
    res.status(500).json({ code: 500, msg: '获取数据失败', data: null });
  }
});

app.get('/api/dashboard/cpuTrend', async (req, res) => {
  const hours = parseInt(req.query.hours) || 6;
  try {
    const [latest] = await pool.query('SELECT MAX(datetime) as dt FROM performance_data');
    const latestTime = latest[0].dt || new Date();
    const startTime = new Date(latestTime.getTime() - hours * 3600000);

    const [rows] = await pool.query(
      'SELECT p.datetime, AVG(p.value) as value FROM performance_data p JOIN module_info m ON p.module_id = m.id WHERE m.mod = ? AND p.datetime >= ? GROUP BY p.datetime ORDER BY p.datetime',
      ['cpu_usage', startTime]
    );

    res.json({
      code: 200,
      msg: 'success',
      data: rows.map(r => ({
        datetime: r.datetime,
        value: parseFloat(r.value || 0)
      }))
    });
  } catch (err) {
    console.error('获取CPU趋势失败:', err);
    res.status(500).json({ code: 500, msg: '获取数据失败', data: null });
  }
});

app.get('/api/dashboard/memoryTrend', async (req, res) => {
  const hours = parseInt(req.query.hours) || 6;
  try {
    const [latest] = await pool.query('SELECT MAX(datetime) as dt FROM performance_data');
    const latestTime = latest[0].dt || new Date();
    const startTime = new Date(latestTime.getTime() - hours * 3600000);

    const [rows] = await pool.query(
      'SELECT p.datetime, AVG(p.value) as value FROM performance_data p JOIN module_info m ON p.module_id = m.id WHERE m.mod = ? AND p.datetime >= ? GROUP BY p.datetime ORDER BY p.datetime',
      ['mem_used', startTime]
    );

    res.json({
      code: 200,
      msg: 'success',
      data: rows.map(r => ({
        datetime: r.datetime,
        value: parseFloat(r.value || 0)
      }))
    });
  } catch (err) {
    console.error('获取内存趋势失败:', err);
    res.status(500).json({ code: 500, msg: '获取数据失败', data: null });
  }
});

app.get('/api/dashboard/diskTrend', async (req, res) => {
  const hours = parseInt(req.query.hours) || 6;
  try {
    const [latest] = await pool.query('SELECT MAX(datetime) as dt FROM disk_data');
    const latestTime = latest[0].dt || new Date();
    const startTime = new Date(latestTime.getTime() - hours * 3600000);

    const [readRows] = await pool.query(
      'SELECT d.datetime, AVG(d.value) as value FROM disk_data d JOIN module_info m ON d.module_id = m.id WHERE m.mod LIKE ? AND d.datetime >= ? GROUP BY d.datetime ORDER BY d.datetime',
      ['%_read', startTime]
    );

    const [writeRows] = await pool.query(
      'SELECT d.datetime, AVG(d.value) as value FROM disk_data d JOIN module_info m ON d.module_id = m.id WHERE m.mod LIKE ? AND d.datetime >= ? GROUP BY d.datetime ORDER BY d.datetime',
      ['%_write', startTime]
    );

    res.json({
      code: 200,
      msg: 'success',
      data: {
        read: readRows.map(r => ({
          datetime: r.datetime,
          value: parseFloat(r.value || 0)
        })),
        write: writeRows.map(r => ({
          datetime: r.datetime,
          value: parseFloat(r.value || 0)
        }))
      }
    });
  } catch (err) {
    console.error('获取磁盘趋势失败:', err);
    res.status(500).json({ code: 500, msg: '获取数据失败', data: null });
  }
});

app.get('/api/dashboard/networkTrend', async (req, res) => {
  const hours = parseInt(req.query.hours) || 6;
  try {
    const [latest] = await pool.query('SELECT MAX(datetime) as dt FROM performance_data');
    const latestTime = latest[0].dt || new Date();
    const startTime = new Date(latestTime.getTime() - hours * 3600000);

    const [inRows] = await pool.query(
      'SELECT p.datetime, AVG(p.value) as value FROM performance_data p JOIN module_info m ON p.module_id = m.id WHERE m.mod = ? AND p.datetime >= ? GROUP BY p.datetime ORDER BY p.datetime',
      ['net_in', startTime]
    );

    const [outRows] = await pool.query(
      'SELECT p.datetime, AVG(p.value) as value FROM performance_data p JOIN module_info m ON p.module_id = m.id WHERE m.mod = ? AND p.datetime >= ? GROUP BY p.datetime ORDER BY p.datetime',
      ['net_out', startTime]
    );

    res.json({
      code: 200,
      msg: 'success',
      data: {
        inbound: inRows.map(r => ({ datetime: r.datetime, value: parseFloat(r.value || 0) })),
        outbound: outRows.map(r => ({ datetime: r.datetime, value: parseFloat(r.value || 0) }))
      }
    });
  } catch (err) {
    console.error('获取网络趋势失败:', err);
    res.status(500).json({ code: 500, msg: '获取数据失败', data: null });
  }
});

app.get('/api/dashboard/top', async (req, res) => {
  const hours = parseInt(req.query.hours) || 1;
  const n = parseInt(req.query.n) || 10;
  try {
    const [latest] = await pool.query('SELECT MAX(datetime) as dt FROM performance_data');
    const latestTime = latest[0].dt || new Date();
    const startTime = new Date(latestTime.getTime() - hours * 3600000);

    const [cpuTop] = await pool.query(
      'SELECT p.host_id, h.host_name, AVG(p.value) as value FROM performance_data p JOIN module_info m ON p.module_id = m.id JOIN host_info h ON p.host_id = h.host_id WHERE m.mod = ? AND p.datetime >= ? GROUP BY p.host_id, h.host_name ORDER BY value DESC LIMIT ?',
      ['cpu_usage', startTime, n]
    );

    const [memTop] = await pool.query(
      'SELECT p.host_id, h.host_name, AVG(p.value) as value FROM performance_data p JOIN module_info m ON p.module_id = m.id JOIN host_info h ON p.host_id = h.host_id WHERE m.mod = ? AND p.datetime >= ? GROUP BY p.host_id, h.host_name ORDER BY value DESC LIMIT ?',
      ['mem_used', startTime, n]
    );

    const [diskTop] = await pool.query(
      'SELECT d.host_id, h.host_name, AVG(d.value) as value FROM disk_data d JOIN host_info h ON d.host_id = h.host_id WHERE d.datetime >= ? GROUP BY d.host_id, h.host_name ORDER BY value DESC LIMIT ?',
      [startTime, n]
    );

    const [netTop] = await pool.query(
      'SELECT p.host_id, h.host_name, AVG(p.value) as value FROM performance_data p JOIN module_info m ON p.module_id = m.id JOIN host_info h ON p.host_id = h.host_id WHERE m.mod IN (?, ?) AND p.datetime >= ? GROUP BY p.host_id, h.host_name ORDER BY value DESC LIMIT ?',
      ['net_in', 'net_out', startTime, n]
    );

    res.json({
      code: 200,
      msg: 'success',
      data: {
        cpu: cpuTop.map(r => ({ hostId: r.host_id, hostName: r.host_name, value: parseFloat(r.value || 0) })),
        memory: memTop.map(r => ({ hostId: r.host_id, hostName: r.host_name, value: parseFloat(r.value || 0) })),
        disk: diskTop.map(r => ({ hostId: r.host_id, hostName: r.host_name, value: parseFloat(r.value || 0) })),
        network: netTop.map(r => ({ hostId: r.host_id, hostName: r.host_name, value: parseFloat(r.value || 0) }))
      }
    });
  } catch (err) {
    console.error('获取TopN数据失败:', err);
    res.status(500).json({ code: 500, msg: '获取数据失败', data: null });
  }
});

app.get('/api/dashboard/hourly', async (req, res) => {
  try {
    const [latest] = await pool.query('SELECT MAX(date) as d FROM hourly_statistics');
    const latestDate = latest[0].d || new Date();
    const startDate = new Date(latestDate);
    startDate.setDate(startDate.getDate() - 1);

    const [rows] = await pool.query(
      'SELECT * FROM hourly_statistics WHERE date >= ? ORDER BY date, hour',
      [startDate]
    );

    res.json({
      code: 200,
      msg: 'success',
      data: rows.map(r => ({
        date: r.date,
        hour: r.hour,
        cpuAvg: parseFloat(r.cpu_avg || 0),
        cpuMax: parseFloat(r.cpu_max || 0),
        cpuMin: parseFloat(r.cpu_min || 0),
        memoryAvg: parseFloat(r.memory_avg || 0),
        diskAvg: parseFloat(r.disk_avg || 0),
        networkInAvg: parseFloat(r.network_in_avg || 0),
        networkOutAvg: parseFloat(r.network_out_avg || 0),
        sampleCount: r.sample_count || 0
      }))
    });
  } catch (err) {
    console.error('获取小时统计失败:', err);
    res.status(500).json({ code: 500, msg: '获取数据失败', data: null });
  }
});

app.get('/api/dashboard/alarmStats', async (req, res) => {
  try {
    const [rows] = await pool.query('SELECT alarm_level, COUNT(*) as count FROM alarm_record GROUP BY alarm_level');

    const stats = { 'CPU>80%': 0, 'Memory>80%': 0, 'Disk>90%': 0, 'Network异常': 0 };
    rows.forEach(r => {
      if (r.alarm_level === 'WARNING') {
        stats['CPU>80%'] += r.count;
        stats['Memory>80%'] += r.count;
      } else if (r.alarm_level === 'Critical') {
        stats['Disk>90%'] += r.count;
      }
    });

    res.json({
      code: 200,
      msg: 'success',
      data: stats
    });
  } catch (err) {
    console.error('获取告警统计失败:', err);
    res.status(500).json({ code: 500, msg: '获取数据失败', data: null });
  }
});

app.get('/api/host/list', async (req, res) => {
  try {
    const [rows] = await pool.query('SELECT * FROM host_info');
    res.json({
      code: 200,
      msg: 'success',
      data: rows
    });
  } catch (err) {
    console.error('获取主机列表失败:', err);
    res.status(500).json({ code: 500, msg: '获取数据失败', data: null });
  }
});

app.get('/api/host/status', async (req, res) => {
  try {
    const [hosts] = await pool.query('SELECT host_id, host_name FROM host_info');
    const [cpuRows] = await pool.query(
      'SELECT p.host_id, AVG(p.value) as cpu_avg FROM performance_data p JOIN module_info m ON p.module_id = m.id WHERE m.mod = ? GROUP BY p.host_id',
      ['cpu_usage']
    );
    const [memRows] = await pool.query(
      'SELECT p.host_id, AVG(p.value) as mem_avg FROM performance_data p JOIN module_info m ON p.module_id = m.id WHERE m.mod = ? GROUP BY p.host_id',
      ['mem_used']
    );

    const cpuMap = {};
    cpuRows.forEach(r => { cpuMap[r.host_id] = parseFloat(r.cpu_avg || 0); });
    const memMap = {};
    memRows.forEach(r => { memMap[r.host_id] = parseFloat(r.mem_avg || 0); });

    res.json({
      code: 200,
      msg: 'success',
      data: hosts.map(h => ({
        hostId: h.host_id,
        hostName: h.host_name,
        ipAddress: '-',
        cpuUsage: cpuMap[h.host_id] || 0,
        memUsed: memMap[h.host_id] || 0,
        status: 'online'
      }))
    });
  } catch (err) {
    console.error('获取主机状态失败:', err);
    res.status(500).json({ code: 500, msg: '获取数据失败', data: null });
  }
});

app.get('/api/alarm/list', async (req, res) => {
  const limit = parseInt(req.query.limit) || 20;
  try {
    const [rows] = await pool.query(
      'SELECT * FROM alarm_record ORDER BY datetime DESC LIMIT ?',
      [limit]
    );

    res.json({
      code: 200,
      msg: 'success',
      data: rows.map(r => ({
        id: r.id,
        time: r.datetime,
        hostId: r.host_id,
        hostname: r.host_name || r.host_id,
        metric: r.metric_name || r.metric,
        value: parseFloat(r.current_value || r.value || 0),
        level: r.alarm_level,
        moduleId: r.module_id
      }))
    });
  } catch (err) {
    console.error('获取告警列表失败:', err);
    res.status(500).json({ code: 500, msg: '获取数据失败', data: null });
  }
});

app.get('/api/module/samples', async (req, res) => {
  try {
    const [rows] = await pool.query(
      'SELECT m.module_name, COUNT(*) as sample_count FROM performance_data p JOIN module_info m ON p.module_id = m.id GROUP BY m.module_name ORDER BY sample_count DESC LIMIT 10'
    );

    res.json({
      code: 200,
      msg: 'success',
      data: rows.map(r => ({
        name: r.module_name,
        value: r.sample_count
      }))
    });
  } catch (err) {
    console.error('获取模块采样数量失败:', err);
    res.status(500).json({ code: 500, msg: '获取数据失败', data: null });
  }
});

const PORT = 8080;

initDb().then(() => {
  app.listen(PORT, () => {
    console.log(`🚀 Node.js 后端服务已启动，监听端口 ${PORT}`);
  });
});