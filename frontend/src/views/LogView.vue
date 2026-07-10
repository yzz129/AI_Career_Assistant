<template>
  <section class="workspace two-col">
    <div class="panel">
      <div class="toolbar">
        <el-button round @click="load">刷新日志</el-button>
      </div>
      <el-table :data="rows" class="soft-table" v-loading="loading">
        <el-table-column prop="logDate" label="日期" width="120" />
        <el-table-column prop="content" label="内容" min-width="240" show-overflow-tooltip />
        <el-table-column prop="status" label="状态" width="110" />
        <el-table-column prop="teacherComment" label="点评" min-width="180" show-overflow-tooltip />
        <el-table-column v-if="auth.role === 'TEACHER'" label="操作" width="120">
          <template #default="{ row }"><el-button link @click="comment(row)">点评</el-button></template>
        </el-table-column>
      </el-table>
    </div>
    <div v-if="auth.role === 'STUDENT'" class="panel write-panel">
      <h3>写一篇今日成长记录</h3>
      <el-date-picker v-model="form.logDate" value-format="YYYY-MM-DD" type="date" />
      <el-input v-model="form.content" type="textarea" :rows="8" placeholder="今天完成了什么？遇到了什么问题？下一步怎么做？" />
      <el-button class="primary-btn" round @click="save">保存日志</el-button>
    </div>
  </section>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { commentLog, myLogs, saveLog, todoLogs } from '../api'
import { useAuthStore } from '../store/auth'

const auth = useAuthStore()
const rows = ref([])
const loading = ref(false)
const form = reactive({ logDate: new Date().toISOString().slice(0, 10), content: '', status: 'SUBMITTED' })
load()

async function load() {
  loading.value = true
  try {
    const data = auth.role === 'TEACHER' ? await todoLogs({ pageNum: 1, pageSize: 20 }) : await myLogs({ pageNum: 1, pageSize: 20 })
    rows.value = data.records || []
  } finally {
    loading.value = false
  }
}

async function save() {
  if (!form.content.trim()) return ElMessage.warning('请填写日志内容')
  await saveLog(form)
  form.content = ''
  ElMessage.success('日志已保存')
  await load()
}

async function comment(row) {
  const { value } = await ElMessageBox.prompt('写下给学生的温柔点评', '日志点评')
  await commentLog({ logId: row.id, teacherComment: value })
  ElMessage.success('点评已提交')
  await load()
}
</script>
