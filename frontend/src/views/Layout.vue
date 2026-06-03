<template>
  <div class="layout-container">
    <header class="layout-header">
      <div class="logo" @click="router.push('/dashboard')" style="cursor:pointer">
        <el-icon :size="22"><Camera /></el-icon>
        智能课堂考勤管理系统
      </div>
      <div class="user-info">
        <el-tag :type="roleTagType" size="small">{{ roleLabel }}</el-tag>
        <span>{{ user?.realName }}</span>
        <el-button text type="primary" @click="handleLogout">退出</el-button>
      </div>
    </header>

    <div class="layout-body">
      <aside class="layout-sidebar">
        <el-menu :default-active="currentRoute" router :collapse="false">
          <el-menu-item index="/dashboard/home">
            <el-icon><HomeFilled /></el-icon>
            <span>首页</span>
          </el-menu-item>

          <!-- 管理员菜单 -->
          <el-menu-item v-if="user?.role === 'admin'" index="/dashboard/users">
            <el-icon><UserFilled /></el-icon>
            <span>用户管理</span>
          </el-menu-item>

          <!-- 教师/管理员菜单 -->
          <template v-if="user?.role === 'admin' || user?.role === 'teacher'">
            <el-menu-item index="/dashboard/courses">
              <el-icon><Reading /></el-icon>
              <span>课程管理</span>
            </el-menu-item>
            <el-menu-item index="/dashboard/attendance">
              <el-icon><VideoCamera /></el-icon>
              <span>考勤管理</span>
            </el-menu-item>
            <el-menu-item index="/dashboard/statistics">
              <el-icon><DataBoard /></el-icon>
              <span>数据统计</span>
            </el-menu-item>
          </template>

          <!-- 学生菜单 -->
          <template v-if="user?.role === 'student'">
            <el-menu-item index="/dashboard/face-register">
              <el-icon><Picture /></el-icon>
              <span>人脸注册</span>
            </el-menu-item>
            <el-menu-item index="/dashboard/attendance-records">
              <el-icon><List /></el-icon>
              <span>考勤记录</span>
            </el-menu-item>
          </template>
        </el-menu>
      </aside>

      <main class="layout-content">
        <router-view />
      </main>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessageBox } from 'element-plus'

const router = useRouter()
const route = useRoute()

const user = JSON.parse(localStorage.getItem('user') || '{}')

const currentRoute = computed(() => route.path)

const roleLabel = computed(() => {
  const map = { admin: '管理员', teacher: '教师', student: '学生' }
  return map[user?.role] || '未知'
})

const roleTagType = computed(() => {
  const map = { admin: 'danger', teacher: 'warning', student: 'success' }
  return map[user?.role] || 'info'
})

const handleLogout = () => {
  ElMessageBox.confirm('确定要退出登录吗？', '提示').then(() => {
    localStorage.removeItem('token')
    localStorage.removeItem('user')
    router.push('/login')
  }).catch(() => {})
}
</script>
