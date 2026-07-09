# DataScreen

> 企业级数据中心监控大屏系统，采用前后端分离架构，提供实时性能监控、趋势分析、告警统计等功能。

## 项目预览

![DataScreen Dashboard](https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=data%20center%20monitoring%20dashboard%20with%20charts%20and%20graphs%20in%20morandi%20color%20palette%20enterprise%20BI%20style&image_size=landscape_16_9)

## 技术栈

### 前端
| 技术 | 版本 | 说明 |
|------|------|------|
| Vue | 3.4+ | 渐进式JavaScript框架 |
| Vite | 5.0+ | 下一代前端构建工具 |
| ECharts | 5.5+ | 数据可视化图表库 |
| Element Plus | 2.6+ | Vue3 UI组件库 |
| Axios | 1.6+ | HTTP客户端 |

### 后端
| 技术 | 版本 | 说明 |
|------|------|------|
| Node.js | 18+ | JavaScript运行时 |
| Express | 4.18+ | Web应用框架 |
| MySQL | 8.0+ | 关系型数据库 |
| mysql2 | 3.6+ | MySQL驱动 |
| cors | 2.8+ | 跨域资源共享 |

### 数据处理
| 技术 | 版本 | 说明 |
|------|------|------|
| Python | 3.9+ | 数据处理语言 |
| pandas | 1.5+ | 数据分析库 |
| numpy | 1.24+ | 数值计算库 |
| SQLAlchemy | 2.0+ | ORM框架 |

## 功能特性

### 实时监控
- 📊 **系统概览** - 服务器数量、在线状态、资源利用率统计
- 🖥️ **主机状态** - 实时CPU、内存、磁盘、网络监控
- 📈 **性能趋势** - CPU/内存/磁盘/网络多维度趋势分析

### 数据分析
- ⏰ **小时统计** - 按小时维度的性能指标汇总
- 🏆 **TOP排行** - 资源消耗前N主机排名
- 🥧 **模块采样** - 各监控模块采样数量分布

### 告警管理
- 🚨 **告警统计** - 按级别分类的告警数量统计
- 📋 **告警列表** - 实时告警记录展示与轮播

## 项目结构

```
DataScreen/
├── backend-node/           # Node.js后端服务
│   ├── server.js           # Express服务器入口
│   ├── package.json        # 后端依赖配置
│   └── .env                # 环境变量配置
├── backend/                # Spring Boot后端(备用)
│   └── src/main/java/...   # Java源代码
├── data-process/           # Python数据处理
│   ├── data_parser.py      # .dat文件解析
│   ├── data_statistics.py  # 统计分析
│   ├── import_mysql.py     # MySQL导入
│   └── requirements.txt    # Python依赖
├── database/               # 数据库配置
│   └── create.sql          # 建表脚本
├── src/                    # Vue前端源码
│   ├── components/         # 组件目录
│   │   ├── AlarmList.vue       # 告警列表
│   │   ├── AlarmRingChart.vue  # 告警统计圆环图
│   │   ├── CpuTrendChart.vue   # CPU趋势图
│   │   ├── DiskIoChart.vue     # 磁盘IO图表
│   │   ├── Header.vue          # 页头组件
│   │   ├── HostStatusTable.vue # 主机状态表
│   │   ├── HourlyStatsChart.vue# 小时统计图
│   │   ├── MemoryTrendChart.vue# 内存趋势图
│   │   ├── NetworkChart.vue    # 网络流量图
│   │   ├── RankingChart.vue    # TOP排行图
│   │   ├── SamplePieChart.vue  # 采样数量饼图
│   │   └── StatCard.vue        # 统计卡片
│   ├── api/                # API封装
│   │   └── dashboard.js    # 仪表盘API
│   ├── config/             # 配置文件
│   │   ├── chartConfig.js  # 图表基础配置
│   │   └── colors.js       # 颜色配置
│   ├── App.vue             # 根组件
│   ├── main.js             # 入口文件
│   └── style.css           # 全局样式
├── .gitignore              # Git忽略配置
├── package.json            # 前端依赖配置
├── vite.config.js          # Vite配置
└── index.html              # HTML模板
```

## 快速开始

### 环境要求
- Node.js >= 18.x
- Python >= 3.9.x
- MySQL >= 8.0.x

### 步骤1：数据库配置

```bash
# 创建数据库
mysql -u root -p -e "CREATE DATABASE datacenter_monitor CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"

# 导入表结构
mysql -u root -p datacenter_monitor < database/create.sql
```

### 步骤2：数据导入

```bash
cd data-process

# 安装依赖
pip install -r requirements.txt

# 配置环境变量
cp .env.example .env
# 编辑 .env 文件，配置数据库连接信息

# 执行数据导入
python import_mysql.py
```

### 步骤3：启动后端服务

```bash
cd backend-node

# 安装依赖
npm install

# 配置环境变量
cp .env.example .env
# 编辑 .env 文件，配置数据库连接信息

# 启动服务
npm start
# 服务运行在 http://localhost:8080
```

### 步骤4：启动前端服务

```bash
# 安装依赖
npm install

# 启动开发服务器
npm run dev
# 服务运行在 http://localhost:5173
```

## API接口

| 接口 | 方法 | 描述 |
|------|------|------|
| `/api/dashboard/overview` | GET | 获取系统概览数据 |
| `/api/dashboard/cpuTrend` | GET | 获取CPU趋势数据 |
| `/api/dashboard/memoryTrend` | GET | 获取内存趋势数据 |
| `/api/dashboard/diskTrend` | GET | 获取磁盘IO趋势数据 |
| `/api/dashboard/networkTrend` | GET | 获取网络流量趋势数据 |
| `/api/dashboard/hourlyStats` | GET | 获取小时统计数据 |
| `/api/dashboard/top` | GET | 获取TOP排行数据 |
| `/api/host/status` | GET | 获取主机状态列表 |
| `/api/alarm/list` | GET | 获取告警记录列表 |
| `/api/alarm/statistics` | GET | 获取告警统计数据 |
| `/api/module/samples` | GET | 获取模块采样数量 |

## 数据来源

原始数据来自以下 `.dat` 文件：
- `host_detail.dat` - 主机详细信息
- `mod_detail.dat` - 监控模块定义
- `pref_tsar.dat` - 性能指标数据（CPU、内存、网络等）
- `disk_tsar.dat` - 磁盘IO数据

## 开发指南

### 添加新图表组件

1. 在 `src/components/` 创建新组件文件
2. 在 `src/App.vue` 中引入并注册组件
3. 在 `src/api/dashboard.js` 中添加对应的API方法

### 添加新API接口

1. 在 `backend-node/server.js` 中添加新的路由处理
2. 在前端 `src/api/dashboard.js` 中添加调用方法

## 部署说明

### 生产环境构建

```bash
# 构建前端
npm run build

# 构建产物位于 dist/ 目录
```

### Nginx配置示例

```nginx
server {
    listen 80;
    server_name your-domain.com;

    location / {
        root /path/to/DataScreen/dist;
        try_files $uri $uri/ /index.html;
    }

    location /api/ {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }
}
```

## 许可证

MIT License

## 贡献

欢迎提交Issue和Pull Request！

---

**项目地址**: [https://github.com/ppcciy/DataScreen](https://github.com/ppcciy/DataScreen)