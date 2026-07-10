<template>
  <section class="ai-workspace">
    <aside class="ai-tools">
      <button v-for="tool in availableTools" :key="tool.key" :class="{ active: active === tool.key }" @click="selectTool(tool.key)">
        <strong>{{ tool.title }}</strong>
        <span>{{ tool.desc }}</span>
      </button>
    </aside>

    <main class="ai-chat">
      <div class="chat-header">
        <div class="mascot-mini">AI</div>
        <div>
          <h3>{{ current.title }}</h3>
          <p>{{ current.desc }}</p>
        </div>
      </div>

      <div class="input-cloud">
        <el-input v-if="active === 'kb'" v-model="question" placeholder="例如：实习申请流程是什么？" />
        <el-input v-if="active === 'interview'" v-model="answer" type="textarea" :rows="3" placeholder="输入你的模拟面试回答..." />
        <el-button class="primary-btn" :loading="streaming" round @click="run">开始流式生成</el-button>
      </div>

      <article class="answer-box">
        <div v-if="!answerText" class="empty-state">点击按钮后，AI 会像打字一样实时输出。</div>
        <p>{{ answerText }}</p>
      </article>
    </main>
  </section>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import request from '../utils/request'
import { useAuthStore } from '../store/auth'

const auth = useAuthStore()
const route = useRoute()
const active = ref(auth.role === 'STUDENT' ? 'resume' : 'interview')
const streaming = ref(false)
const answerText = ref('')
const question = ref('实习申请流程是什么？')
const answer = ref('我在项目中负责后端接口开发，完成了 JWT 登录和岗位管理接口。')
const resume = ref(null)
const student = ref(null)

const tools = [
  { key: 'resume', title: '简历优化', desc: '把经历打磨得更清晰' },
  { key: 'job', title: '岗位推荐', desc: '根据技能匹配机会' },
  { key: 'interview', title: '模拟面试', desc: '获得回答点评' },
  { key: 'kb', title: '知识库问答', desc: '查询校内流程制度' }
]
const availableTools = computed(() => tools.filter((tool) => auth.role === 'STUDENT' || !['resume', 'job'].includes(tool.key)))
const current = computed(() => tools.find((tool) => tool.key === active.value) || availableTools.value[0])

onMounted(() => {
  const requested = String(route.query.tool || '')
  if (availableTools.value.some((tool) => tool.key === requested)) active.value = requested
  const savedQuestion = sessionStorage.getItem('internai_ai_question')
  if (savedQuestion) {
    question.value = savedQuestion
    active.value = 'kb'
    sessionStorage.removeItem('internai_ai_question')
  }
})

function selectTool(key) {
  active.value = key
  answerText.value = ''
}

async function run() {
  if (active.value === 'kb' && !question.value.trim()) return ElMessage.warning('请输入问题')
  if (active.value === 'interview' && !answer.value.trim()) return ElMessage.warning('请输入面试回答')
  answerText.value = ''
  streaming.value = true
  try {
    const url = await buildUrl()
    await stream(url)
  } catch (error) {
    ElMessage.error(error.message || 'AI 生成失败')
  } finally {
    streaming.value = false
  }
}

async function buildUrl() {
  if (active.value === 'resume') {
    if (!resume.value) resume.value = await request.get('/resume/my')
    if (!resume.value?.id) throw new Error('请先保存一份简历')
    return `/api/ai/resume/optimize?resumeId=${resume.value.id}`
  }
  if (active.value === 'job') {
    if (!student.value && auth.role === 'STUDENT') student.value = await request.get('/students/me')
    return `/api/ai/job/recommend?studentId=${student.value?.id || 1}`
  }
  if (active.value === 'interview') {
    return `/api/ai/interview?jobId=1&answer=${encodeURIComponent(answer.value)}`
  }
  return `/api/ai/kb/chat?question=${encodeURIComponent(question.value)}`
}

async function stream(url) {
  const response = await fetch(url, {
    headers: { Authorization: `Bearer ${localStorage.getItem('internai_token') || ''}` }
  })
  if (!response.ok || !response.body) throw new Error('无法连接 AI 流')
  const reader = response.body.getReader()
  const decoder = new TextDecoder('utf-8')
  while (true) {
    const { done, value } = await reader.read()
    if (done) break
    const chunk = decoder.decode(value, { stream: true })
    answerText.value += chunk
      .split('\n')
      .map((line) => line.replace(/^data:/, '').trim())
      .filter((line) => line && line !== '[DONE]')
      .join('')
  }
}
</script>
