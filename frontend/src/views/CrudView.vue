<template>
  <section class="workspace">
    <div class="toolbar">
      <el-input v-model="keyword" :placeholder="config.search" clearable @keyup.enter="load" />
      <el-button round @click="load">查询</el-button>
      <el-button v-if="canEdit" class="primary-btn small" round @click="openCreate">新增</el-button>
    </div>

    <el-table :data="rows" class="soft-table" v-loading="loading">
      <el-table-column v-for="col in config.columns" :key="col.prop" :prop="col.prop" :label="col.label" min-width="120" show-overflow-tooltip />
      <el-table-column v-if="canEdit" label="操作" width="170" fixed="right">
        <template #default="{ row }">
          <el-button link @click="openEdit(row)">编辑</el-button>
          <el-button link type="danger" @click="remove(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination layout="prev, pager, next" :total="total" :page-size="pageSize" v-model:current-page="pageNum" @current-change="load" />

    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑' : '新增'" width="560px">
      <el-form label-position="top">
        <el-form-item v-for="field in config.fields" :key="field.prop" :label="field.label">
          <el-input v-model="form[field.prop]" :type="field.type || 'text'" :rows="field.rows || 3" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button class="primary-btn small" @click="save">保存</el-button>
      </template>
    </el-dialog>
  </section>
</template>

<script setup>
import { computed, reactive, ref, watch } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessageBox } from 'element-plus'
import { createApi, deleteApi, pageApi, updateApi } from '../api'
import { useAuthStore } from '../store/auth'

const route = useRoute()
const auth = useAuthStore()
const loading = ref(false)
const rows = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = 10
const keyword = ref('')
const dialogVisible = ref(false)
const form = reactive({})

const configs = {
  users: {
    base: '/users',
    search: '搜索用户名 / 姓名',
    columns: [{ prop: 'username', label: '用户名' }, { prop: 'realName', label: '姓名' }, { prop: 'roleCode', label: '角色' }, { prop: 'status', label: '状态' }],
    fields: [{ prop: 'username', label: '用户名' }, { prop: 'password', label: '密码' }, { prop: 'realName', label: '姓名' }, { prop: 'roleCode', label: '角色' }, { prop: 'phone', label: '电话' }, { prop: 'email', label: '邮箱' }]
  },
  students: {
    base: '/students',
    search: '搜索学生姓名 / 学号',
    columns: [{ prop: 'studentNo', label: '学号' }, { prop: 'name', label: '姓名' }, { prop: 'major', label: '专业' }, { prop: 'skills', label: '技能' }, { prop: 'intentionCity', label: '意向城市' }],
    fields: [{ prop: 'userId', label: '用户ID' }, { prop: 'studentNo', label: '学号' }, { prop: 'name', label: '姓名' }, { prop: 'major', label: '专业' }, { prop: 'grade', label: '年级' }, { prop: 'teacherId', label: '指导教师ID' }, { prop: 'skills', label: '技能' }, { prop: 'intentionCity', label: '意向城市' }]
  },
  teachers: {
    base: '/teachers',
    search: '搜索教师姓名 / 工号',
    columns: [{ prop: 'teacherNo', label: '工号' }, { prop: 'name', label: '姓名' }, { prop: 'department', label: '院系' }, { prop: 'title', label: '职称' }],
    fields: [{ prop: 'userId', label: '用户ID' }, { prop: 'teacherNo', label: '工号' }, { prop: 'name', label: '姓名' }, { prop: 'department', label: '院系' }, { prop: 'title', label: '职称' }, { prop: 'phone', label: '电话' }]
  },
  companies: {
    base: '/companies',
    search: '搜索企业名称',
    columns: [{ prop: 'companyName', label: '企业名称' }, { prop: 'industry', label: '行业' }, { prop: 'city', label: '城市' }, { prop: 'contactName', label: '联系人' }],
    fields: [{ prop: 'companyName', label: '企业名称' }, { prop: 'industry', label: '行业' }, { prop: 'city', label: '城市' }, { prop: 'contactName', label: '联系人' }, { prop: 'contactPhone', label: '联系电话' }, { prop: 'description', label: '企业介绍', type: 'textarea', rows: 4 }]
  },
  knowledge: {
    base: '/knowledge',
    search: '搜索知识库标题 / 关键词',
    columns: [{ prop: 'title', label: '标题' }, { prop: 'category', label: '分类' }, { prop: 'keywords', label: '关键词' }, { prop: 'content', label: '内容' }],
    fields: [{ prop: 'title', label: '标题' }, { prop: 'category', label: '分类' }, { prop: 'keywords', label: '关键词' }, { prop: 'content', label: '内容', type: 'textarea', rows: 6 }]
  }
}

const config = computed(() => configs[route.meta.resource])
const canEdit = computed(() => auth.role === 'ADMIN')

watch(() => route.meta.resource, () => {
  pageNum.value = 1
  load()
}, { immediate: true })

async function load() {
  loading.value = true
  try {
    const params = { pageNum: pageNum.value, pageSize }
    if (route.meta.resource === 'companies') params.companyName = keyword.value
    else params.keyword = keyword.value
    const data = await pageApi(config.value.base, params)
    rows.value = data.records || []
    total.value = data.total || 0
  } finally {
    loading.value = false
  }
}

function openCreate() {
  Object.keys(form).forEach((key) => delete form[key])
  dialogVisible.value = true
}

function openEdit(row) {
  Object.assign(form, row)
  dialogVisible.value = true
}

async function save() {
  if (form.id) await updateApi(config.value.base, form.id, form)
  else await createApi(config.value.base, form)
  dialogVisible.value = false
  load()
}

async function remove(row) {
  await ElMessageBox.confirm('确认删除这条记录吗？')
  await deleteApi(config.value.base, row.id)
  load()
}
</script>
