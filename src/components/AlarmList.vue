<template>
  <div class="chart-card">
    <div class="card-header">
      <h3 class="card-title">最近告警列表</h3>
      <span class="alarm-count">共 {{ alarmData.length }} 条</span>
    </div>
    <div class="alarm-container" ref="alarmContainer">
      <div 
        v-for="(alarm, index) in displayAlarms" 
        :key="alarm.id || index" 
        class="alarm-item"
        :class="getLevelClass(alarm.level)"
      >
        <div class="alarm-time">{{ formatTime(alarm.time) }}</div>
        <div class="alarm-info">
          <div class="alarm-host">{{ alarm.hostname ? alarm.hostname.split('.')[0] : alarm.host }}</div>
          <div class="alarm-metric">{{ alarm.metric }}</div>
        </div>
        <div class="alarm-value">{{ alarm.value }}</div>
        <el-tag :type="getTagType(alarm.level)" size="small">
          {{ getLevelLabel(alarm.level) }}
        </el-tag>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'

const props = defineProps({
  data: {
    type: Array,
    required: true
  }
})

const alarmContainer = ref(null)
const currentPage = ref(0)
const pageSize = 6
let scrollTimer = null

const alarmData = computed(() => {
  return props.data
})

const displayAlarms = computed(() => {
  const start = currentPage.value * pageSize
  return alarmData.value.slice(start, start + pageSize)
})

function startScroll() {
  if (alarmData.value.length <= pageSize) return
  scrollTimer = setInterval(() => {
    const maxPage = Math.ceil(alarmData.value.length / pageSize)
    currentPage.value = (currentPage.value + 1) % maxPage
  }, 4000)
}

function stopScroll() {
  if (scrollTimer) {
    clearInterval(scrollTimer)
    scrollTimer = null
  }
}

function formatTime(ts) {
  const date = new Date(ts)
  const hour = String(date.getHours()).padStart(2, '0')
  const minute = String(date.getMinutes()).padStart(2, '0')
  const second = String(date.getSeconds()).padStart(2, '0')
  return `${hour}:${minute}:${second}`
}

function getLevelClass(level) {
  if (level === 'Critical') return 'danger'
  if (level === 'Warning') return 'warning'
  return level || ''
}

function getTagType(level) {
  if (level === 'Critical') return 'danger'
  if (level === 'Warning') return 'warning'
  return 'info'
}

function getLevelLabel(level) {
  const labels = {
    Critical: '严重',
    Warning: '警告',
    danger: '严重',
    warning: '警告'
  }
  return labels[level] || level
}

onMounted(() => {
  startScroll()
})

onUnmounted(() => {
  stopScroll()
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
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-title {
  font-size: 16px;
  font-weight: 600;
  color: var(--title-color);
  margin: 0;
}

.alarm-count {
  font-size: 12px;
  color: var(--text-secondary);
}

.alarm-container {
  flex: 1;
  overflow-y: auto;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.alarm-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px;
  background: var(--bg-color);
  border-radius: 10px;
  border-left: 3px solid transparent;
  transition: all 0.3s ease;
}

.alarm-item:hover {
  background: rgba(125, 162, 255, 0.05);
}

.alarm-item.danger {
  border-left-color: var(--danger-color);
}

.alarm-item.warning {
  border-left-color: var(--warning-color);
}

.alarm-time {
  font-size: 12px;
  color: var(--text-secondary);
  font-family: monospace;
  min-width: 50px;
}

.alarm-info {
  flex: 1;
  min-width: 0;
}

.alarm-host {
  font-size: 13px;
  font-weight: 500;
  color: var(--title-color);
}

.alarm-metric {
  font-size: 11px;
  color: var(--text-secondary);
  margin-top: 2px;
}

.alarm-value {
  font-size: 13px;
  font-weight: 500;
  color: var(--text);
  min-width: 80px;
  text-align: right;
}

@media screen and (max-width: 1366px) {
  .alarm-item {
    padding: 10px;
    gap: 10px;
  }
  .alarm-host {
    font-size: 12px;
  }
  .alarm-metric {
    font-size: 10px;
  }
}
</style>
