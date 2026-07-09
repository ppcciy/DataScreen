<template>
  <div class="chart-card">
    <div class="card-header">
      <h3 class="card-title">{{ title }}</h3>
    </div>
    <div ref="chartRef" class="chart-container"></div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, watch, computed } from 'vue'
import * as echarts from 'echarts'
import { colors } from '@/config/colors'

const props = defineProps({
  data: {
    type: Array,
    required: true
  },
  title: {
    type: String,
    required: true
  },
  color: {
    type: String,
    default: colors.accent
  },
  unit: {
    type: String,
    default: '%'
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
  
  const data = props.data.map(item => ({
    name: item.hostname ? item.hostname.split('.')[0] : (item.hostId || 'Unknown'),
    value: parseFloat(item.value) || 0
  }))
  
  const option = {
    backgroundColor: 'transparent',
    tooltip: {
      backgroundColor: colors.card,
      borderColor: colors.border,
      borderWidth: 1,
      textStyle: {
        color: colors.text
      },
      trigger: 'axis',
      axisPointer: {
        type: 'shadow'
      },
      formatter: function(params) {
        const data = params[0]
        return `<div style="font-weight: 500; margin-bottom: 4px;">${data.name}</div>
                <div>平均使用率: <span style="color: ${props.color};">${data.value.toFixed(2)}${props.unit}</span></div>`
      }
    },
    grid: {
      top: 10,
      right: 60,
      bottom: 10,
      left: 80
    },
    xAxis: {
      type: 'value',
      show: false,
      max: 100
    },
    yAxis: {
      type: 'category',
      data: data.map(d => d.name),
      inverse: true,
      axisLine: {
        show: false
      },
      axisTick: {
        show: false
      },
      axisLabel: {
        color: colors.text,
        fontSize: 11,
        width: 70,
        overflow: 'truncate'
      }
    },
    series: [{
      type: 'bar',
      data: data.map(d => d.value),
      barWidth: '40%',
      itemStyle: {
        color: new echarts.graphic.LinearGradient(0, 0, 1, 0, [
          { offset: 0, color: props.color },
          { offset: 1, color: `${props.color}60` }
        ]),
        borderRadius: [0, 4, 4, 0]
      },
      label: {
        show: true,
        position: 'right',
        formatter: `{c}${props.unit}`,
        color: colors.textSecondary,
        fontSize: 10
      }
    }]
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
