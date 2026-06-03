<template>
  <div class="login-container">
    <div class="login-left">
      <div class="brand">
        <div class="brand-icon">
          <svg viewBox="0 0 48 48" width="64" height="64">
            <rect x="8" y="28" width="6" height="16" rx="1" fill="#fff" opacity="0.7"/>
            <rect x="16" y="20" width="6" height="24" rx="1" fill="#fff" opacity="0.85"/>
            <rect x="24" y="12" width="6" height="32" rx="1" fill="#fff"/>
            <rect x="32" y="20" width="6" height="24" rx="1" fill="#fff" opacity="0.85"/>
            <rect x="40" y="28" width="6" height="16" rx="1" fill="#fff" opacity="0.7"/>
            <circle cx="27" cy="8" r="3" fill="#fff" opacity="0.9"/>
          </svg>
        </div>
        <h1>课堂考勤管理系统</h1>
        <p>智能识别 · 精准记录 · 高效管理</p>
      </div>
    </div>
    <div class="login-right">
      <div class="login-card-inner">
        <h2>用户登录</h2>
        <p class="login-subtitle">请使用学校统一身份认证账号登录</p>
        <div class="login-form">
          <div class="login-field">
            <input v-model="form.username" placeholder="请输入账号" class="login-input" @keyup.enter="handleLogin" />
          </div>
          <div class="login-field">
            <input v-model="form.password" type="password" placeholder="请输入密码" class="login-input" @keyup.enter="handleLogin" />
          </div>
          <button class="login-btn" :class="{ loading: loading }" @click="handleLogin" :disabled="loading">
            {{ loading ? '登录中...' : '登 录' }}
          </button>
        </div>
        <div class="login-tips">
          <span>测试账号：admin / teacher01 / student01</span>
          <span>默认密码：123456</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getRandomTheme } from '../utils/theme'

const router = useRouter()
const loading = ref(false)

const form = reactive({ username: '', password: '' })

// 每次进入登录页随机一个人民币配色
onMounted(() => {
  const theme = getRandomTheme()
  // 设置左侧渐变
  document.documentElement.style.setProperty('--brand-start', theme.gradient[0])
  document.documentElement.style.setProperty('--brand-mid', theme.gradient[1])
  document.documentElement.style.setProperty('--brand-end', theme.gradient[2])
  // 设置按钮色
  document.documentElement.style.setProperty('--btn-color', theme.primary)
  document.documentElement.style.setProperty('--btn-hover', theme.light3)
  document.documentElement.style.setProperty('--btn-active', theme.dark2)
})

const handleLogin = async () => {
  if (!form.username || !form.password) {
    alert('请输入账号和密码')
    return
  }
  loading.value = true
  try {
    const res = await fetch('/api/auth/login', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(form)
    }).then(r => r.json())

    if (res.code === 200) {
      localStorage.setItem('token', res.data.token)
      localStorage.setItem('user', JSON.stringify(res.data.user))
      router.push('/dashboard')
    } else {
      alert(res.msg)
    }
  } catch {
    alert('登录失败')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-card-inner {
  width: 100%;
  max-width: 380px;
  background: #fff;
  padding: 40px;
  border-radius: 16px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
}

.login-card-inner h2 {
  margin-bottom: 4px;
  color: #1d1d1f;
  font-size: 22px;
  font-weight: 600;
}

.login-subtitle {
  color: #6e6e73;
  font-size: 14px;
  margin-bottom: 32px;
}

.login-form {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.login-field {
  width: 100%;
}

.login-input {
  width: 100%;
  height: 44px;
  padding: 0 16px;
  border: 1px solid #dcdfe6;
  border-radius: 8px;
  font-size: 15px;
  outline: none;
  background: #fff;
  color: #333;
  box-sizing: border-box;
  transition: border-color 0.2s;
}

.login-input:focus {
  border-color: var(--btn-color, #3d5a80);
}

.login-input::placeholder {
  color: #c0c4cc;
}

.login-btn {
  width: 100%;
  height: 44px;
  border: none;
  border-radius: 8px;
  font-size: 15px;
  color: #fff;
  background: var(--btn-color, #3d5a80);
  cursor: pointer;
  transition: background 0.2s;
}

.login-btn:hover {
  background: var(--btn-hover, #5a7d9a);
}

.login-btn:active {
  background: var(--btn-active, #2c4663);
}

.login-btn.loading {
  opacity: 0.7;
  cursor: not-allowed;
}

.login-tips {
  text-align: center;
  font-size: 12px;
  color: #8e8e93;
  margin-top: 24px;
  line-height: 1.8;
}

.login-tips span {
  display: block;
}
</style>
