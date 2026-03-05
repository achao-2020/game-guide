import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('token') || '')
  const username = ref(localStorage.getItem('username') || '')
  const role = ref(localStorage.getItem('role') || '')

  const isLoggedIn = computed(() => !!token.value)
  const isAdmin = computed(() => role.value === 'ADMIN')

  function setUserInfo(userInfo) {
    token.value = userInfo.token
    username.value = userInfo.username
    role.value = userInfo.role
    
    localStorage.setItem('token', userInfo.token)
    localStorage.setItem('username', userInfo.username)
    localStorage.setItem('role', userInfo.role)
  }

  function clearUserInfo() {
    token.value = ''
    username.value = ''
    role.value = ''
    
    localStorage.removeItem('token')
    localStorage.removeItem('username')
    localStorage.removeItem('role')
  }

  return {
    token,
    username,
    role,
    isLoggedIn,
    isAdmin,
    setUserInfo,
    clearUserInfo
  }
})



