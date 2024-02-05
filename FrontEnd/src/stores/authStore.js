// src/stores/authStore.js
import { defineStore } from 'pinia';
import { useCookies } from 'vue3-cookies';

export const useAuthStore = defineStore('auth', {
  state: () => ({
    isLogin: false,
  }),
  actions: {
    checkLoginStatus() {
      const { cookies } = useCookies();
      this.isLogin = !!cookies.get('accessToken');
    }
  },
});
