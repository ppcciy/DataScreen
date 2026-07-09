<template>
  <div class="stat-card" @mouseenter="isHovered = true" @mouseleave="isHovered = false">
    <div class="card-icon" :style="{ backgroundColor: iconBg }">
      <span class="icon-text">{{ icon }}</span>
    </div>
    <div class="card-content">
      <div class="card-title">{{ title }}</div>
      <div class="card-value">
        {{ value }}
        <span v-if="unit" class="value-unit">{{ unit }}</span>
      </div>
      <div class="card-trend">
        <div class="trend-row">
          <span class="trend-item" :class="trendUp ? 'up' : 'down'">
            {{ trendUp ? '↑' : '↓' }}
            {{ trendPercent }}%
            <span class="trend-label">同比昨日</span>
          </span>
        </div>
        <div class="trend-row">
          <span class="trend-item" :class="hourTrendUp ? 'up' : 'down'">
            {{ hourTrendUp ? '↑' : '↓' }}
            {{ hourTrendPercent }}%
            <span class="trend-label">环比上一小时</span>
          </span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { colors } from '@/config/colors'

const props = defineProps({
  title: {
    type: String,
    required: true
  },
  value: {
    type: [String, Number],
    required: true
  },
  unit: {
    type: String,
    default: ''
  },
  icon: {
    type: String,
    default: '📊'
  },
  trendPercent: {
    type: String,
    default: '2.3'
  },
  trendUp: {
    type: Boolean,
    default: true
  },
  hourTrendPercent: {
    type: String,
    default: '1.5'
  },
  hourTrendUp: {
    type: Boolean,
    default: true
  },
  color: {
    type: String,
    default: colors.accent
  }
})

const isHovered = ref(false)

const iconBg = computed(() => {
  return `${props.color}15`
})
</script>

<style scoped>
.stat-card {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 20px;
  background: var(--card-bg);
  border-radius: var(--card-radius);
  border: 1px solid var(--border-color);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.04);
  transition: all 0.3s ease;
  cursor: default;
}

.stat-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.08);
}

.card-icon {
  width: 56px;
  height: 56px;
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.icon-text {
  font-size: 28px;
}

.card-content {
  flex: 1;
  min-width: 0;
}

.card-title {
  font-size: 13px;
  color: var(--text-secondary);
  margin-bottom: 8px;
}

.card-value {
  font-size: 28px;
  font-weight: 700;
  color: var(--title-color);
  display: flex;
  align-items: baseline;
  gap: 4px;
}

.value-unit {
  font-size: 14px;
  font-weight: 500;
  color: var(--text-secondary);
}

.card-trend {
  display: flex;
  flex-direction: column;
  gap: 4px;
  margin-top: 8px;
}

.trend-row {
  display: flex;
  align-items: center;
}

.trend-item {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 11px;
}

.trend-item.up {
  color: var(--success-color);
}

.trend-item.down {
  color: var(--danger-color);
}

.trend-label {
  color: var(--text-secondary);
}

@media screen and (max-width: 1600px) {
  .stat-card {
    padding: 16px;
    gap: 12px;
  }
  .card-icon {
    width: 48px;
    height: 48px;
  }
  .icon-text {
    font-size: 24px;
  }
  .card-value {
    font-size: 24px;
  }
}

@media screen and (max-width: 1366px) {
  .card-value {
    font-size: 20px;
  }
}
</style>
