<template>
  <div>
    <h2 style="margin-bottom:24px;color:#303133;">欢迎回来，{{ user?.realName }}</h2>

    <div class="stat-cards">
      <div class="stat-card">
        <div class="label">我的角色</div>
        <div class="value" :style="{ color: roleColor }">{{ roleLabel }}</div>
      </div>
      <div class="stat-card" v-if="user?.role === 'student'">
        <div class="label">人脸状态</div>
        <div class="value" :style="{ color: user?.faceRegistered ? '#67c23a' : '#e6a23c' }">
          {{ user?.faceRegistered ? '已注册' : '未注册' }}
        </div>
      </div>
      <div class="stat-card" v-if="user?.role === 'teacher' || user?.role === 'admin'">
        <div class="label">待处理考勤</div>
        <div class="value" style="color:#409eff;">{{ todayAttendanceCount }}</div>
      </div>
    </div>

    <el-card>
      <template #header>
        <span>系统简介</span>
      </template>
      <p style="line-height:1.8;color:#606266;">
        基于人脸识别的智能课堂考勤管理系统，采用前后端分离架构（SpringBoot + Vue.js），
        集成 SSD 人脸检测算法与 FaceNet 深度学习人脸识别算法，实现了从人脸信息采集、
        实时课堂人脸身份比对到考勤数据自动统计分析的全流程自动化管理。
      </p>
      <p style="line-height:1.8;color:#606266;margin-top:8px;">
        系统支持管理员、教师、学生多角色权限划分，可完成课程管理、考勤发起、
        考勤数据多维度筛选、报表导出等功能，无需专用硬件，普通教室摄像头即可满足部署需求。
      </p>
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'

const user = JSON.parse(localStorage.getItem('user') || '{}')
const todayAttendanceCount = ref(0)

const roleLabel = computed(() => {
  const map = { admin: '管理员', teacher: '教师', student: '学生' }
  return map[user?.role] || '未知'
})
const roleColor = computed(() => {
  const map = { admin: '#f56c6c', teacher: '#e6a23c', student: '#67c23a' }
  return map[user?.role] || '#909399'
})
</script>
