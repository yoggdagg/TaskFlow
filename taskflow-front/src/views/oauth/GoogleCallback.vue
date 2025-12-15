<template>
    <div class="oauth-callback">
        <div class="loading">
            <div class="spinner"></div>
            <h2>로그인 처리 중...</h2>
            <p>잠시만 기다려주세요.</p>
        </div>
    </div>
</template>

<script setup>
import { onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { googleCallback } from '@/api/oauth';
import { useAuthStore } from '@/stores/authStore';

const router = useRouter();
const authStore = useAuthStore();

onMounted(async () => {
    try {
        const urlParams = new URLSearchParams(window.location.search);
        const code = urlParams.get('code');

        if (!code) {
            console.error('구글 인증 코드가 없습니다');
            alert('로그인에 실패했습니다.');
            router.push('/');
            return;
        }

        console.log('구글 인증 코드 받음:', code.substring(0, 20) + '...');

        const data = await googleCallback(code);

        console.log('Google 로그인 성공:', data);

        if (data.accessToken) {
            authStore.accessToken = data.accessToken;
            authStore.accessToken = data.accessToken;
            authStore.user = {
                id: data.id,
                name: data.username,
                email: data.email,
                phone: data.phone,
                address: data.address,
                profileImage: data.profileImage,
                provider: data.provider,
                role: data.role
            };



            // storage 이벤트 발생시켜 Navbar 업데이트 트리거
            window.dispatchEvent(new Event('storage'));

            alert(`${authStore.user.name}님 환영합니다!`);

            // 메인 페이지로 이동
            router.push('/');
        } else {
            throw new Error('액세스 토큰을 받지 못했습니다.');
        }

    } catch (error) {
        console.error('Google OAuth 콜백 처리 실패:', error);

        if (error.response) {
            const message = error.response.data?.message || '로그인에 실패했습니다.';
            alert(message);
        } else if (error.request) {
            alert('서버와 통신할 수 없습니다.');
        } else {
            alert('로그인 처리 중 오류가 발생했습니다.');
        }

        router.push('/');
    }
});
</script>

<style scoped>
.oauth-callback {
    display: flex;
    justify-content: center;
    align-items: center;
    height: 100vh;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.loading {
    text-align: center;
    color: white;
}

.spinner {
    width: 50px;
    height: 50px;
    margin: 0 auto 20px;
    border: 4px solid rgba(255, 255, 255, 0.3);
    border-top-color: white;
    border-radius: 50%;
    animation: spin 1s linear infinite;
}

@keyframes spin {
    to {
        transform: rotate(360deg);
    }
}

.loading h2 {
    font-size: 2rem;
    margin-bottom: 1rem;
}

.loading p {
    font-size: 1.2rem;
    opacity: 0.9;
}
</style>
