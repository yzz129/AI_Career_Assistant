import { defineStore } from 'pinia'
import { login as loginApi, me, register as registerApi } from '../api'

export const useAuthStore = defineStore('auth', {
  state: () => ({
    token: localStorage.getItem('internai_token') || '',
    user: JSON.parse(localStorage.getItem('internai_user') || 'null')
  }),
  getters: {
    role: (state) => state.user?.roleCode || ''
  },
  actions: {
    async login(form) {
      const data = await loginApi(form)
      this.token = data.token
      this.user = {
        userId: data.userId,
        username: data.username,
        realName: data.realName,
        roleCode: data.roleCode
      }
      localStorage.setItem('internai_token', this.token)
      localStorage.setItem('internai_user', JSON.stringify(this.user))
    },
    async register(form) {
      const data = await registerApi(form)
      this.token = data.token
      this.user = {
        userId: data.userId,
        username: data.username,
        realName: data.realName,
        roleCode: data.roleCode
      }
      localStorage.setItem('internai_token', this.token)
      localStorage.setItem('internai_user', JSON.stringify(this.user))
    },
    async hydrate() {
      if (!this.token) return
      this.user = await me()
      localStorage.setItem('internai_user', JSON.stringify(this.user))
    },
    logout() {
      this.token = ''
      this.user = null
      localStorage.removeItem('internai_token')
      localStorage.removeItem('internai_user')
    }
  }
})
