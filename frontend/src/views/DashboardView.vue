<template>
  <div class="reference-dashboard">
    <div class="header-actions dashboard-header-actions">
      <button class="season-btn" @click="showSeason">
        <Calendar />
        {{ overview.profile.season }}
        <ArrowRight />
      </button>
      <button class="bell-btn" @click="showNotifications">
        <Bell />
        <b v-if="overview.profile.notificationCount">{{ overview.profile.notificationCount }}</b>
      </button>
      <div class="profile" role="button" tabindex="0" @click="showProfile" @keyup.enter="showProfile">
        <div class="avatar"><UserFilled /></div>
        <div>
          <strong>{{ overview.profile.realName }}</strong>
          <span>{{ overview.profile.major }}</span>
        </div>
        <ArrowDown />
      </div>
    </div>

    <section class="dashboard-center">
      <header class="reference-header">
        <div class="greeting-block">
          <h1>{{ overview.profile.greeting || '早上好' }} <Sunny class="greeting-icon" /></h1>
          <p>{{ overview.profile.subtitle }}</p>
        </div>

        <div class="header-mascot">
          <div class="tiny-mascot">
            <span class="sprout"></span>
            <span class="face eye-left"></span>
            <span class="face eye-right"></span>
            <span class="face blush-left"></span>
            <span class="face blush-right"></span>
          </div>
          <StarFilled class="star star-a" />
          <StarFilled class="star star-b" />
        </div>

      </header>

      <section class="stats-grid" v-loading="loading">
        <article v-for="item in displayStats" :key="item.key" class="stat-card clickable" @click="openStat(item.key)">
          <div class="stat-icon" :class="item.color">
            <component :is="item.icon" />
          </div>
          <div>
            <strong>{{ item.value }}</strong>
            <p>{{ item.label }}</p>
            <span v-html="item.sub"></span>
          </div>
        </article>
      </section>

      <section class="glass-section jobs-section">
        <div class="section-title">
          <h2><Briefcase class="section-icon" /> 热门推荐岗位</h2>
          <button @click="$router.push('/jobs')">查看全部 <ArrowRight /></button>
        </div>
        <div v-if="overview.jobs.length" class="job-row">
          <article v-for="job in overview.jobs" :key="job.id || job.title" class="reference-job-card clickable" @click="openJob(job)">
            <div class="company-logo" :class="job.logoClass"><OfficeBuilding /></div>
            <h3>{{ job.company }}</h3>
            <strong>{{ job.title }}</strong>
            <p>{{ job.city }} · {{ job.salaryRange }}</p>
            <div class="tag-row">
              <em v-for="tag in job.tags" :key="tag">{{ tag }}</em>
            </div>
            <footer>
              <span>{{ job.match }} <small>匹配</small></span>
              <div class="job-card-actions">
                <button
                  v-if="job.sourceUrl"
                  class="source-badge"
                  :title="job.sourceCheckedAt ? `${job.sourceCheckedAt} 核验` : '查看岗位来源'"
                  @click.stop="openSource(job)"
                >
                  <Link /> {{ job.sourceName }}
                </button>
                <button class="bookmark-button" :aria-label="isFavorite(job) ? '取消收藏' : '收藏岗位'" @click.stop="toggleFavorite(job)">
                  <CollectionTag :class="{ favorited: isFavorite(job) }" />
                </button>
              </div>
            </footer>
          </article>
        </div>
        <div v-else class="empty-dashboard">数据库暂无开放岗位，请先维护岗位数据。</div>
      </section>

      <section class="glass-section process-section">
        <h2><Promotion class="section-icon" /> 我的申请流程</h2>
        <div class="process-content">
          <div class="process-line">
            <div v-for="step in displayProcess" :key="step.label" class="process-step">
              <div class="process-dot" :class="step.color">
                <component :is="step.icon" />
              </div>
              <strong>{{ step.label }}</strong>
              <span>{{ step.time }}</span>
            </div>
          </div>
          <article class="current-card">
            <div class="current-top">
              <div class="small-logo"><OfficeBuilding /></div>
              <span>{{ overview.process.current.badge }}</span>
            </div>
            <h3>{{ overview.process.current.companyName }}</h3>
            <strong>{{ overview.process.current.jobTitle }}</strong>
            <p>{{ overview.process.current.progressText }}</p>
            <div class="progress-line">
              <span :style="{ width: `${overview.process.current.progressPercent || 0}%` }"></span>
            </div>
            <small>{{ overview.process.current.progressNo }}</small>
          </article>
        </div>
      </section>

      <section class="bottom-grid">
        <article class="glass-section resume-card">
          <h2><DocumentChecked class="section-icon" /> 简历优化建议</h2>
          <div class="resume-content">
            <div class="score-ring">
              <strong>{{ overview.resume.score }}</strong>
              <span>简历评分</span>
            </div>
            <div class="resume-list">
              <p>{{ overview.resume.summary }}</p>
              <div v-for="suggestion in overview.resume.suggestions" :key="suggestion.text">
                <Check /> {{ suggestion.text }}
                <em :class="suggestion.type">{{ suggestion.level }}</em>
              </div>
              <button @click="openResumeTool">立即优化</button>
            </div>
            <div class="resume-illustration">
              <DocumentChecked />
              <MagicStick class="magic-icon" />
            </div>
          </div>
        </article>

        <article class="glass-section kb-card">
          <div class="section-title">
            <h2><Collection class="section-icon" /> 知识库问答</h2>
            <button @click="openKnowledge">更多问题 <ArrowRight /></button>
          </div>
          <ul>
            <li v-for="question in overview.knowledge.questions" :key="question" class="clickable" @click="askQuestion(question)">{{ question }}</li>
          </ul>
          <div class="kb-illustration">
            <Collection />
            <Service class="kb-ai-icon" />
          </div>
        </article>
      </section>
    </section>

    <aside class="ai-panel">
      <div class="ai-panel-header">
        <div class="robot-avatar"><Service /></div>
        <h2>AI 助手</h2>
        <span><i></i> 在线</span>
      </div>

      <div class="chat-stream">
        <div
          v-for="message in overview.assistant.messages"
          :key="`${message.role}-${message.time}-${message.content}`"
          :class="message.role === 'user' ? 'user-message' : 'assistant-message'"
        >
          <div v-if="message.role !== 'user'" class="robot-avatar small"><Service /></div>
          <p>{{ message.content }}</p>
          <time>{{ message.time }}</time>
          <button v-if="message.showRecommendButton" @click="$router.push('/jobs')">
            {{ overview.assistant.recommendButtonText }} <ArrowRight />
          </button>
        </div>
      </div>

      <div class="quick-questions">
        <button v-for="question in overview.assistant.quickQuestions" :key="question" @click="askQuestion(question)">{{ question }}</button>
      </div>

      <div class="chat-input">
        <input v-model="chatQuestion" placeholder="输入你的问题..." @keyup.enter="sendQuestion" />
        <button aria-label="发送问题" @click="sendQuestion"><Promotion /></button>
      </div>
      <p class="ai-tip">内容由 AI 生成，仅供参考</p>
    </aside>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  ArrowDown,
  ArrowRight,
  Bell,
  Briefcase,
  Calendar,
  Check,
  ChatDotRound,
  Collection,
  CollectionTag,
  Document,
  DocumentChecked,
  Finished,
  Link,
  MagicStick,
  OfficeBuilding,
  Promotion,
  Service,
  StarFilled,
  Sunny,
  UserFilled
} from '@element-plus/icons-vue'
import { dashboardOverview } from '../api'
import { useAuthStore } from '../store/auth'

const loading = ref(false)
const overview = ref(emptyOverview())
const router = useRouter()
const auth = useAuthStore()
const chatQuestion = ref('')
const favorites = ref(JSON.parse(localStorage.getItem('internai_favorites') || '[]'))

function openStat(key) {
  const routes = {
    jobs: '/jobs',
    apply: auth.role === 'ADMIN' ? '/jobs' : '/applies',
    interview: auth.role === 'ADMIN' ? '/ai' : '/applies',
    match: auth.role === 'STUDENT' ? '/resume' : '/ai'
  }
  router.push(routes[key] || '/dashboard')
}

function openResumeTool() {
  router.push(auth.role === 'STUDENT' ? '/resume' : { path: '/ai', query: { tool: 'interview' } })
}

function openKnowledge() {
  router.push(auth.role === 'TEACHER' ? { path: '/ai', query: { tool: 'kb' } } : '/knowledge')
}

function openJob(job) {
  router.push({ path: '/jobs', query: { jobId: job.id } })
}

function openSource(job) {
  if (!job.sourceUrl) return
  window.open(job.sourceUrl, '_blank', 'noopener,noreferrer')
}

function isFavorite(job) {
  return favorites.value.includes(job.id)
}

function toggleFavorite(job) {
  favorites.value = isFavorite(job) ? favorites.value.filter((id) => id !== job.id) : [...favorites.value, job.id]
  localStorage.setItem('internai_favorites', JSON.stringify(favorites.value))
  ElMessage.success(isFavorite(job) ? '岗位已收藏' : '已取消收藏')
}

function askQuestion(question) {
  sessionStorage.setItem('internai_ai_question', question)
  router.push({ path: '/ai', query: { tool: 'kb' } })
}

function sendQuestion() {
  const value = chatQuestion.value.trim()
  if (!value) return ElMessage.warning('请输入问题')
  askQuestion(value)
}

function showSeason() {
  ElMessageBox.alert('当前为 2026 秋招季，建议优先完善简历并关注开放岗位。', '招聘日历')
}

function showNotifications() {
  router.push('/applies')
}

function showProfile() {
  ElMessageBox.alert(`${overview.value.profile.realName}\n${overview.value.profile.major}`, '个人信息')
}

const statIcons = {
  jobs: Briefcase,
  apply: Promotion,
  interview: StarFilled,
  match: Finished
}

const processIcons = {
  submit: Promotion,
  screen: Document,
  test: UserFilled,
  interview: ChatDotRound,
  offer: Check
}

const displayStats = computed(() =>
  overview.value.stats.map((item) => ({
    ...item,
    icon: statIcons[item.key] || Briefcase
  }))
)

const displayProcess = computed(() =>
  overview.value.process.steps.map((step) => ({
    ...step,
    icon: processIcons[step.iconKey] || Document
  }))
)

onMounted(loadOverview)

async function loadOverview() {
  loading.value = true
  try {
    overview.value = normalizeOverview(await dashboardOverview())
  } finally {
    loading.value = false
  }
}

function normalizeOverview(data) {
  const base = emptyOverview()
  return {
    ...base,
    ...data,
    profile: { ...base.profile, ...(data?.profile || {}) },
    process: {
      ...base.process,
      ...(data?.process || {}),
      current: { ...base.process.current, ...(data?.process?.current || {}) },
      steps: data?.process?.steps || []
    },
    resume: { ...base.resume, ...(data?.resume || {}), suggestions: data?.resume?.suggestions || [] },
    knowledge: { ...base.knowledge, ...(data?.knowledge || {}), questions: data?.knowledge?.questions || [] },
    assistant: {
      ...base.assistant,
      ...(data?.assistant || {}),
      messages: data?.assistant?.messages || [],
      quickQuestions: data?.assistant?.quickQuestions || []
    },
    stats: data?.stats || [],
    jobs: data?.jobs || []
  }
}

function emptyOverview() {
  return {
    profile: {
      greeting: '',
      subtitle: '',
      realName: '',
      major: '',
      season: '',
      notificationCount: 0
    },
    stats: [],
    jobs: [],
    process: {
      steps: [],
      current: {
        companyName: '',
        jobTitle: '',
        badge: '',
        progressText: '',
        progressPercent: 0,
        progressNo: '0/5'
      }
    },
    resume: {
      score: 0,
      summary: '',
      suggestions: []
    },
    knowledge: {
      questions: []
    },
    assistant: {
      messages: [],
      quickQuestions: [],
      recommendButtonText: ''
    }
  }
}
</script>
