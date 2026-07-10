<template>
  <section class="workspace">
    <div class="toolbar">
      <el-input v-model="query.title" placeholder="岗位名称" clearable />
      <el-input v-model="query.city" placeholder="城市" clearable />
      <el-input v-model="query.skillKeyword" placeholder="技能关键词" clearable />
      <el-button round @click="load">探索岗位</el-button>
      <el-button v-if="auth.role === 'ADMIN'" class="primary-btn small" round @click="open({ status: 'OPEN' })">新增岗位</el-button>
    </div>

    <div class="job-grid" v-loading="loading">
      <article v-for="job in rows" :key="job.id" class="job-card" :data-job-id="job.id">
        <div class="job-card-head">
          <span>{{ job.city || '远程友好' }}</span>
          <strong>{{ job.salaryRange || '面议' }}</strong>
        </div>
        <h3>{{ job.title }}</h3>
        <p>{{ job.companyName }}</p>
        <div class="chips">
          <em v-for="skill in splitSkills(job.skillKeyword)" :key="skill">{{ skill }}</em>
        </div>
        <footer>
          <el-button v-if="auth.role === 'STUDENT'" class="primary-btn small" round @click="apply(job)">申请</el-button>
          <template v-if="auth.role === 'ADMIN'">
            <el-button link @click="open(job)">编辑</el-button>
            <el-button link type="danger" @click="remove(job)">删除</el-button>
          </template>
        </footer>
      </article>
    </div>

    <el-dialog v-model="dialogVisible" title="岗位信息" width="620px">
      <el-form label-position="top">
        <el-form-item label="企业ID"><el-input v-model="form.companyId" /></el-form-item>
        <el-form-item label="企业名称"><el-input v-model="form.companyName" /></el-form-item>
        <el-form-item label="岗位名称"><el-input v-model="form.title" /></el-form-item>
        <el-form-item label="城市"><el-input v-model="form.city" /></el-form-item>
        <el-form-item label="薪资"><el-input v-model="form.salaryRange" /></el-form-item>
        <el-form-item label="技能关键词"><el-input v-model="form.skillKeyword" /></el-form-item>
        <el-form-item label="状态"><el-input v-model="form.status" /></el-form-item>
        <el-form-item label="描述"><el-input v-model="form.description" type="textarea" :rows="4" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button class="primary-btn small" @click="save">保存</el-button>
      </template>
    </el-dialog>
  </section>
</template>

<script setup>
import { nextTick, reactive, ref } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { createApi, deleteApi, pageApi, submitApply, updateApi } from '../api'
import { useAuthStore } from '../store/auth'

const auth = useAuthStore()
const route = useRoute()
const loading = ref(false)
const rows = ref([])
const dialogVisible = ref(false)
const query = reactive({ title: '', city: '', skillKeyword: '', status: 'OPEN' })
const form = reactive({})

load()

function splitSkills(value = '') {
  return value.split(/[,，]/).map((s) => s.trim()).filter(Boolean).slice(0, 4)
}

async function load() {
  loading.value = true
  try {
    const data = await pageApi('/jobs', { pageNum: 1, pageSize: 30, ...query })
    rows.value = data.records || []
    if (route.query.jobId) {
      await nextTick()
      document.querySelector(`[data-job-id="${route.query.jobId}"]`)?.scrollIntoView({ behavior: 'smooth', block: 'center' })
    }
  } finally {
    loading.value = false
  }
}

function open(row) {
  Object.keys(form).forEach((key) => delete form[key])
  Object.assign(form, row)
  dialogVisible.value = true
}

async function save() {
  if (!String(form.companyId || '').trim()) return ElMessage.warning('请填写企业 ID')
  if (!String(form.title || '').trim()) return ElMessage.warning('请填写岗位名称')
  if (form.id) await updateApi('/jobs', form.id, form)
  else await createApi('/jobs', form)
  dialogVisible.value = false
  ElMessage.success(form.id ? '岗位修改成功' : '岗位新增成功')
  await load()
}

async function remove(row) {
  await ElMessageBox.confirm('确认删除岗位吗？')
  await deleteApi('/jobs', row.id)
  ElMessage.success('岗位已删除')
  await load()
}

async function apply(job) {
  const { value } = await ElMessageBox.prompt('请简要说明你的申请理由', `申请 ${job.title}`, {
    inputValue: `我希望申请 ${job.title}，并将课程与项目能力用于真实实习场景。`,
    inputValidator: (text) => text?.trim().length >= 10 || '申请理由至少填写 10 个字'
  })
  await submitApply({ jobId: job.id, reason: value.trim() })
  ElMessage.success('申请已提交，等待老师审核')
}
</script>
