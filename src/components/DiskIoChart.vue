<template>
  <div class="chart-card">
    <div class="card-header">
      <h3 class="card-title">磁盘IO</h3>
      <div class="io-tabs">
        <button 
          v-for="tab in ioTabs" 
          :key="tab.value"
          :class="{ active: selectedTab === tab.value }"
          @click="selectedTab = tab.value"
        >
          {{ tab.label }}
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

const props = defineProps({
  data: {
    type: Object,
    required: true
  }
})

const chartRef = ref(null)
let chartInstance = null
const selectedTab = ref('both')

const ioTabs = [
  { label: '读', value: 'read' },
  { label: '写', value: 'write' },
  { label: '读写对比', value: 'both' }
]

function formatTime(datetime) {
  if (!datetime) return ''
  const dt = new Date(datetime)
  return `${dt.getHours().toString().padStart(2, '0')}:${dt.getMinutes().toString().padStart(2, '0')}`
}

function initChart() {
  if (!chartRef.value) return
  chartInstance = echarts.init(chartRef.value)
  updateChart()
}

function updateChart() {
  if (!chartInstance) return
  
  const readData = (props.data.read || []).map(item => ({
    time: formatTime(item.datetime),
    value: parseFloat(item.value) || 0
  }))
  const writeData = (props.data.write || []).map(item => ({
    time: formatTime(item.datetime),
    value: parseFloat(item.value) || 0
  }))
  
  const allTimes = [...new Set([...readData.map(d => d.time), ...writeData.map(d => d.time)])].sort()
  
  const getAlignedData = (sourceData) => {
    return allTimes.map(time => {
      const item = sourceData.find(d => d.time === time)
      return item ? item.value : 0
    })
  }
  
  const alignedReadData = getAlignedData(readData)
  const alignedWriteData = getAlignedData(writeData)
  
  const series = []
  
  if (selectedTab.value === 'read' || selectedTab.value === 'both') {
    series.push({
      name: '读',
      type: 'bar',
      data: alignedReadData,
      itemStyle: {
        color: colors.chart.networkIn,
        borderRadius: [4, 4, 0, 0]
      }
    })
  }
  
  if (selectedTab.value === 'write' || selectedTab.value === 'both') {
    series.push({
      name: '写',
      type: 'bar',
      data: alignedWriteData,
      itemStyle: {
        color: colors.chart.networkOut,
        borderRadius: [4, 4, 0, 0]
      }
    })
  }
  
  const option = {
    ...baseChartConfig,
    tooltip: {
      ...baseChartConfig.tooltip,
      trigger: 'axis',
      axisPointer: {
        type: 'shadow'
      },
      formatter: function(params) {
        let result = `<div style="font-weight: 500; margin-bottom: 4px;">${params[0].name}</div>`
        params.forEach(p => {
          result += `<div>${p.seriesName}: <span style="color: ${p.color};">${p.value.toFixed(0)} sectors/s</span></div>`
        })
        return result
      }
    },
    legend: {
      ...baseChartConfig.legend,
      show: selectedTab.value === 'both',
      top: 0
    },
    grid: {
      top: selectedTab.value === 'both' ? 40 : 30,
      right: 20,
      bottom: 40,
      left: 50
    },
    xAxis: {
      ...baseChartConfig.xAxis,
      type: 'category',
      data: allTimes
    },
    yAxis: {
      ...baseChartConfig.yAxis,
      type: 'value',
      axisLabel: {
        ...baseChartConfig.yAxis.axisLabel,
        formatter: '{value}'
      }
    },
    series
  }
  
  chartInstance.setOption(option)
}

function handleResize() {
  chartInstance?.resize()
}

watch(() => props.data, updateChart, { deep: true })
watch(selectedTab, updateChart)

onMounted(() => {
  initChart()
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

.io-tabs {
  display: flex;
  gap: 8px;
}

.io-tabs button {
  padding: 6px 14px;
  font-size: 12px;
  color: var(--text-secondary);
  background: transparent;
  border: 1px solid var(--border-color);
  border-radius: 20px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.io-tabs button:hover {
  border-color: var(--accent-color);
  color: var(--accent-color);
}

.io-tabs button.active {
  background: var(--accent-color);
  border-color: var(--accent-color);
  color: #fff;
}

.chart-container {
  flex: 1;
  min-height: 0;
}
</style>
