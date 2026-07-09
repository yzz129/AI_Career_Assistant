<template>
  <section class="workspace two-col">
    <div class="panel resume-editor">
      <h3>我的简历</h3>
      <el-input v-model="resume.title" placeholder="简历标题" />
      <el-input v-model="resume.content" type="textarea" :rows="14" placeholder="教育背景、项目经历、技能栈、求职方向..." />
      <el-input v-model="resume.fileUrl" placeholder="附件链接，可选" />
      <el-button class="primary-btn" round @click="save">保存简历</el-button>
    </div>
    <div class="panel resume-tips">
      <h3>AI 建议预览</h3>
      <p>{{ resume.aiSuggestion || '保存简历后，可以到 AI 助手里进行流式优化演示。' }}</p>
      <el-button round @click="$router.push('/ai')">去优化简历</el-button>
    </div>
  </section>
</template>

<script setup>
import { reactive } from 'vue'
import { ElMessage } from 'element-plus'
import { myResume, saveResume } from '../api'

const resume = reactive({ title: '', content: '', fileUrl: '', aiSuggestion: '', version: 1 })

init()

async function init() {
  const data = await myResume()
  if (data) Object.assign(resume, data)
}

async function save() {
  const data = await saveResume(resume)
  Object.assign(resume, data)
  ElMessage.success('简历已保存')
}
</script>
