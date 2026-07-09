<template>
  <div class="chart-card">
    <div class="card-header">
      <h3 class="card-title">CPU利用率趋势</h3>
      <div class="time-range">
        <button 
          v-for="range in timeRanges" 
          :key="range.value"
          :class="{ active: selectedRange === range.value }"
          @click="handleRangeChange(range.value)"
        >
          {{ range.label }}
        </button>
      </div>
    </div>
    <div ref="chartRef" class="chart-container"></div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, watch } from 'vue'
import * as echarts from 'echarts'
import { baseChartConfig } from '@/config/chartConfig'
import { colors } from '@/config/colors'
import axios from 'axios'

const chartRef = ref(null)
let chartInstance = null
const selectedRange = ref('1h')
const chartData = ref([])

const timeRanges = [
  { label: '最近1小时', value: '1h' },
  { label: '最近6小时', value: '6h' },
  { label: '最近24小时', value: '24h' }
]

function formatTime(datetime) {
  if (!datetime) return ''
  const dt = new Date(datetime)
  return `${dt.getHours().toString().padStart(2, '0')}:${dt.getMinutes().toString().padStart(2, '0')}`
}

async function fetchData(hours) {
  try {
    const response = await axios.get('/api/dashboard/cpuTrend', { params: { hours } })
    if (response.data.code === 200) {
      chartData.value = response.data.data.map(item => ({
        time: item.datetime ? formatTime(item.datetime) : '',
        value: parseFloat(item.value) || 0
      }))
      updateChart()
    }
  } catch (error) {
    console.error('获取CPU趋势数据失败:', error)
  }
}

function handleRangeChange(value) {
  selectedRange.value = value
  const hours = parseInt(value.replace('h', ''))
  fetchData(hours)
}

function initChart() {
  if (!chartRef.value) return
  chartInstance = echarts.init(chartRef.value)
  updateChart()
}

function updateChart() {
  if (!chartInstance) return
  
  const data = chartData.value
  
  const option = {
    ...baseChartConfig,
    tooltip: {
      ...baseChartConfig.tooltip,
      formatter: function(params) {
        const data = params[0]
        return `<div style="font-weight: 500; margin-bottom: 4px;">${data.name}</div>
                <div>CPU使用率: <span style="color: ${colors.chart.cpu};">${data.value.toFixed(2)}%</span></div>`
      }
    },
    grid: {
      top: 30,
      right: 20,
      bottom: 40,
      left: 50
    },
    xAxis: {
      ...baseChartConfig.xAxis,
      type: 'category',
      data: data.map(s => s.time)
    },
    yAxis: {
      ...baseChartConfig.yAxis,
      type: 'value',
      min: 0,
      max: 100,
      axisLabel: {
        ...baseChartConfig.yAxis.axisLabel,
        formatter: '{value}%'
      }
    },
    series: [{
      name: 'CPU使用率',
      type: 'line',
      smooth: true,
      symbol: 'circle',
      symbolSize: 6,
      data: data.map(s => s.value),
      lineStyle: {
        color: colors.chart.cpu,
        width: 2
      },
      itemStyle: {
        color: colors.chart.cpu,
        borderWidth: 2,
        borderColor: colors.card
      },
      areaStyle: {
        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
          { offset: 0, color: `${colors.chart.cpu}30` },
          { offset: 1, color: `${colors.chart.cpu}05` }
        ])
      }
    }]
  }
  
  chartInstance.setOption(option)
}

function handleResize() {
  chartInstance?.resize()
}

onMounted(() => {
  initChart()
  fetchData(6)
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  chartInstance?.dispose()
})
</script>

<style scoped>
.chart-card {
  background: var(--card-bg);
  border-radius: var(--card-radius);
  border: 1px solid var(--border-color);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.04);
  padding: 20px;
  height: 100%;
  display: flex;
  flex-direction: column;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.card-title {
  font-size: 16px;
  font-weight: 600;
  color: var(--title-color);
  margin: 0;
}

.time-range {
  display: flex;
  gap: 8px;
}

.time-range button {
  padding: 6px 14px;
  font-size: 12px;
  color: var(--text-secondary);
  background: transparent;
  border: 1px solid var(--border-color);
  border-radius: 20px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.time-range button:hover {
  border-color: var(--accent-color);
  color: var(--accent-color);
}

.time-range button.active {
  background: var(--accent-color);
  border-color: var(--accent-color);
  color: #fff;
}

.chart-container {
  flex: 1;
  min-height: 0;
}
</style>
