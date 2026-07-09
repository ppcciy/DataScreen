<template>
  <div class="chart-card">
    <div class="card-header">
      <h3 class="card-title">告警统计</h3>
    </div>
    <div ref="chartRef" class="chart-container"></div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, watch } from 'vue'
import * as echarts from 'echarts'
import { colors } from '@/config/colors'

const props = defineProps({
  data: {
    type: Object,
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
  
  const warningCount = (props.data['CPU>80%'] || 0) + (props.data['Memory>80%'] || 0) + (props.data['Network异常'] || 0)
  const criticalCount = props.data['Disk>90%'] || 0
  const data = [
    { name: 'Warning', value: warningCount, color: colors.warning },
    { name: 'Critical', value: criticalCount, color: colors.danger }
  ].filter(item => item.value > 0)
  
  const option = {
    backgroundColor: 'transparent',
    tooltip: {
      backgroundColor: colors.card,
      borderColor: colors.border,
      borderWidth: 1,
      textStyle: {
        color: colors.text
      },
      trigger: 'item',
      formatter: function(params) {
        return `<div style="font-weight: 500; margin-bottom: 4px;">${params.name}</div>
                <div>告警数: <span style="color: ${params.color};">${params.value}</span></div>
                <div>占比: ${params.percent.toFixed(1)}%</div>`
      }
    },
    series: [{
      type: 'pie',
      radius: ['45%', '72%'],
      center: ['50%', '50%'],
      avoidLabelOverlap: false,
      itemStyle: {
        borderRadius: 6,
        borderColor: colors.card,
        borderWidth: 2
      },
      label: {
        show: true,
        position: 'inside',
        formatter: '{c}',
        color: colors.title,
        fontSize: 14,
        fontWeight: 'bold'
      },
      emphasis: {
        label: {
          show: true,
          fontSize: 16,
          fontWeight: 'bold',
          formatter: '{b}\n{c}'
        }
      },
      labelLine: {
        show: false
      },
      data: data.map(item => ({
        ...item,
        itemStyle: {
          color: item.color
        }
      }))
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
