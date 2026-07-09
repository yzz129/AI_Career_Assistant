import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '../store/auth'

const routes = [
  { path: '/login', component: () => import('../views/LoginView.vue') },
  {
    path: '/',
    component: () => import('../layout/AppLayout.vue'),
    redirect: '/dashboard',
    children: [
      { path: 'dashboard', component: () => import('../views/DashboardView.vue'), meta: { roles: ['ADMIN', 'TEACHER', 'STUDENT'] } },
      { path: 'users', component: () => import('../views/CrudView.vue'), meta: { roles: ['ADMIN'], resource: 'users' } },
      { path: 'students', component: () => import('../views/CrudView.vue'), meta: { roles: ['ADMIN', 'TEACHER'], resource: 'students' } },
      { path: 'teachers', component: () => import('../views/CrudView.vue'), meta: { roles: ['ADMIN'], resource: 'teachers' } },
      { path: 'companies', component: () => import('../views/CrudView.vue'), meta: { roles: ['ADMIN', 'TEACHER', 'STUDENT'], resource: 'companies' } },
      { path: 'jobs', component: () => import('../views/JobsView.vue'), meta: { roles: ['ADMIN', 'TEACHER', 'STUDENT'] } },
      { path: 'applies', component: () => import('../views/ApplyView.vue'), meta: { roles: ['TEACHER', 'STUDENT'] } },
      { path: 'logs', component: () => import('../views/LogView.vue'), meta: { roles: ['TEACHER', 'STUDENT'] } },
      { path: 'resume', component: () => import('../views/ResumeView.vue'), meta: { roles: ['STUDENT'] } },
      { path: 'ai', component: () => import('../views/AiAssistantView.vue'), meta: { roles: ['STUDENT', 'TEACHER', 'ADMIN'] } },
      { path: 'knowledge', component: () => import('../views/CrudView.vue'), meta: { roles: ['ADMIN', 'STUDENT'], resource: 'knowledge' } }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to) => {
  const auth = useAuthStore()
  if (to.path !== '/login' && !auth.token) return '/login'
  const roles = to.meta.roles
  if (roles && auth.role && !roles.includes(auth.role)) return '/dashboard'
  if (to.path === '/login' && auth.token) return '/dashboard'
  return true
})

export default router
