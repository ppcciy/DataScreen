<template>
  <div class="dashboard-container">
    <Header />
    <div class="stats-row">
      <StatCard
        v-for="(stat, index) in statCards"
        :key="index"
        :title="stat.title"
        :value="stat.value"
        :unit="stat.unit"
        :icon="stat.icon"
        :color="stat.color"
        :trend-percent="stat.trendPercent"
        :trend-up="stat.trendUp"
        :hour-trend-percent="stat.hourTrendPercent"
        :hour-trend-up="stat.hourTrendUp"
      />
    </div>
    <div class="main-content">
      <div class="left-panel">
        <CpuTrendChart />
        <MemoryTrendChart :data="memoryTrend" :key="'mem-' + dataLoaded" />
        <DiskIoChart :data="diskTrend" :key="'disk-' + dataLoaded" />
      </div>
      <div class="center-panel">
        <NetworkChart :data="networkTrend" :key="'net-' + dataLoaded" />
        <HostStatusTable :data="hostStatusData" />
      </div>
      <div class="right-panel">
        <RankingChart
          title="CPU排行榜"
          :data="topNData.cpu || []"
          :color="colors.accent"
          unit="%"
          :key="'cpu-rank-' + dataLoaded"
        />
        <RankingChart
          title="磁盘排行榜"
          :data="topNData.disk || []"
          :color="colors.chart.disk"
          unit="%"
          :key="'disk-rank-' + dataLoaded"
        />
        <div class="right-bottom">
          <SamplePieChart :data="moduleSamples" :key="'pie-' + dataLoaded" />
        <AlarmRingChart :data="alarmStats" :key="'ring-' + dataLoaded" />
        </div>
        <HourlyStatsChart :data="hourlyStats" :key="'hour-' + dataLoaded" />
        <AlarmList :data="alarmList" :key="'alarm-' + dataLoaded" />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import Header from '@/components/Header.vue'
import StatCard from '@/components/StatCard.vue'
import CpuTrendChart from '@/components/CpuTrendChart.vue'
import MemoryTrendChart from '@/components/MemoryTrendChart.vue'
import DiskIoChart from '@/components/DiskIoChart.vue'
import NetworkChart from '@/components/NetworkChart.vue'
import HostStatusTable from '@/components/HostStatusTable.vue'
import RankingChart from '@/components/RankingChart.vue'
import SamplePieChart from '@/components/SamplePieChart.vue'
import HourlyStatsChart from '@/components/HourlyStatsChart.vue'
import AlarmRingChart from '@/components/AlarmRingChart.vue'
import AlarmList from '@/components/AlarmList.vue'
import { colors } from '@/config/colors'
import { dashboardAPI, hostAPI, alarmAPI } from '@/api/dashboard'

const overview = ref(null)
const hostDetail = ref([])
const hostStatusData = ref([])
const cpuTrend = ref([])
const memoryTrend = ref([])
const diskTrend = ref([])
const networkTrend = ref({})
const topNData = ref({})
const hourlyStats = ref([])
const alarmStats = ref({})
const alarmList = ref([])
const moduleSamples = ref([])
const dataLoaded = ref(0)
let refreshTimer = null

const statCards = computed(() => {
  if (!overview.value) {
    return Array(6).fill({
      title: '-',
      value: 0,
      unit: '',
      icon: '📊',
      color: colors.accent,
      trendPercent: '0',
      trendUp: true,
      hourTrendPercent: '0',
      hourTrendUp: true
    })
  }
  
  const o = overview.value
  return [
    {
      title: '服务器总数',
      value: o.serverCount || 0,
      unit: '台',
      icon: '🖥️',
      color: colors.accent,
      trendPercent: '0',
      trendUp: true,
      hourTrendPercent: '0',
      hourTrendUp: true
    },
    {
      title: '在线服务器数量',
      value: o.onlineCount || 0,
      unit: '台',
      icon: '✅',
      color: colors.success,
      trendPercent: '0',
      trendUp: true,
      hourTrendPercent: '0',
      hourTrendUp: true
    },
    {
      title: 'CPU平均利用率',
      value: parseFloat(o.cpuAvg) || 0,
      unit: '%',
      icon: '💻',
      color: colors.accent,
      trendPercent: '2.3',
      trendUp: true,
      hourTrendPercent: '1.5',
      hourTrendUp: true
    },
    {
      title: '内存平均利用率',
      value: parseFloat(o.memoryAvg) || 0,
      unit: 'GB',
      icon: '💾',
      color: colors.success,
      trendPercent: '1.8',
      trendUp: true,
      hourTrendPercent: '0.8',
      hourTrendUp: true
    },
    {
      title: '磁盘平均利用率',
      value: parseFloat(o.diskAvg) || 0,
      unit: '%',
      icon: '📀',
      color: colors.warning,
      trendPercent: '3.2',
      trendUp: true,
      hourTrendPercent: '1.2',
      hourTrendUp: true
    },
    {
      title: '网络吞吐量',
      value: parseFloat(o.networkAvg) || 0,
      unit: 'MB/s',
      icon: '🌐',
      color: colors.accent,
      trendPercent: '5.6',
      trendUp: true,
      hourTrendPercent: '2.1',
      hourTrendUp: true
    }
  ]
})

async function loadData() {
  try {
    const [
      overviewRes,
      hostListRes,
      hostStatusRes,
      cpuTrendRes,
      memoryTrendRes,
      diskTrendRes,
      networkTrendRes,
      topNRes,
      hourlyStatsRes,
      alarmStatsRes,
      alarmListRes,
      moduleSamplesRes
    ] = await Promise.all([
      dashboardAPI.getOverview(),
      hostAPI.getHostList(),
      hostAPI.getHostStatus(),
      dashboardAPI.getCpuTrend(6),
      dashboardAPI.getMemoryTrend(6),
      dashboardAPI.getDiskTrend(6),
      dashboardAPI.getNetworkTrend(6),
      dashboardAPI.getTopN(1, 10),
      dashboardAPI.getHourlyStats(),
      dashboardAPI.getAlarmStats(),
      alarmAPI.getAlarmList(20),
      dashboardAPI.getModuleSamples()
    ])
    
    overview.value = overviewRes.data.data
    hostDetail.value = hostListRes.data.data
    hostStatusData.value = hostStatusRes.data.data
    cpuTrend.value = cpuTrendRes.data.data
    memoryTrend.value = memoryTrendRes.data.data
    diskTrend.value = diskTrendRes.data.data
    networkTrend.value = networkTrendRes.data.data
    topNData.value = topNRes.data.data
    hourlyStats.value = hourlyStatsRes.data.data
    alarmStats.value = alarmStatsRes.data.data
    alarmList.value = alarmListRes.data.data
    moduleSamples.value = moduleSamplesRes.data.data
    dataLoaded.value++
  } catch (error) {
    console.error('加载数据失败:', error)
  }
}

onMounted(() => {
  loadData()
  refreshTimer = setInterval(() => {
    loadData()
  }, 30000)
})

onUnmounted(() => {
  if (refreshTimer) {
    clearInterval(refreshTimer)
  }
})
</script>

<style scoped>
.dashboard-container {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  padding: 20px;
  gap: 20px;
  background-color: var(--bg-color);
}

.stats-row {
  display: grid;
  grid-template-columns: repeat(6, 1fr);
  gap: 16px;
}

.main-content {
  flex: 1;
  display: grid;
  grid-template-columns: 1fr 1fr 0.85fr;
  gap: 16px;
  min-height: 0;
}

.left-panel,
.center-panel,
.right-panel {
  display: flex;
  flex-direction: column;
  gap: 16px;
  min-height: 0;
}

.left-panel > *,
.center-panel > *,
.right-panel > * {
  flex: 1;
  min-height: 0;
}

.right-panel > :not(.right-bottom) {
  min-height: 200px;
}

.right-bottom {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
  flex: 1;
  min-height: 220px;
}

.right-bottom > * {
  min-height: 0;
}

@media screen and (max-width: 1600px) {
  .dashboard-container {
    padding: 16px;
    gap: 16px;
  }
  .stats-row {
    gap: 12px;
  }
  .main-content {
    gap: 12px;
  }
}

@media screen and (max-width: 1440px) {
  .main-content {
    grid-template-columns: 1fr 1fr 0.9fr;
  }
}

@media screen and (max-width: 1366px) {
  .dashboard-container {
    padding: 12px;
    gap: 12px;
  }
  .stats-row {
    gap: 10px;
  }
  .main-content {
    gap: 10px;
    grid-template-columns: 1fr 1fr 1fr;
  }
}
</style>
