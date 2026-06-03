import axios from 'axios'
import { ElMessage } from 'element-plus'
import router from '../router'

const request = axios.create({
  baseURL: '/api',
  timeout: 30000
})

// 请求拦截器 - 添加token
request.interceptors.request.use(
  config => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  error => Promise.reject(error)
)

// 响应拦截器 - 统一错误处理
request.interceptors.response.use(
  response => {
    const res = response.data
    if (res.code === 401) {
      localStorage.removeItem('token')
      localStorage.removeItem('user')
      router.push('/login')
      ElMessage.error('登录已过期，请重新登录')
      return Promise.reject(res)
    }
    return res
  },
  error => {
    if (error.response?.status === 401) {
      localStorage.removeItem('token')
      localStorage.removeItem('user')
      router.push('/login')
    }
    ElMessage.error(error.response?.data?.msg || '请求失败')
    return Promise.reject(error)
  }
)

// ============ 认证相关 ============
export const login = (data) => request.post('/auth/login', data)
export const getUserInfo = () => request.get('/auth/info')

// ============ 用户管理 ============
export const listUsers = (params) => request.get('/user/list', { params })
export const addUser = (data) => request.post('/user/add', data)
export const updateUser = (data) => request.put('/user/update', data)
export const deleteUser = (id) => request.delete(`/user/delete/${id}`)
export const resetPassword = (id) => request.post(`/user/reset-password/${id}`)

// ============ 课程管理 ============
export const listCourses = (params) => request.get('/course/list', { params })
export const getCourse = (id) => request.get(`/course/${id}`)
export const addCourse = (data) => request.post('/course/add', data)
export const updateCourse = (data) => request.put('/course/update', data)
export const deleteCourse = (id) => request.delete(`/course/delete/${id}`)

// ============ 人脸识别 ============
export const registerFace = (userId, image) => {
  const formData = new FormData()
  formData.append('userId', userId)
  formData.append('image', image)
  return request.post('/face/register', formData, {
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}
export const startAttendance = (courseId) => request.post('/face/attendance/start', null, { params: { courseId } })
export const stopAttendance = (courseId) => request.post('/face/attendance/stop', null, { params: { courseId } })

// ============ 考勤记录 ============
export const listAttendance = (params) => request.get('/attendance/list', { params })
export const getCourseAttendance = (courseId, weekNum) => request.get(`/attendance/course/${courseId}`, { params: { weekNum } })
export const getAttendanceStats = (courseId) => request.get(`/attendance/statistics/${courseId}`)
export const getStudentStats = (courseId, studentId) => request.get('/attendance/statistics/student', { params: { courseId, studentId } })
export const exportAttendance = (courseId, weekNum) => {
  const token = localStorage.getItem('token')
  window.open(`/api/attendance/export?courseId=${courseId}&weekNum=${weekNum || ''}&token=${token}`, '_blank')
}

export default request
