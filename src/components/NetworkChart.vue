<template>
  <div class="chart-card">
    <div class="card-header">
      <h3 class="card-title">网络流量</h3>
    </div>
    <div ref="chartRef" class="chart-container"></div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, watch, computed } from 'vue'
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
  
  const inbound = (props.data.inbound || []).map(item => ({
    time: formatTime(item.datetime),
    value: parseFloat(item.value) || 0
  }))
  const outbound = (props.data.outbound || []).map(item => ({
    time: formatTime(item.datetime),
    value: parseFloat(item.value) || 0
  }))
  
  const labels = inbound.map(s => s.time)
  
  const option = {
    ...baseChartConfig,
    tooltip: {
      ...baseChartConfig.tooltip,
      formatter: function(params) {
        let result = `<div style="font-weight: 500; margin-bottom: 4px;">${params[0].name}</div>`
        params.forEach(p => {
          result += `<div>${p.seriesName}: <span style="color: ${p.color};">${p.value.toFixed(2)} MB/s</span></div>`
        })
        return result
      }
    },
    legend: {
      ...baseChartConfig.legend,
      show: true,
      top: 0
    },
    grid: {
      top: 40,
      right: 20,
      bottom: 45,
      left: 70
    },
    xAxis: {
      ...baseChartConfig.xAxis,
      type: 'category',
      data: labels
    },
    yAxis: {
      ...baseChartConfig.yAxis,
      type: 'value',
      axisLabel: {
        ...baseChartConfig.yAxis.axisLabel,
        formatter: '{value} MB/s'
      }
    },
    series: [
      {
        name: 'Inbound',
        type: 'line',
        smooth: true,
        symbol: 'circle',
        symbolSize: 6,
        data: inbound.map(s => s.value),
        lineStyle: {
          color: colors.chart.networkIn,
          width: 2
        },
        itemStyle: {
          color: colors.chart.networkIn,
          borderWidth: 2,
          borderColor: colors.card
        },
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: `${colors.chart.networkIn}20` },
            { offset: 1, color: `${colors.chart.networkIn}05` }
          ])
        }
      },
      {
        name: 'Outbound',
        type: 'line',
        smooth: true,
        symbol: 'circle',
        symbolSize: 6,
        data: outbound.map(s => s.value),
        lineStyle: {
          color: colors.chart.networkOut,
          width: 2
        },
        itemStyle: {
          color: colors.chart.networkOut,
          borderWidth: 2,
          borderColor: colors.card
        },
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: `${colors.chart.networkOut}20` },
            { offset: 1, color: `${colors.chart.networkOut}05` }
          ])
        }
      }
    ]
  }
  
  chartInstance.setOption(option)
}

function handleResize() {
  chartInstance?.resize()
}

watch(() => props.data, updateChart, { deep: true })

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
  margin-bottom: 16px;
}

.card-title {
  font-size: 16px;
  font-weight: 600;
  color: var(--title-color);
  margin: 0;
}

.chart-container {
  flex: 1;
  min-height: 0;
}
</style>
