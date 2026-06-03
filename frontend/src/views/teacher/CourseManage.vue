<template>
  <div>
    <div class="page-header">
      <h3>课程管理</h3>
      <el-button type="primary" @click="showAddDialog">添加课程</el-button>
    </div>

    <div class="table-container">
      <div class="search-bar">
        <el-input v-model="query.keyword" placeholder="搜索课程名称/编号" clearable style="width:200px" @clear="fetchData" @keyup.enter="fetchData" />
        <el-button type="primary" @click="fetchData">搜索</el-button>
      </div>

      <el-table :data="courses" stripe v-loading="loading">
        <el-table-column prop="courseName" label="课程名称" min-width="150" />
        <el-table-column prop="courseCode" label="课程编号" width="120" />
        <el-table-column prop="teacherName" label="授课教师" width="120" />
        <el-table-column prop="className" label="班级" width="130" />
        <el-table-column prop="classroom" label="教室" width="120" />
        <el-table-column label="上课时间" width="200">
          <template #default="{ row }">
            周{{ ['','一','二','三','四','五','六','日'][row.weekDay] }}
            {{ row.startTime }} - {{ row.endTime }}
          </template>
        </el-table-column>
        <el-table-column label="教学周" width="130">
          <template #default="{ row }">
            第{{ row.weekStart }}-{{ row.weekEnd }}周
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button text type="primary" size="small" @click="showEditDialog(row)">编辑</el-button>
            <el-popconfirm title="确定删除？" @confirm="handleDelete(row.id)">
              <template #reference>
                <el-button text type="danger" size="small">删除</el-button>
              </template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>

      <div style="margin-top:16px;display:flex;justify-content:flex-end;">
        <el-pagination
          v-model:current-page="query.page"
          :page-size="query.pageSize"
          :total="total"
          layout="total, prev, pager, next"
          @current-change="fetchData"
        />
      </div>
    </div>

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑课程' : '添加课程'" width="600px">
      <el-form :model="form" :rules="formRules" ref="formRef" label-width="100px">
        <el-form-item label="课程名称" prop="courseName">
          <el-input v-model="form.courseName" />
        </el-form-item>
        <el-form-item label="课程编号">
          <el-input v-model="form.courseCode" />
        </el-form-item>
        <el-form-item label="授课教师" prop="teacherName">
          <el-input v-model="form.teacherName" />
        </el-form-item>
        <el-form-item label="教师ID" prop="teacherId">
          <el-input-number v-model="form.teacherId" :min="1" />
        </el-form-item>
        <el-form-item label="上课班级">
          <el-input v-model="form.className" />
        </el-form-item>
        <el-form-item label="教室">
          <el-input v-model="form.classroom" />
        </el-form-item>
        <el-row :gutter="16">
          <el-col :span="8">
            <el-form-item label="星期">
              <el-select v-model="form.weekDay">
                <el-option v-for="d in 7" :key="d" :label="'周' + ['','一','二','三','四','五','六','日'][d]" :value="d.toString()" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="开始">
              <el-time-picker v-model="form.startTime" format="HH:mm" value-format="HH:mm" style="width:100%" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="结束">
              <el-time-picker v-model="form.endTime" format="HH:mm" value-format="HH:mm" style="width:100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="起始周">
              <el-input-number v-model="form.weekStart" :min="1" :max="20" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="结束周">
              <el-input-number v-model="form.weekEnd" :min="form.weekStart || 1" :max="20" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitting">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { listCourses, addCourse, updateCourse, deleteCourse } from '../../api'

const loading = ref(false)
const submitting = ref(false)
const courses = ref([])
const total = ref(0)
const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref(null)

const query = reactive({ keyword: '', page: 1, pageSize: 10 })

const form = reactive({
  id: null, courseName: '', courseCode: '', teacherId: 1, teacherName: '',
  className: '', classroom: '', weekDay: '1', startTime: '08:00', endTime: '09:40',
  weekStart: 1, weekEnd: 18
})

const formRules = {
  courseName: [{ required: true, message: '请输入课程名称', trigger: 'blur' }],
  teacherName: [{ required: true, message: '请输入教师姓名', trigger: 'blur' }]
}

const fetchData = async () => {
  loading.value = true
  try {
    const res = await listCourses(query)
    if (res.code === 200) {
      courses.value = res.data.rows
      total.value = res.data.total
    }
  } finally {
    loading.value = false
  }
}

const showAddDialog = () => {
  isEdit.value = false
  Object.assign(form, { id: null, courseName: '', courseCode: '', teacherId: 1, teacherName: '', className: '', classroom: '', weekDay: '1', startTime: '08:00', endTime: '09:40', weekStart: 1, weekEnd: 18 })
  dialogVisible.value = true
}

const showEditDialog = (row) => {
  isEdit.value = true
  Object.assign(form, row)
  dialogVisible.value = true
}

const handleSubmit = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  submitting.value = true
  try {
    if (isEdit.value) {
      await updateCourse(form)
      ElMessage.success('更新成功')
    } else {
      await addCourse(form)
      ElMessage.success('添加成功')
    }
    dialogVisible.value = false
    fetchData()
  } finally {
    submitting.value = false
  }
}

const handleDelete = async (id) => {
  await deleteCourse(id)
  ElMessage.success('删除成功')
  fetchData()
}

onMounted(fetchData)
</script>
