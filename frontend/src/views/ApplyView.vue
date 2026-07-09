<template>
  <section class="workspace">
    <div class="toolbar">
      <el-button round @click="load">{{ auth.role === 'TEACHER' ? '刷新待审核' : '刷新我的申请' }}</el-button>
    </div>
    <el-table :data="rows" class="soft-table" v-loading="loading">
      <el-table-column prop="jobTitle" label="岗位" min-width="180" />
      <el-table-column prop="companyName" label="企业" min-width="160" />
      <el-table-column prop="reason" label="申请理由" min-width="220" show-overflow-tooltip />
      <el-table-column prop="status" label="状态" width="120" />
      <el-table-column prop="reviewComment" label="教师意见" min-width="180" show-overflow-tooltip />
      <el-table-column v-if="auth.role === 'TEACHER'" label="审核" width="210">
        <template #default="{ row }">
          <el-button link @click="review(row, 'APPROVED')">通过</el-button>
          <el-button link type="danger" @click="review(row, 'REJECTED')">驳回</el-button>
        </template>
      </el-table-column>
    </el-table>
  </section>
</template>

<script setup>
import { ref } from 'vue'
import { ElMessageBox } from 'element-plus'
import { myApplies, reviewApply, todoApplies } from '../api'
import { useAuthStore } from '../store/auth'

const auth = useAuthStore()
const rows = ref([])
const loading = ref(false)
load()

async function load() {
  loading.value = true
  try {
    const data = auth.role === 'TEACHER' ? await todoApplies({ pageNum: 1, pageSize: 20 }) : await myApplies({ pageNum: 1, pageSize: 20 })
    rows.value = data.records || []
  } finally {
    loading.value = false
  }
}

async function review(row, status) {
  const { value } = await ElMessageBox.prompt('填写审核意见', status === 'APPROVED' ? '通过申请' : '驳回申请')
  await reviewApply({ applyId: row.id, status, reviewComment: value })
  load()
}
</script>
