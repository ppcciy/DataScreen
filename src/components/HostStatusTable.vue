<template>
  <div class="chart-card">
    <div class="card-header">
      <h3 class="card-title">主机状态</h3>
      <span class="row-count">共 {{ hostData.length }} 台</span>
    </div>
    <div class="table-container" ref="tableContainer">
      <el-table :data="displayData" :row-class-name="rowClassName" @row-click="handleRowClick" height="100%">
        <el-table-column prop="hostname" label="主机名" width="120">
          <template #default="scope">
            <span class="hostname">{{ scope.row.hostname.split('.')[0] }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="ip" label="IP" width="90">
          <template #default="scope">
            <span>{{ scope.row.ip || '-' }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="cpu" label="CPU" width="70">
          <template #default="scope">
            <div class="metric-cell">
              <span :class="getMetricClass(parseFloat(scope.row.cpu))">{{ scope.row.cpu }}%</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="memory" label="Memory" width="90">
          <template #default="scope">
            <span>{{ scope.row.memory }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="disk" label="Disk" width="70">
          <template #default="scope">
            <div class="metric-cell">
              <span :class="getMetricClass(parseFloat(scope.row.disk))">{{ scope.row.disk }}%</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="network" label="Network" width="100">
          <template #default="scope">
            <span>{{ scope.row.network }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="60">
          <template #default="scope">
            <el-tag :type="getStatusType(scope.row.status)" size="small">
              {{ getStatusLabel(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
      </el-table>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { colors } from '@/config/colors'

const props = defineProps({
  data: {
    type: Array,
    required: true
  }
})

const tableContainer = ref(null)
const currentPage = ref(0)
const pageSize = 8
let scrollTimer = null

const hostData = computed(() => {
  return props.data.map(item => ({
    hostname: item.hostName || item.hostname || '',
    ip: item.ipAddress || item.ip || '',
    cpu: parseFloat(item.cpuUsage || item.cpu || 0).toFixed(1),
    memory: item.memUsed ? (item.memUsed / 1024).toFixed(1) + ' GB' : '-',
    disk: '-',
    network: '-',
    status: 'normal'
  }))
})

const displayData = computed(() => {
  const start = currentPage.value * pageSize
  return hostData.value.slice(start, start + pageSize)
})

function startScroll() {
  if (hostData.value.length <= pageSize) return
  scrollTimer = setInterval(() => {
    const maxPage = Math.ceil(hostData.value.length / pageSize)
    currentPage.value = (currentPage.value + 1) % maxPage
  }, 5000)
}

function stopScroll() {
  if (scrollTimer) {
    clearInterval(scrollTimer)
    scrollTimer = null
  }
}

function getMetricClass(value) {
  if (value > 90) return 'danger'
  if (value > 80) return 'warning'
  return 'normal'
}

function getStatusType(status) {
  const types = {
    normal: 'success',
    warning: 'warning',
    danger: 'danger'
  }
  return types[status] || 'info'
}

function getStatusLabel(status) {
  const labels = {
    normal: '正常',
    warning: '告警',
    danger: '严重'
  }
  return labels[status] || status
}

function rowClassName({ row }) {
  return row.status
}

function handleRowClick(row) {
  console.log('点击主机:', row)
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

.row-count {
  font-size: 12px;
  color: var(--text-secondary);
}

.table-container {
  flex: 1;
  overflow: hidden;
}

.hostname {
  font-weight: 500;
  color: var(--title-color);
}

.metric-cell span {
  font-weight: 500;
}

.metric-cell span.normal {
  color: var(--success-color);
}

.metric-cell span.warning {
  color: var(--warning-color);
}

.metric-cell span.danger {
  color: var(--danger-color);
}

.el-table .danger {
  background-color: rgba(240, 138, 138, 0.05);
}

.el-table .warning {
  background-color: rgba(246, 178, 107, 0.05);
}

.el-table th,
.el-table td {
  padding: 10px 12px;
  font-size: 13px;
}

.el-table th {
  font-weight: 500;
}

@media screen and (max-width: 1366px) {
  .el-table th,
  .el-table td {
    padding: 8px 10px;
    font-size: 12px;
  }
}
</style>
