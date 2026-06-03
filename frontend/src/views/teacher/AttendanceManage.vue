<template>
  <div>
    <div class="page-header">
      <h3>考勤管理</h3>
    </div>

    <!-- 我的课程列表 -->
    <el-card>
      <template #header>
        <span>我的课程</span>
      </template>
      <el-table :data="courses" stripe v-loading="loadingCourses">
        <el-table-column prop="courseName" label="课程名称" min-width="140" />
        <el-table-column prop="className" label="班级" width="140" />
        <el-table-column prop="classroom" label="教室" width="120" />
        <el-table-column label="上课时间" width="190">
          <template #default="{ row }">
            周{{ ['','一','二','三','四','五','六','日'][row.weekDay] }} {{ row.startTime }} - {{ row.endTime }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="240" fixed="right">
          <template #default="{ row }">
            <el-button type="success" size="small" @click="handleStart(row)" :loading="startingId === row.id">
              <el-icon><VideoCamera /></el-icon> 开始考勤
            </el-button>
            <el-button type="warning" size="small" @click="handleStop(row)">
              <el-icon><VideoPause /></el-icon> 结束
            </el-button>
            <el-button size="small" @click="viewAttendance(row)">
              <el-icon><View /></el-icon> 查看记录
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 考勤记录 -->
    <el-card style="margin-top:16px;">
      <template #header>
        <span>考勤记录</span>
      </template>

      <div class="search-bar">
        <el-select v-model="query.courseId" placeholder="选择课程" clearable style="width:200px" @change="fetchRecords">
          <el-option v-for="c in courses" :key="c.id" :label="c.courseName" :value="c.id" />
        </el-select>
        <el-input-number v-model="query.weekNum" :min="1" :max="20" placeholder="周次" />
        <el-select v-model="query.status" placeholder="状态" clearable style="width:120px" @change="fetchRecords">
          <el-option label="已签到" :value="1" />
          <el-option label="缺勤" :value="0" />
          <el-option label="迟到" :value="2" />
        </el-select>
        <el-button type="primary" @click="fetchRecords">查询</el-button>
        <el-button @click="handleExport">导出Excel</el-button>
      </div>

      <el-table :data="records" stripe v-loading="loadingRecords">
        <el-table-column prop="studentNo" label="学号" width="120" />
        <el-table-column prop="studentName" label="姓名" width="120" />
        <el-table-column prop="className" label="班级" width="120" />
        <el-table-column label="考勤状态" width="120">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : row.status === 2 ? 'warning' : 'danger'">
              {{ row.status === 1 ? '已签到' : row.status === 2 ? '迟到' : '缺勤' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="signTime" label="签到时间" width="180" />
        <el-table-column prop="weekNum" label="周次" width="80" />
      </el-table>

      <div style="margin-top:16px;display:flex;justify-content:flex-end;">
        <el-pagination
          v-model:current-page="query.page"
          :page-size="query.pageSize"
          :total="totalRecords"
          layout="total, prev, pager, next"
          @current-change="fetchRecords"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { listCourses, startAttendance, stopAttendance, listAttendance, exportAttendance, getCourseAttendance } from '../../api'

const user = JSON.parse(localStorage.getItem('user') || '{}')

const courses = ref([])
const loadingCourses = ref(false)
const startingId = ref(null)
const records = ref([])
const loadingRecords = ref(false)
const totalRecords = ref(0)

const query = reactive({
  courseId: null,
  weekNum: null,
  status: null,
  page: 1,
  pageSize: 10
})

const fetchCourses = async () => {
  loadingCourses.value = true
  try {
    const res = await listCourses({ teacherId: user.role === 'teacher' ? user.id : undefined, pageSize: 100 })
    if (res.code === 200) courses.value = res.data.rows
  } finally {
    loadingCourses.value = false
  }
}

const handleStart = async (course) => {
  startingId.value = course.id
  try {
    const res = await startAttendance(course.id)
    if (res.code === 200) {
      ElMessage.success(`课程 "${course.courseName}" 考勤已开始，摄像头即将启动`)
    } else {
      ElMessage.error(res.msg)
    }
  } finally {
    startingId.value = null
  }
}

const handleStop = async (course) => {
  try {
    await stopAttendance(course.id)
    ElMessage.success('考勤已结束')
  } catch (e) {
    ElMessage.error('结束失败')
  }
}

const fetchRecords = async () => {
  if (!query.courseId) return
  loadingRecords.value = true
  try {
    const res = await listAttendance(query)
    if (res.code === 200) {
      records.value = res.data.rows
      totalRecords.value = res.data.total
    }
  } finally {
    loadingRecords.value = false
  }
}

const viewAttendance = (course) => {
  query.courseId = course.id
  query.weekNum = new Date().getWeekNumber()
  fetchRecords()
}

const handleExport = () => {
  if (!query.courseId) {
    ElMessage.warning('请先选择课程')
    return
  }
  exportAttendance(query.courseId, query.weekNum || undefined)
}

// 获取当前周数
Date.prototype.getWeekNumber = function () {
  const startOfYear = new Date(this.getFullYear(), 0, 1)
  const diff = this - startOfYear
  return Math.ceil((diff / 86400000 + startOfYear.getDay() + 1) / 7)
}

onMounted(fetchCourses)
</script>
