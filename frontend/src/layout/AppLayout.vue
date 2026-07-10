<template>
  <div class="app-shell">
    <aside class="sidebar">
      <div class="brand" @click="$router.push('/dashboard')">
        <div class="brand-text">
          <strong>InternAI</strong>
          <i aria-hidden="true"></i>
        </div>
        <small>AI 实习就业助手</small>
      </div>

      <nav class="nav-list">
        <RouterLink v-for="item in visibleMenus" :key="item.path" :to="item.path" class="nav-item">
          <component :is="item.icon" />
          <span>{{ item.label }}</span>
        </RouterLink>
      </nav>

      <div class="sidebar-note">
        <div class="tiny-mascot sidebar-mascot">
          <span class="sprout"></span>
          <span class="face eye-left"></span>
          <span class="face eye-right"></span>
          <span class="face blush-left"></span>
          <span class="face blush-right"></span>
        </div>
        <p>今天也要加油鸭！</p>
        <small>已为你生成 <b>12</b> 条建议</small>
        <div class="note-progress"><span></span></div>
        <div class="growth-pill">
          <StarFilled />
          成长值 <b>860</b>
        </div>
      </div>
    </aside>

    <main class="main">
      <header class="app-topbar">
        <button class="topbar-logout" type="button" aria-label="退出登录" @click="logout">
          <SwitchButton />
          <span>退出登录</span>
        </button>
      </header>
      <RouterView />
    </main>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useAuthStore } from '../store/auth'
import {
  Briefcase,
  ChatDotRound,
  Collection,
  Document,
  DocumentChecked,
  EditPen,
  Notebook,
  OfficeBuilding,
  StarFilled,
  SwitchButton,
  User,
  UserFilled
} from '@element-plus/icons-vue'

const auth = useAuthStore()

const menus = [
  { path: '/dashboard', label: '岗位中心', icon: Briefcase, roles: ['ADMIN', 'TEACHER', 'STUDENT'] },
  { path: '/jobs', label: '岗位列表', icon: Briefcase, roles: ['ADMIN', 'TEACHER', 'STUDENT'] },
  { path: '/applies', label: auth.role === 'TEACHER' ? '申请审核' : '我的申请', icon: Document, roles: ['TEACHER', 'STUDENT'] },
  { path: '/logs', label: auth.role === 'TEACHER' ? '日志点评' : '实习日志', icon: EditPen, roles: ['TEACHER', 'STUDENT'] },
  { path: '/ai', label: 'AI 助手', icon: ChatDotRound, roles: ['ADMIN', 'TEACHER', 'STUDENT'] },
  { path: '/resume', label: '简历优化', icon: DocumentChecked, roles: ['STUDENT'] },
  { path: '/knowledge', label: '知识库问答', icon: Collection, roles: ['ADMIN', 'STUDENT'] },
  { path: '/users', label: '用户管理', icon: User, roles: ['ADMIN'] },
  { path: '/students', label: '学生档案', icon: Notebook, roles: ['ADMIN', 'TEACHER'] },
  { path: '/teachers', label: '教师档案', icon: UserFilled, roles: ['ADMIN'] },
  { path: '/companies', label: '企业管理', icon: OfficeBuilding, roles: ['ADMIN'] }
]

const visibleMenus = computed(() => menus.filter((item) => item.roles.includes(auth.role)))

function logout() {
  auth.logout()
  window.location.href = '/login'
}
</script>
