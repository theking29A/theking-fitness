import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('admin_token') || '')
  const account = ref(localStorage.getItem('admin_account') || '')
  const nickname = ref(localStorage.getItem('admin_nickname') || '')

  const setUser = (data: { token: string; account: string; nickname?: string }) => {
    token.value = data.token
    account.value = data.account
    nickname.value = data.nickname || data.account
    localStorage.setItem('admin_token', data.token)
    localStorage.setItem('admin_account', data.account)
    localStorage.setItem('admin_nickname', data.nickname || data.account)
  }

  const logout = () => {
    token.value = ''
    account.value = ''
    nickname.value = ''
    localStorage.removeItem('admin_token')
    localStorage.removeItem('admin_account')
    localStorage.removeItem('admin_nickname')
  }

  return { token, account, nickname, setUser, logout }
})
