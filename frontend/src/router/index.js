import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  { path: '/', redirect: '/login' },
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/Login.vue')
  },
  {
    path: '/dashboard',
    component: () => import('../views/Layout.vue'),
    redirect: '/dashboard/home',
    children: [
      {
        path: 'home',
        name: 'Home',
        component: () => import('../views/Home.vue'),
        meta: { title: '首页' }
      },
      {
        path: 'users',
        name: 'Users',
        component: () => import('../views/admin/UserManage.vue'),
        meta: { title: '用户管理', roles: ['admin'] }
      },
      {
        path: 'courses',
        name: 'Courses',
        component: () => import('../views/teacher/CourseManage.vue'),
        meta: { title: '课程管理', roles: ['admin', 'teacher'] }
      },
      {
        path: 'face-register',
        name: 'FaceRegister',
        component: () => import('../views/student/FaceRegister.vue'),
        meta: { title: '人脸注册', roles: ['student'] }
      },
      {
        path: 'attendance',
        name: 'Attendance',
        component: () => import('../views/teacher/AttendanceManage.vue'),
        meta: { title: '考勤管理', roles: ['admin', 'teacher'] }
      },
      {
        path: 'attendance-records',
        name: 'AttendanceRecords',
        component: () => import('../views/student/AttendanceRecords.vue'),
        meta: { title: '考勤记录', roles: ['student'] }
      },
      {
        path: 'statistics',
        name: 'Statistics',
        component: () => import('../views/teacher/Statistics.vue'),
        meta: { title: '数据统计', roles: ['admin', 'teacher'] }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  const user = JSON.parse(localStorage.getItem('user') || '{}')

  if (to.path !== '/login' && !token) {
    return next('/login')
  }

  if (to.meta.roles && !to.meta.roles.includes(user.role)) {
    ElMessage.error('无权访问该页面')
    return next('/dashboard/home')
  }

  document.title = to.meta.title ? `${to.meta.title} - 智能课堂考勤管理系统` : '智能课堂考勤管理系统'
  next()
})

export default router
