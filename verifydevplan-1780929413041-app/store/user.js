import { defineStore } from 'pinia'
import { userLogin } from '@/api/user.js'
import { getToken, setToken, setUser, getUser, clearAuth, isLoggedIn } from '@/utils/auth.js'

export const useUserStore = defineStore('user', {
  state: () => ({
    token: getToken(),
    userInfo: getUser()
  }),
  getters: {
    loggedIn: (state) => !!state.token
  },
  actions: {
    async login(username, password) {
      const data = await userLogin({ username, password })
      this.token = data.token
      this.userInfo = data.userInfo
      setToken(data.token)
      setUser(data.userInfo)
      return data
    },
    logout() {
      this.token = ''
      this.userInfo = null
      clearAuth()
    },
    restoreSession() {
      this.token = getToken()
      this.userInfo = getUser()
      return isLoggedIn()
    }
  }
})
