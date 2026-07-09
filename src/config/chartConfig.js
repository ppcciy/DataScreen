import { colors } from './colors'

export const baseChartConfig = {
  backgroundColor: 'transparent',
  animationDuration: 1500,
  animationEasing: 'cubicOut',
  grid: {
    top: 40,
    right: 30,
    bottom: 40,
    left: 50
  },
  tooltip: {
    backgroundColor: colors.card,
    borderColor: colors.border,
    borderWidth: 1,
    textStyle: {
      color: colors.text
    },
    trigger: 'axis'
  },
  legend: {
    textStyle: {
      color: colors.textSecondary,
      fontSize: 12
    }
  },
  xAxis: {
    axisLine: {
      lineStyle: {
        color: colors.border
      }
    },
    axisTick: {
      show: false
    },
    axisLabel: {
      color: colors.textSecondary,
      fontSize: 11
    }
  },
  yAxis: {
    axisLine: {
      show: false
    },
    axisTick: {
      show: false
    },
    axisLabel: {
      color: colors.textSecondary,
      fontSize: 11
    },
    splitLine: {
      lineStyle: {
        color: colors.border,
        type: 'dashed'
      }
    }
  }
}

export const lineChartConfig = {
  ...baseChartConfig,
  series: [{
    type: 'line',
    smooth: true,
    symbol: 'circle',
    symbolSize: 6,
    lineStyle: {
      width: 2
    },
    itemStyle: {
      borderWidth: 2,
      borderColor: colors.card
    }
  }]
}

export const areaChartConfig = {
  ...baseChartConfig,
  series: [{
    type: 'line',
    smooth: true,
    symbol: 'circle',
    symbolSize: 6,
    lineStyle: {
      width: 2
    },
    itemStyle: {
      borderWidth: 2,
      borderColor: colors.card
    },
    areaStyle: {
      opacity: 0.15
    }
  }]
}

export const barChartConfig = {
  ...baseChartConfig,
  grid: {
    top: 40,
    right: 30,
    bottom: 40,
    left: 50
  },
  series: [{
    type: 'bar',
    barWidth: '50%',
    itemStyle: {
      borderRadius: [4, 4, 0, 0]
    }
  }]
}

export const pieChartConfig = {
  backgroundColor: 'transparent',
  animationDuration: 1500,
  animationEasing: 'cubicOut',
  tooltip: {
    backgroundColor: colors.card,
    borderColor: colors.border,
    borderWidth: 1,
    textStyle: {
      color: colors.text
    },
    trigger: 'item',
    formatter: '{b}: {c} ({d}%)'
  },
  legend: {
    orient: 'vertical',
    right: 10,
    top: 'center',
    textStyle: {
      color: colors.textSecondary,
      fontSize: 12
    }
  }
}

export const gaugeChartConfig = {
  backgroundColor: 'transparent',
  animationDuration: 1500,
  animationEasing: 'cubicOut',
  series: [{
    type: 'gauge',
    startAngle: 200,
    endAngle: -20,
    min: 0,
    max: 100,
    splitNumber: 5,
    axisLine: {
      lineStyle: {
        width: 12,
        color: [
          [0.3, colors.success],
          [0.7, colors.warning],
          [1, colors.danger]
        ]
      }
    },
    pointer: {
      itemStyle: {
        color: colors.accent
      },
      length: '60%'
    },
    axisTick: {
      show: false
    },
    splitLine: {
      show: false
    },
    axisLabel: {
      color: colors.textSecondary,
      fontSize: 10,
      distance: 15
    },
    title: {
      show: false
    },
    detail: {
      valueAnimation: true,
      formatter: '{value}%',
      color: colors.title,
      fontSize: 24,
      fontWeight: 'bold',
      offsetCenter: [0, '60%']
    }
  }]
}
