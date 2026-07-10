<template>
  <div class="login-page">
    <section class="login-hero">
      <div class="floating-card card-a">AI 简历建议已生成</div>
      <div class="floating-card card-b">3 个岗位适合你</div>
      <div class="mascot">AI</div>
      <h1>InternAI</h1>
      <p>把实习申请、日志、简历和 AI 求职辅导装进一个轻盈的职业驾驶舱。</p>
    </section>

    <section class="login-panel">
      <h2>{{ mode === 'login' ? '欢迎回来' : '创建账号' }}</h2>
      <p>{{ mode === 'login' ? '选择演示账号，一键进入答辩流程。' : '注册学生或教师身份，管理员账号仅可由后台创建。' }}</p>
      <el-form v-if="mode === 'login'" :model="form" label-position="top" @submit.prevent="handleLogin">
        <el-form-item label="用户名">
          <el-input v-model="form.username" size="large" placeholder="admin / teacher / student" />
        </el-form-item>
        <el-form-item label="密码">
          <el-input v-model="form.password" size="large" type="password" show-password placeholder="123456" />
        </el-form-item>
        <el-button class="primary-btn" size="large" :loading="loading" @click="handleLogin">进入 InternAI</el-button>
      </el-form>
      <el-form v-else :model="registerForm" label-position="top" @submit.prevent="handleRegister">
        <div class="register-grid">
          <el-form-item label="身份">
            <el-radio-group v-model="registerForm.roleCode">
              <el-radio-button value="STUDENT">学生</el-radio-button>
              <el-radio-button value="TEACHER">教师</el-radio-button>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="真实姓名">
            <el-input v-model="registerForm.realName" size="large" maxlength="64" placeholder="请输入姓名" />
          </el-form-item>
          <el-form-item label="用户名">
            <el-input v-model="registerForm.username" size="large" maxlength="32" placeholder="4-32 位字母、数字或下划线" />
          </el-form-item>
          <el-form-item label="密码">
            <el-input v-model="registerForm.password" size="large" type="password" show-password maxlength="32" placeholder="至少 6 位" />
          </el-form-item>
          <el-form-item label="确认密码">
            <el-input v-model="confirmPassword" size="large" type="password" show-password placeholder="再次输入密码" />
          </el-form-item>
          <el-form-item label="手机号（选填）">
            <el-input v-model="registerForm.phone" size="large" placeholder="请输入手机号" />
          </el-form-item>
          <el-form-item class="register-email" label="邮箱（选填）">
            <el-input v-model="registerForm.email" size="large" placeholder="name@example.com" />
          </el-form-item>
        </div>
        <el-button class="primary-btn" size="large" :loading="loading" @click="handleRegister">注册并进入 InternAI</el-button>
      </el-form>
      <div v-if="mode === 'login'" class="demo-accounts">
        <button v-for="account in accounts" :key="account" @click="quick(account)">{{ account }}</button>
      </div>
      <button class="auth-switch" type="button" @click="switchMode">
        {{ mode === 'login' ? '没有账号？立即注册' : '已有账号？返回登录' }}
      </button>
    </section>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useAuthStore } from '../store/auth'

const router = useRouter()
const auth = useAuthStore()
const loading = ref(false)
const mode = ref('login')
const confirmPassword = ref('')
const accounts = ['admin', 'teacher', 'student']
const form = reactive({ username: 'student', password: '123456' })
const registerForm = reactive({ username: '', password: '', realName: '', roleCode: 'STUDENT', phone: '', email: '' })

function switchMode() {
  mode.value = mode.value === 'login' ? 'register' : 'login'
}

function quick(username) {
  form.username = username
  form.password = '123456'
}

async function handleLogin() {
  loading.value = true
  try {
    await auth.login(form)
    router.push('/dashboard')
  } finally {
    loading.value = false
  }
}

async function handleRegister() {
  if (!registerForm.realName.trim()) return ElMessage.warning('请输入真实姓名')
  if (!/^[A-Za-z0-9_]{4,32}$/.test(registerForm.username)) return ElMessage.warning('用户名应为 4-32 位字母、数字或下划线')
  if (registerForm.password.length < 6) return ElMessage.warning('密码至少需要 6 位')
  if (registerForm.password !== confirmPassword.value) return ElMessage.warning('两次输入的密码不一致')
  loading.value = true
  try {
    await auth.register(registerForm)
    ElMessage.success('注册成功')
    router.push('/dashboard')
  } finally {
    loading.value = false
  }
}
</script>
