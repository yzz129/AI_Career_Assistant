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
      <h2>欢迎回来</h2>
      <p>选择演示账号，一键进入答辩流程。</p>
      <el-form :model="form" label-position="top" @submit.prevent="handleLogin">
        <el-form-item label="用户名">
          <el-input v-model="form.username" size="large" placeholder="admin / teacher / student" />
        </el-form-item>
        <el-form-item label="密码">
          <el-input v-model="form.password" size="large" type="password" show-password placeholder="123456" />
        </el-form-item>
        <el-button class="primary-btn" size="large" :loading="loading" @click="handleLogin">进入 InternAI</el-button>
      </el-form>
      <div class="demo-accounts">
        <button v-for="account in accounts" :key="account" @click="quick(account)">{{ account }}</button>
      </div>
    </section>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../store/auth'

const router = useRouter()
const auth = useAuthStore()
const loading = ref(false)
const accounts = ['admin', 'teacher', 'student']
const form = reactive({ username: 'student', password: '123456' })

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
</script>
