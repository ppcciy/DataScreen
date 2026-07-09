<template>
  <div class="chart-card">
    <div class="card-header">
      <h3 class="card-title">按小时统计</h3>
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
    type: Array,
    required: true
  }
})

const chartRef = ref(null)
let chartInstance = null

function initChart() {
  if (!chartRef.value) return
  chartInstance = echarts.init(chartRef.value)
  updateChart()
}

function updateChart() {
  if (!chartInstance) return
  
  const stats = props.data.map(item => ({
    label: `${item.date} ${item.hour}:00`,
    avg: parseFloat(item.cpuAvg) || 0,
    max: parseFloat(item.cpuMax) || 0,
    min: parseFloat(item.cpuMin) || 0,
    count: item.sampleCount || 0
  }))
  
  const option = {
    ...baseChartConfig,
    tooltip: {
      ...baseChartConfig.tooltip,
      formatter: function(params) {
        let result = `<div style="font-weight: 500; margin-bottom: 4px;">${params[0].name}</div>`
        params.forEach(p => {
          const unit = p.seriesName === '采样数' ? '' : '%'
          result += `<div>${p.seriesName}: <span style="color: ${p.color};">${p.value.toFixed(2)}${unit}</span></div>`
        })
        return result
      }
    },
    legend: {
      ...baseChartConfig.legend,
      show: true,
      top: 5,
      right: 5,
      left: 5,
      orient: 'horizontal',
      itemWidth: 12,
      itemHeight: 12,
      textStyle: {
        fontSize: 11
      }
    },
    grid: {
      top: 35,
      right: 15,
      bottom: 35,
      left: 45
    },
    xAxis: {
      ...baseChartConfig.xAxis,
      type: 'category',
      data: stats.map(s => s.label),
      axisLabel: {
        ...baseChartConfig.xAxis.axisLabel,
        fontSize: 10,
        rotate: 45
      }
    },
    yAxis: [
      {
        ...baseChartConfig.yAxis,
        type: 'value',
        min: 0,
        max: 100,
        axisLabel: {
          ...baseChartConfig.yAxis.axisLabel,
          formatter: '{value}%'
        }
      },
      {
        ...baseChartConfig.yAxis,
        type: 'value',
        axisLabel: {
          ...baseChartConfig.yAxis.axisLabel,
          formatter: '{value}'
        }
      }
    ],
    series: [
      {
        name: '平均CPU',
        type: 'line',
        smooth: true,
        symbol: 'circle',
        symbolSize: 6,
        data: stats.map(s => s.avg),
        lineStyle: {
          color: colors.chart.avg,
          width: 2
        },
        itemStyle: {
          color: colors.chart.avg,
          borderWidth: 2,
          borderColor: colors.card
        }
      },
      {
        name: '最大CPU',
        type: 'line',
        smooth: true,
        symbol: 'circle',
        symbolSize: 6,
        data: stats.map(s => s.max),
        lineStyle: {
          color: colors.chart.max,
          width: 2,
          type: 'dashed'
        },
        itemStyle: {
          color: colors.chart.max,
          borderWidth: 2,
          borderColor: colors.card
        }
      },
      {
        name: '采样数',
        type: 'line',
        yAxisIndex: 1,
        smooth: true,
        symbol: 'circle',
        symbolSize: 6,
        data: stats.map(s => s.count),
        lineStyle: {
          color: colors.chart.min,
          width: 2
        },
        itemStyle: {
          color: colors.chart.min,
          borderWidth: 2,
          borderColor: colors.card
        },
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: `${colors.chart.min}20` },
            { offset: 1, color: `${colors.chart.min}05` }
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
