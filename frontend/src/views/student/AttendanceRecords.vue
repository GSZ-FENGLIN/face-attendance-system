<template>
  <div>
    <div class="page-header">
      <h3>我的考勤记录</h3>
    </div>

    <el-card>
      <el-table :data="records" stripe v-loading="loading">
        <el-table-column prop="courseName" label="课程名称" min-width="150" />
        <el-table-column label="考勤状态" width="120">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : row.status === 2 ? 'warning' : 'danger'" effect="dark">
              {{ row.status === 1 ? '已签到' : row.status === 2 ? '迟到' : '缺勤' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="signTime" label="签到时间" width="180" />
        <el-table-column prop="weekNum" label="周次" width="80" />
      </el-table>

      <div style="margin-top:16px;display:flex;justify-content:flex-end;">
        <el-pagination
          v-model:current-page="page"
          :page-size="pageSize"
          :total="total"
          layout="total, prev, pager, next"
          @current-change="fetchData"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { listAttendance } from '../../api'

const user = JSON.parse(localStorage.getItem('user') || '{}')
const records = ref([])
const loading = ref(false)
const total = ref(0)
const page = ref(1)
const pageSize = ref(10)

const fetchData = async () => {
  loading.value = true
  try {
    const res = await listAttendance({ studentId: user.id, page: page.value, pageSize: pageSize.value })
    if (res.code === 200) {
      records.value = res.data.rows || []
      total.value = res.data.total
    }
  } finally {
    loading.value = false
  }
}

onMounted(fetchData)
</script>
