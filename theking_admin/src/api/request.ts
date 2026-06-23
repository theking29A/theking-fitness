import axios from 'axios'
import { useUserStore } from '@/stores/user'

const request = axios.create({
  baseURL: '/api',
  timeout: 15000
})

request.interceptors.request.use((config) => {
  const userStore = useUserStore()
  if (userStore.token && config.params) {
    config.params.token = userStore.token
  } else if (userStore.token && !config.params) {
    config.params = { token: userStore.token }
  }
  return config
})

request.interceptors.response.use(
  (res) => res.data,
  (err) => {
    if (err.response?.status === 401) {
      const userStore = useUserStore()
      userStore.logout()
      window.location.hash = '#/login'
    }
    return Promise.reject(err)
  }
)

export default request
