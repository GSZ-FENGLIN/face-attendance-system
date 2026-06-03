<template>
  <div>
    <div class="page-header">
      <h3>用户管理</h3>
      <div>
        <el-button type="primary" @click="showAddDialog">添加用户</el-button>
      </div>
    </div>

    <div class="table-container">
      <div class="search-bar">
        <el-input v-model="query.keyword" placeholder="搜索姓名/账号/学号" clearable style="width:200px" @clear="fetchData" @keyup.enter="fetchData" />
        <el-select v-model="query.role" placeholder="角色" clearable style="width:120px" @change="fetchData">
          <el-option label="管理员" value="admin" />
          <el-option label="教师" value="teacher" />
          <el-option label="学生" value="student" />
        </el-select>
        <el-select v-model="query.status" placeholder="状态" clearable style="width:120px" @change="fetchData">
          <el-option label="启用" :value="1" />
          <el-option label="禁用" :value="0" />
        </el-select>
        <el-button type="primary" @click="fetchData">搜索</el-button>
      </div>

      <el-table :data="users" stripe v-loading="loading">
        <el-table-column prop="username" label="账号" width="120" />
        <el-table-column prop="realName" label="姓名" width="120" />
        <el-table-column prop="role" label="角色" width="100">
          <template #default="{ row }">
            <el-tag :type="row.role === 'admin' ? 'danger' : row.role === 'teacher' ? 'warning' : 'success'" size="small">
              {{ row.role === 'admin' ? '管理员' : row.role === 'teacher' ? '教师' : '学生' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="studentNo" label="学号" width="120" />
        <el-table-column prop="className" label="班级" min-width="120" />
        <el-table-column prop="phone" label="电话" width="130" />
        <el-table-column prop="faceRegistered" label="人脸" width="80">
          <template #default="{ row }">
            <el-tag :type="row.faceRegistered ? 'success' : 'info'" size="small">
              {{ row.faceRegistered ? '已注册' : '未注册' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status ? 'success' : 'danger'" size="small">
              {{ row.status ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button text type="primary" size="small" @click="showEditDialog(row)">编辑</el-button>
            <el-button text type="warning" size="small" @click="handleResetPwd(row.id)">重置密码</el-button>
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

    <!-- 添加/编辑对话框 -->
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑用户' : '添加用户'" width="500px">
      <el-form :model="form" :rules="formRules" ref="formRef" label-width="80px">
        <el-form-item label="账号" prop="username">
          <el-input v-model="form.username" :disabled="isEdit" />
        </el-form-item>
        <el-form-item label="姓名" prop="realName">
          <el-input v-model="form.realName" />
        </el-form-item>
        <el-form-item label="角色" prop="role">
          <el-select v-model="form.role" style="width:100%">
            <el-option label="管理员" value="admin" />
            <el-option label="教师" value="teacher" />
            <el-option label="学生" value="student" />
          </el-select>
        </el-form-item>
        <el-form-item label="学号" v-if="form.role === 'student'">
          <el-input v-model="form.studentNo" />
        </el-form-item>
        <el-form-item label="班级" v-if="form.role === 'student'">
          <el-input v-model="form.className" />
        </el-form-item>
        <el-form-item label="电话">
          <el-input v-model="form.phone" />
        </el-form-item>
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
import { listUsers, addUser, updateUser, deleteUser, resetPassword } from '../../api'

const loading = ref(false)
const submitting = ref(false)
const users = ref([])
const total = ref(0)
const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref(null)

const query = reactive({
  keyword: '',
  role: '',
  status: null,
  page: 1,
  pageSize: 10
})

const form = reactive({
  id: null,
  username: '',
  realName: '',
  role: 'student',
  studentNo: '',
  className: '',
  phone: ''
})

const formRules = {
  username: [{ required: true, message: '请输入账号', trigger: 'blur' }],
  realName: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  role: [{ required: true, message: '请选择角色', trigger: 'change' }]
}

const fetchData = async () => {
  loading.value = true
  try {
    const res = await listUsers(query)
    if (res.code === 200) {
      users.value = res.data.rows
      total.value = res.data.total
    }
  } finally {
    loading.value = false
  }
}

const showAddDialog = () => {
  isEdit.value = false
  form.id = null
  form.username = ''
  form.realName = ''
  form.role = 'student'
  form.studentNo = ''
  form.className = ''
  form.phone = ''
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
      await updateUser(form)
      ElMessage.success('更新成功')
    } else {
      await addUser(form)
      ElMessage.success('添加成功')
    }
    dialogVisible.value = false
    fetchData()
  } finally {
    submitting.value = false
  }
}

const handleDelete = async (id) => {
  await deleteUser(id)
  ElMessage.success('删除成功')
  fetchData()
}

const handleResetPwd = async (id) => {
  await resetPassword(id)
  ElMessage.success('密码已重置为 123456')
}

onMounted(fetchData)
</script>
