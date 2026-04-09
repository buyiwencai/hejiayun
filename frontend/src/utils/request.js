import axios from 'axios'
import { ElMessage } from 'element-plus'
import router from '../router'

const request = axios.create({
  baseURL: '/api',
  timeout: 10000
})

request.interceptors.request.use(
  config => {
    const username = localStorage.getItem('username')
    if (username) {
      config.headers['X-Username'] = username
    }
    const roleKey = localStorage.getItem('roleKey')
    if (roleKey) {
      config.headers['X-User-Role'] = roleKey
    }
    return config
  }
)

request.interceptors.response.use(
  response => {
    const res = response.data
    if (res.code === 403) {
      ElMessage.error(res.message || '无权限操作')
      return Promise.reject(res)
    }
    if (res.code !== 200) {
      ElMessage.error(res.message || '请求失败')
      return Promise.reject(res)
    }
    return res
  },
  error => {
    if (error.response && error.response.status === 403) {
      ElMessage.error(error.response.data.message || '无权限操作')
    } else {
      ElMessage.error(error.message || '网络请求失败')
    }
    return Promise.reject(error)
  }
)

export default request
