// src/views/oauth/NaverCallback.vue
<script setup>
import { onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/authStore'
import { naverCallback } from '@/api/oauth'

const router = useRouter()
const authStore = useAuthStore()

onMounted(async () => {
    try {
        const urlParams = new URLSearchParams(window.location.search)
        const code = urlParams.get('code')
        const state = urlParams.get('state')

        if (!code || !state) {
            throw new Error('Authorization code or state not found')
        }

        const data = await naverCallback(code, state)
        authStore.login(data)

        console.log('Naver 로그인 성공:', data.user)
        router.push('/')

    } catch (error) {
        console.error('Naver 로그인 실패:', error)
        alert('Naver 로그인에 실패했습니다.')
        router.push('/login')
    }
})
</script>

<template>
    <div class="callback-container">
        <p>Naver 로그인 처리 중...</p>
    </div>
</template>

<style scoped>
.callback-container {
    display: flex;
    justify-content: center;
    align-items: center;
    height: 100vh;
    font-size: 1.2rem;
}
</style>
