<template>
  <div>
    <div class="page-header">
      <h3>数据统计</h3>
    </div>

    <el-card>
      <template #header>
        <span>出勤率统计</span>
      </template>

      <div class="search-bar" style="margin-bottom:16px;">
        <el-select v-model="courseId" placeholder="选择课程" style="width:240px">
          <el-option v-for="c in courses" :key="c.id" :label="c.courseName" :value="c.id" />
        </el-select>
        <el-button type="primary" @click="loadStatistics">查询统计</el-button>
      </div>

      <el-row :gutter="16" v-if="statsLoaded">
        <el-col :span="8">
          <div class="stat-card">
            <div class="label">已签到</div>
            <div class="value" style="color:#67c23a;">{{ presentCount }}</div>
          </div>
        </el-col>
        <el-col :span="8">
          <div class="stat-card">
            <div class="label">缺勤</div>
            <div class="value" style="color:#f56c6c;">{{ absentCount }}</div>
          </div>
        </el-col>
        <el-col :span="8">
          <div class="stat-card">
            <div class="label">迟到</div>
            <div class="value" style="color:#e6a23c;">{{ lateCount }}</div>
          </div>
        </el-col>
      </el-row>

      <div ref="chartRef" style="width:100%;height:400px;margin-top:16px;"></div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue'
import { listCourses, getAttendanceStats } from '../../api'
import * as echarts from 'echarts'

const user = JSON.parse(localStorage.getItem('user') || '{}')
const courses = ref([])
const courseId = ref(null)
const statsLoaded = ref(false)
const presentCount = ref(0)
const absentCount = ref(0)
const lateCount = ref(0)
const chartRef = ref(null)

let chart = null

const fetchCourses = async () => {
  const res = await listCourses({ teacherId: user.role === 'teacher' ? user.id : undefined, pageSize: 100 })
  if (res.code === 200) courses.value = res.data.rows
}

const loadStatistics = async () => {
  if (!courseId.value) return
  const res = await getAttendanceStats(courseId.value)
  if (res.code !== 200) return

  const stats = res.data
  presentCount.value = stats.find(s => s.status === 1)?.count || 0
  absentCount.value = stats.find(s => s.status === 0)?.count || 0
  lateCount.value = stats.find(s => s.status === 2)?.count || 0
  statsLoaded.value = true

  await nextTick()
  renderChart()
}

const renderChart = () => {
  if (chart) chart.dispose()
  if (!chartRef.value) return

  chart = echarts.init(chartRef.value)
  chart.setOption({
    title: { text: '考勤统计', left: 'center' },
    tooltip: { trigger: 'item' },
    legend: { bottom: '5%' },
    series: [{
      type: 'pie',
      radius: ['40%', '70%'],
      center: ['50%', '45%'],
      label: { show: true, formatter: '{b}: {c} ({d}%)' },
      data: [
        { value: presentCount.value, name: '已签到', itemStyle: { color: '#67c23a' } },
        { value: absentCount.value, name: '缺勤', itemStyle: { color: '#f56c6c' } },
        { value: lateCount.value, name: '迟到', itemStyle: { color: '#e6a23c' } }
      ]
    }]
  })
}

onMounted(fetchCourses)
</script>
