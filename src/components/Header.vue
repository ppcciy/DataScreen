<template>
  <header class="dashboard-header">
    <div class="header-left">
      <h1 class="header-title">数据中心监控平台</h1>
    </div>
    <div class="header-right">
      <div class="header-item">
       
      </div>
      <div class="header-item">
        <span class="item-label">自动刷新</span>
        <span class="item-value" :class="{ active: autoRefresh }">
          {{ autoRefresh ? '开启' : '关闭' }}
        </span>
      </div>
      <div class="header-item">
        <span class="item-label">{{ weekDays[weekDay] }}</span>
        <span class="item-value">{{ currentDate }}</span>
      </div>
      <div class="header-item time-item">
        <span class="item-value time">{{ currentTime }}</span>
      </div>
    </div>
  </header>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'

const currentDate = ref('')
const currentTime = ref('')
const weekDay = ref(0)
const autoRefresh = ref(true)

const weekDays = ['星期日', '星期一', '星期二', '星期三', '星期四', '星期五', '星期六']

let timer = null

function updateTime() {
  const now = new Date()
  const year = now.getFullYear()
  const month = String(now.getMonth() + 1).padStart(2, '0')
  const day = String(now.getDate()).padStart(2, '0')
  const hour = String(now.getHours()).padStart(2, '0')
  const minute = String(now.getMinutes()).padStart(2, '0')
  const second = String(now.getSeconds()).padStart(2, '0')
  
  currentDate.value = `${year}-${month}-${day}`
  currentTime.value = `${hour}:${minute}:${second}`
  weekDay.value = now.getDay()
}

onMounted(() => {
  updateTime()
  timer = setInterval(updateTime, 1000)
})

onUnmounted(() => {
  if (timer) {
    clearInterval(timer)
  }
})
</script>

<style scoped>
.dashboard-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 24px;
  background: var(--card-bg);
  border-radius: var(--card-radius);
  border: 1px solid var(--border-color);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.04);
}

.header-left {
  display: flex;
  align-items: center;
}

.header-title {
  font-size: 20px;
  font-weight: 600;
  color: var(--title-color);
  margin: 0;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 32px;
}

.header-item {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
}

.item-label {
  font-size: 12px;
  color: var(--text-secondary);
  margin-bottom: 4px;
}

.item-value {
  font-size: 14px;
  font-weight: 500;
  color: var(--text-color);
}

.item-value.active {
  color: var(--success-color);
}

.time-item {
  padding-left: 20px;
  border-left: 1px solid var(--border-color);
}

.time-item .item-value {
  font-size: 20px;
  font-weight: 600;
  color: var(--title-color);
}

@media screen and (max-width: 1440px) {
  .header-right {
    gap: 20px;
  }
  .header-title {
    font-size: 18px;
  }
}
</style>
