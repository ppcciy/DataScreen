import axios from 'axios'

const baseURL = '/api'

const api = axios.create({
  baseURL,
  timeout: 10000
})

export const dashboardAPI = {
  getOverview() {
    return api.get('/dashboard/overview')
  },
  
  getCpuTrend(hours = 6) {
    return api.get('/dashboard/cpuTrend', { params: { hours } })
  },
  
  getMemoryTrend(hours = 6) {
    return api.get('/dashboard/memoryTrend', { params: { hours } })
  },
  
  getDiskTrend(hours = 6) {
    return api.get('/dashboard/diskTrend', { params: { hours } })
  },
  
  getNetworkTrend(hours = 6) {
    return api.get('/dashboard/networkTrend', { params: { hours } })
  },
  
  getTopN(hours = 1, n = 10) {
    return api.get('/dashboard/top', { params: { hours, n } })
  },
  
  getHourlyStats() {
    return api.get('/dashboard/hourly')
  },
  
  getAlarmStats() {
    return api.get('/dashboard/alarmStats')
  },
  
  getModuleSamples() {
    return api.get('/module/samples')
  }
}

export const hostAPI = {
  getHostList() {
    return api.get('/host/list')
  },
  
  getHostStatus() {
    return api.get('/host/status')
  },
  
  getHostById(id) {
    return api.get(`/host/${id}`)
  }
}

export const alarmAPI = {
  getAlarmList(limit = 20) {
    return api.get('/alarm/list', { params: { limit } })
  }
}

export default api
