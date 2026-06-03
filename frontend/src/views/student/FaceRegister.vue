<template>
  <div>
    <div class="page-header">
      <h3>人脸注册</h3>
    </div>

    <el-row :gutter="24">
      <el-col :span="12">
        <el-card>
          <template #header>
            <span>摄像头拍照</span>
          </template>
          <div style="text-align:center;">
            <video ref="videoRef" autoplay playsinline style="width:100%;max-width:480px;border-radius:8px;background:#000;"></video>
            <div style="margin-top:16px;">
              <el-button type="primary" @click="capturePhoto" :disabled="!cameraReady">
                <el-icon><Camera /></el-icon> 拍照
              </el-button>
              <el-button @click="startCamera" v-if="!cameraReady">开启摄像头</el-button>
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :span="12">
        <el-card>
          <template #header>
            <span>上传照片</span>
          </template>
          <div style="text-align:center;">
            <div v-if="previewUrl">
              <img :src="previewUrl" style="width:100%;max-width:480px;border-radius:8px;" />
            </div>
            <div v-else style="height:240px;display:flex;align-items:center;justify-content:center;color:#909399;border:2px dashed #dcdfe6;border-radius:8px;">
              请拍照或上传照片
            </div>
            <div style="margin-top:16px;">
              <input type="file" ref="fileInput" accept="image/*" style="display:none" @change="handleFileChange" />
              <el-button @click="$refs.fileInput.click()">选择图片</el-button>
              <el-button type="success" @click="submitFace" :loading="submitting" :disabled="!previewUrl">
                <el-icon><Check /></el-icon> 提交注册
              </el-button>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-card style="margin-top:16px;">
      <template #header>
        <span>注册说明</span>
      </template>
      <ol style="line-height:2;color:#606266;padding-left:20px;">
        <li>请确保面部清晰可见，无遮挡物（口罩、帽子、墨镜等）</li>
        <li>建议在不同角度（正面、左侧、右侧）各拍摄一张照片，提高识别准确率</li>
        <li>确保光线充足，避免背光和阴影</li>
        <li>系统将提取人脸128维特征向量并存储，用于后续考勤比对</li>
        <li>人脸注册成功后，后续考勤无需任何操作，系统自动识别</li>
      </ol>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Camera, Check } from '@element-plus/icons-vue'
import { registerFace } from '../../api'

const user = JSON.parse(localStorage.getItem('user') || '{}')

const videoRef = ref(null)
const fileInput = ref(null)
const cameraReady = ref(false)
const previewUrl = ref('')
const capturedBlob = ref(null)
const submitting = ref(false)
let stream = null

onMounted(() => {
  if (user.faceRegistered) {
    ElMessage.success('您已成功注册人脸，如需更新请重新拍照上传')
  }
})

onUnmounted(() => {
  stopCamera()
})

const startCamera = async () => {
  try {
    stream = await navigator.mediaDevices.getUserMedia({ video: true })
    if (videoRef.value) {
      videoRef.value.srcObject = stream
    }
    cameraReady.value = true
  } catch (e) {
    ElMessage.error('无法开启摄像头: ' + e.message)
  }
}

const stopCamera = () => {
  if (stream) {
    stream.getTracks().forEach(t => t.stop())
    stream = null
  }
  cameraReady.value = false
}

const capturePhoto = () => {
  if (!videoRef.value || !cameraReady.value) return

  const canvas = document.createElement('canvas')
  canvas.width = videoRef.value.videoWidth
  canvas.height = videoRef.value.videoHeight
  canvas.getContext('2d').drawImage(videoRef.value, 0, 0)

  canvas.toBlob((blob) => {
    capturedBlob.value = blob
    previewUrl.value = URL.createObjectURL(blob)
    ElMessage.success('拍照成功')
  }, 'image/jpeg', 0.9)
}

const handleFileChange = (e) => {
  const file = e.target.files[0]
  if (!file) return
  capturedBlob.value = file
  previewUrl.value = URL.createObjectURL(file)
}

const submitFace = async () => {
  if (!capturedBlob.value) {
    ElMessage.warning('请先拍照或选择照片')
    return
  }

  submitting.value = true
  try {
    const file = new File([capturedBlob.value], 'face.jpg', { type: 'image/jpeg' })
    const res = await registerFace(user.id, file)
    if (res.code === 200) {
      ElMessage.success('人脸注册成功！')
      user.faceRegistered = 1
      localStorage.setItem('user', JSON.stringify(user))
    } else {
      ElMessage.error(res.msg)
    }
  } catch (e) {
    ElMessage.error('注册失败')
  } finally {
    submitting.value = false
  }
}
</script>
