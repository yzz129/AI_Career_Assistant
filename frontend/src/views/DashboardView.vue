<template>
  <div class="reference-dashboard">
    <section class="dashboard-center">
      <header class="reference-header">
        <div class="greeting-block">
          <h1>{{ overview.profile.greeting || '早上好' }} <span>👋</span></h1>
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
          <i class="star star-a">✦</i>
          <i class="star star-b">✦</i>
        </div>

        <div class="header-actions">
          <button class="season-btn">
            <Calendar />
            {{ overview.profile.season }}
            <ArrowRight />
          </button>
          <button class="bell-btn">
            <Bell />
            <b>{{ overview.profile.notificationCount || 0 }}</b>
          </button>
          <div class="profile">
            <div class="avatar">{{ overview.profile.avatarText || 'AI' }}</div>
            <div>
              <strong>{{ overview.profile.realName }}</strong>
              <span>{{ overview.profile.major }}</span>
            </div>
            <ArrowDown />
          </div>
        </div>
      </header>

      <section class="stats-grid" v-loading="loading">
        <article v-for="item in displayStats" :key="item.key" class="stat-card">
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
          <h2><i>♟</i> 热门推荐岗位 <span>✦</span></h2>
          <button @click="$router.push('/jobs')">查看全部 <ArrowRight /></button>
        </div>
        <div v-if="overview.jobs.length" class="job-row">
          <article v-for="job in overview.jobs" :key="job.id || job.title" class="reference-job-card">
            <div class="company-logo" :class="job.logoClass">{{ job.logo }}</div>
            <h3>{{ job.company }}</h3>
            <strong>{{ job.title }}</strong>
            <p>{{ job.city }} · 实习</p>
            <div class="tag-row">
              <em v-for="tag in job.tags" :key="tag">{{ tag }}</em>
            </div>
            <footer>
              <span>{{ job.match }} <small>匹配</small></span>
              <CollectionTag />
            </footer>
          </article>
        </div>
        <div v-else class="empty-dashboard">数据库暂无开放岗位，请先维护岗位数据。</div>
      </section>

      <section class="glass-section process-section">
        <h2><i>▣</i> 我的申请流程</h2>
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
              <div class="small-logo">AI</div>
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
          <h2><i>▣</i> 简历优化建议 <span>✦</span></h2>
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
              <button @click="$router.push('/resume')">立即优化</button>
            </div>
            <div class="resume-illustration">
              <DocumentChecked />
              <i>★</i>
            </div>
          </div>
        </article>

        <article class="glass-section kb-card">
          <div class="section-title">
            <h2><i>▣</i> 知识库问答</h2>
            <button @click="$router.push('/knowledge')">更多问题 <ArrowRight /></button>
          </div>
          <ul>
            <li v-for="question in overview.knowledge.questions" :key="question">{{ question }}</li>
          </ul>
          <div class="kb-illustration">
            <Collection />
            <span>AI</span>
          </div>
        </article>
      </section>
    </section>

    <aside class="ai-panel">
      <div class="ai-panel-header">
        <div class="robot-avatar">🤖</div>
        <h2>AI 助手</h2>
        <span><i></i> 在线</span>
      </div>

      <div class="chat-stream">
        <div
          v-for="message in overview.assistant.messages"
          :key="`${message.role}-${message.time}-${message.content}`"
          :class="message.role === 'user' ? 'user-message' : 'assistant-message'"
        >
          <div v-if="message.role !== 'user'" class="robot-avatar small">🤖</div>
          <p>{{ message.content }}</p>
          <time>{{ message.time }}</time>
          <button v-if="message.showRecommendButton" @click="$router.push('/jobs')">
            {{ overview.assistant.recommendButtonText }} <ArrowRight />
          </button>
        </div>
      </div>

      <div class="quick-questions">
        <button v-for="question in overview.assistant.quickQuestions" :key="question">{{ question }}</button>
      </div>

      <div class="chat-input">
        <input placeholder="输入你的问题..." />
        <button @click="$router.push('/ai')"><Promotion /></button>
      </div>
      <p class="ai-tip">内容由 AI 生成，仅供参考</p>
    </aside>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
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
  Promotion,
  StarFilled,
  UserFilled
} from '@element-plus/icons-vue'
import { dashboardOverview } from '../api'

const loading = ref(false)
const overview = ref(emptyOverview())

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
      notificationCount: 0,
      avatarText: 'AI'
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
