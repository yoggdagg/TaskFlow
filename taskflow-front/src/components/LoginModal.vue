<template>
    <teleport to="body">
        <transition name="modal">
            <div v-if="show" class="modal-overlay" @click="closeModal">
                <div class="modal-container" @click.stop>
                    <!-- 닫기 버튼 -->
                    <button class="close-btn" @click="closeModal" aria-label="닫기">
                        <svg viewBox="0 0 24 24">
                            <line x1="18" y1="6" x2="6" y2="18"></line>
                            <line x1="6" y1="6" x2="18" y2="18"></line>
                        </svg>
                    </button>

                    <!-- 헤더 -->
                    <div class="modal-header">
                        <div class="header-icon">
                            <svg viewBox="0 0 24 24" class="icon">
                                <path d="M9 11l3 3L22 4"></path>
                                <path d="M21 12v7a2 2 0 01-2 2H5a2 2 0 01-2-2V5a2 2 0 012-2h11"></path>
                            </svg>
                        </div>
                        <h2 class="modal-title">TaskFlow에 로그인</h2>
                        <p class="modal-subtitle">계속하려면 로그인해주세요</p>
                    </div>

                    <!-- 소셜 로그인 버튼 -->
                    <div class="social-login">
                        <button @click="loginWithGoogle" class="social-btn google-btn">
                            <svg viewBox="0 0 24 24" class="social-icon">
                                <path
                                    d="M22.56 12.25c0-.78-.07-1.53-.2-2.25H12v4.26h5.92c-.26 1.37-1.04 2.53-2.21 3.31v2.77h3.57c2.08-1.92 3.28-4.74 3.28-8.09z"
                                    fill="#4285F4" />
                                <path
                                    d="M12 23c2.97 0 5.46-.98 7.28-2.66l-3.57-2.77c-.98.66-2.23 1.06-3.71 1.06-2.86 0-5.29-1.93-6.16-4.53H2.18v2.84C3.99 20.53 7.7 23 12 23z"
                                    fill="#34A853" />
                                <path
                                    d="M5.84 14.09c-.22-.66-.35-1.36-.35-2.09s.13-1.43.35-2.09V7.07H2.18C1.43 8.55 1 10.22 1 12s.43 3.45 1.18 4.93l2.85-2.22.81-.62z"
                                    fill="#FBBC05" />
                                <path
                                    d="M12 5.38c1.62 0 3.06.56 4.21 1.64l3.15-3.15C17.45 2.09 14.97 1 12 1 7.7 1 3.99 3.47 2.18 7.07l3.66 2.84c.87-2.6 3.3-4.53 6.16-4.53z"
                                    fill="#EA4335" />
                            </svg>
                            <span>Google로 계속하기</span>
                        </button>

                        <button @click="loginWithKakao" class="social-btn kakao-btn">
                            <svg class="social-icon" viewBox="0 0 24 24">
                                <path fill="#000000"
                                    d="M12 3c-5.799 0-10.5 3.664-10.5 8.185 0 2.885 1.916 5.412 4.794 6.853-.197.718-.633 2.365-.717 2.747-.096.44.162.436.344.317.134-.088 2.138-1.405 3.124-2.062.633.088 1.284.135 1.955.135 5.799 0 10.5-3.664 10.5-8.185S17.799 3 12 3z" />
                            </svg>
                            <span>Kakao로 계속하기</span>
                        </button>

                        <button @click="loginWithNaver" class="social-btn naver-btn">
                            <svg class="social-icon" viewBox="0 0 24 24">
                                <path fill="#FFFFFF"
                                    d="M16.273 12.845L7.376 0H0v24h7.726V11.156L16.624 24H24V0h-7.727v12.845z" />
                            </svg>
                            <span>Naver로 계속하기</span>
                        </button>
                    </div>

                    <!-- 구분선 -->
                    <div class="divider">
                        <span>또는</span>
                    </div>

                    <!-- 이메일 로그인 폼 -->
                    <form @submit.prevent="handleEmailLogin" class="login-form">
                        <div class="form-group">
                            <label for="email" class="form-label">이메일</label>
                            <input v-model="email" type="email" id="email" class="form-input"
                                placeholder="email@example.com" required />
                        </div>

                        <div class="form-group">
                            <label for="password" class="form-label">비밀번호</label>
                            <div class="password-wrapper">
                                <input v-model="password" :type="showPassword ? 'text' : 'password'" id="password"
                                    class="form-input" placeholder="••••••••" required />
                                <button type="button" @click="togglePassword" class="toggle-password"
                                    aria-label="비밀번호 표시">
                                    <svg v-if="!showPassword" viewBox="0 0 24 24" class="eye-icon">
                                        <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"></path>
                                        <circle cx="12" cy="12" r="3"></circle>
                                    </svg>
                                    <svg v-else viewBox="0 0 24 24" class="eye-icon">
                                        <path
                                            d="M17.94 17.94A10.07 10.07 0 0 1 12 20c-7 0-11-8-11-8a18.45 18.45 0 0 1 5.06-5.94M9.9 4.24A9.12 9.12 0 0 1 12 4c7 0 11 8 11 8a18.5 18.5 0 0 1-2.16 3.19m-6.72-1.07a3 3 0 1 1-4.24-4.24">
                                        </path>
                                        <line x1="1" y1="1" x2="23" y2="23"></line>
                                    </svg>
                                </button>
                            </div>
                        </div>

                        <div class="form-options">
                            <label class="checkbox-wrapper">
                                <input type="checkbox" v-model="rememberMe" />
                                <span class="checkbox-label">로그인 상태 유지</span>
                            </label>
                            <a href="#" class="forgot-password" @click.prevent="forgotPassword">비밀번호 찾기</a>
                        </div>

                        <button type="submit" class="submit-btn" :disabled="isLoading">
                            <span v-if="!isLoading">로그인</span>
                            <span v-else class="loading-spinner">로그인 중...</span>
                        </button>
                    </form>

                    <!-- 회원가입 링크 -->
                    <div class="signup-link">
                        <span>계정이 없으신가요?</span>
                        <a href="#" @click.prevent="goToSignup">회원가입</a>
                    </div>
                </div>
            </div>
        </transition>
    </teleport>
</template>

<script setup>
import { ref, watch, onMounted, onBeforeUnmount } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/authStore'
import { googleLogin, kakaoLogin, naverLogin } from '@/api/oauth'

const router = useRouter()
const authStore = useAuthStore();
const props = defineProps({
    show: {
        type: Boolean,
        default: false
    }
})

const emit = defineEmits(['update:show', 'login-success'])

// 폼 상태
const email = ref('')
const password = ref('')
const showPassword = ref(false)
const rememberMe = ref(false)
const isLoading = ref(false)

// 모달 닫기
const closeModal = () => {
    emit('update:show', false)
    // 폼 초기화
    email.value = ''
    password.value = ''
    showPassword.value = false
    rememberMe.value = false
}

// 비밀번호 표시 토글
const togglePassword = () => {
    showPassword.value = !showPassword.value
}

// 컴포넌트 마운트 시 저장된 이메일 불러오기
onMounted(() => {
    const savedEmail = localStorage.getItem('savedEmail')
    if (savedEmail) {
        email.value = savedEmail
        rememberMe.value = true
    }
})

// 일반 로그인
const handleEmailLogin = async () => {
    isLoading.value = true

    try {
        const response = await auth.login(email.value, password.value)

        // 토큰을 localStorage에 저장
        if (response.accessToken) {
            localStorage.setItem('accessToken', response.accessToken)
        }
        if (response.refreshToken) {
            localStorage.setItem('refreshToken', response.refreshToken)
        }

        // 아이디 기억하기 체크박스 처리
        if (rememberMe.value) {
            localStorage.setItem('rememberMe', 'true')
            localStorage.setItem('savedEmail', email.value)
        } else {
            localStorage.removeItem('rememberMe')
            localStorage.removeItem('savedEmail')
        }

        // 로그인 성공 이벤트 emit
        emit('login-success', {
            name: response.name || response.username,
            email: response.email,
            accessToken: response.accessToken,
            refreshToken: response.refreshToken
        })

        closeModal();

        console.log('이메일 로그인 성공')
    } catch (error) {
        console.error('로그인 실패:', error)

        // 에러 메시지 처리
        let errorMessage = '로그인에 실패했습니다.'

        if (error.response) {
            // 서버에서 응답이 온 경우
            switch (error.response.status) {
                case 401:
                    errorMessage = '이메일 또는 비밀번호가 올바르지 않습니다.'
                    break
                case 404:
                    errorMessage = '존재하지 않는 계정입니다.'
                    break
                case 500:
                    errorMessage = '서버 오류가 발생했습니다. 잠시 후 다시 시도해주세요.'
                    break
                default:
                    errorMessage = error.response.data?.message || '로그인에 실패했습니다.'
            }
        } else if (error.request) {
            // 요청은 보냈지만 응답이 없는 경우
            errorMessage = '서버에 연결할 수 없습니다. 네트워크를 확인해주세요.'
        }

        alert(errorMessage)
    } finally {
        isLoading.value = false
    }
}

// 소셜 로그인
const loginWithGoogle = () => {
    console.log('Google 로그인 시작')
    googleLogin() // OAuth 인증 페이지로 리다이렉트
}

const loginWithKakao = () => {
    console.log('Kakao 로그인 시작')
    kakaoLogin()
}

const loginWithNaver = () => {
    console.log('Naver 로그인 시작')
    naverLogin()
}

// 비밀번호 찾기
const forgotPassword = () => {
    console.log('비밀번호 찾기')
    closeModal()
    router.push('/forgot-password')  // 수정
}

// 회원가입 - 수정된 부분
const goToSignup = () => {
    console.log('회원가입')
    closeModal()
    router.push('/signup')  // 주석 해제 및 활성화
}

// ESC 키로 닫기
watch(() => props.show, (newVal) => {
    if (newVal) {
        document.addEventListener('keydown', handleEscape)
        document.body.style.overflow = 'hidden'
    } else {
        document.removeEventListener('keydown', handleEscape)
        document.body.style.overflow = ''
    }
})

const handleEscape = (e) => {
    if (e.key === 'Escape') {
        closeModal()
    }
}
</script>


<style scoped>
.modal-overlay {
    position: fixed;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: rgba(0, 0, 0, 0.5);
    backdrop-filter: blur(4px);
    display: flex;
    align-items: center;
    justify-content: center;
    z-index: 1000;
    padding: 20px;
}

.modal-container {
    background: white;
    border-radius: 20px;
    box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
    max-width: 440px;
    width: 100%;
    max-height: 90vh;
    overflow-y: auto;
    position: relative;
    padding: 40px 32px;
}

.close-btn {
    position: absolute;
    top: 20px;
    right: 20px;
    width: 32px;
    height: 32px;
    border: none;
    background: #f1f5f9;
    border-radius: 8px;
    cursor: pointer;
    display: flex;
    align-items: center;
    justify-content: center;
    transition: all 0.2s;
}

.close-btn:hover {
    background: #e2e8f0;
    transform: rotate(90deg);
}

.close-btn svg {
    width: 16px;
    height: 16px;
    stroke: #64748b;
    stroke-width: 2;
}

.modal-header {
    text-align: center;
    margin-bottom: 32px;
}

.header-icon {
    width: 64px;
    height: 64px;
    margin: 0 auto 16px;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    border-radius: 16px;
    display: flex;
    align-items: center;
    justify-content: center;
}

.header-icon .icon {
    width: 32px;
    height: 32px;
    stroke: white;
    stroke-width: 2;
    stroke-linecap: round;
    stroke-linejoin: round;
    fill: none;
}

.modal-title {
    font-size: 24px;
    font-weight: 700;
    color: #1e293b;
    margin: 0 0 8px 0;
}

.modal-subtitle {
    font-size: 14px;
    color: #64748b;
    margin: 0;
}

.social-login {
    display: flex;
    flex-direction: column;
    gap: 12px;
    margin-bottom: 24px;
}

.social-btn {
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 12px;
    padding: 12px 20px;
    border: 2px solid #e2e8f0;
    background: white;
    border-radius: 12px;
    font-size: 15px;
    font-weight: 600;
    cursor: pointer;
    transition: all 0.2s;
}

.social-btn:hover {
    border-color: #cbd5e1;
    transform: translateY(-1px);
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
}

.social-icon {
    width: 20px;
    height: 20px;
}

.google-btn {
    color: #1e293b;
}

.kakao-btn {
    background: #FEE500;
    border-color: #FEE500;
    color: #000000;
}

.kakao-btn:hover {
    background: #FDD835;
    border-color: #FDD835;
}

.naver-btn {
    background: #03C75A;
    border-color: #03C75A;
    color: white;
}

.naver-btn:hover {
    background: #02B350;
    border-color: #02B350;
}

.divider {
    display: flex;
    align-items: center;
    text-align: center;
    margin: 24px 0;
}

.divider::before,
.divider::after {
    content: '';
    flex: 1;
    border-bottom: 1px solid #e2e8f0;
}

.divider span {
    padding: 0 16px;
    font-size: 13px;
    color: #94a3b8;
    font-weight: 500;
}

.login-form {
    display: flex;
    flex-direction: column;
    gap: 20px;
}

.form-group {
    display: flex;
    flex-direction: column;
    gap: 8px;
}

.form-label {
    font-size: 14px;
    font-weight: 600;
    color: #1e293b;
}

.form-input {
    padding: 12px 16px;
    border: 2px solid #e2e8f0;
    border-radius: 10px;
    font-size: 15px;
    transition: all 0.2s;
    outline: none;
}

.form-input:focus {
    border-color: #667eea;
    box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

.password-wrapper {
    position: relative;
}

.toggle-password {
    position: absolute;
    right: 12px;
    top: 50%;
    transform: translateY(-50%);
    background: none;
    border: none;
    cursor: pointer;
    padding: 4px;
}

.eye-icon {
    width: 20px;
    height: 20px;
    stroke: #94a3b8;
    stroke-width: 2;
    stroke-linecap: round;
    stroke-linejoin: round;
    fill: none;
}

.form-options {
    display: flex;
    justify-content: space-between;
    align-items: center;
}

.checkbox-wrapper {
    display: flex;
    align-items: center;
    gap: 8px;
    cursor: pointer;
}

.checkbox-wrapper input[type="checkbox"] {
    width: 18px;
    height: 18px;
    cursor: pointer;
}

.checkbox-label {
    font-size: 14px;
    color: #64748b;
}

.forgot-password {
    font-size: 14px;
    color: #667eea;
    text-decoration: none;
    font-weight: 600;
}

.forgot-password:hover {
    text-decoration: underline;
}

.submit-btn {
    padding: 14px 24px;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    color: white;
    border: none;
    border-radius: 12px;
    font-size: 16px;
    font-weight: 700;
    cursor: pointer;
    transition: all 0.2s;
}

.submit-btn:hover:not(:disabled) {
    transform: translateY(-2px);
    box-shadow: 0 8px 20px rgba(102, 126, 234, 0.4);
}

.submit-btn:disabled {
    opacity: 0.7;
    cursor: not-allowed;
}

.loading-spinner {
    display: inline-block;
}

.signup-link {
    text-align: center;
    margin-top: 24px;
    font-size: 14px;
    color: #64748b;
}

.signup-link a {
    color: #667eea;
    text-decoration: none;
    font-weight: 600;
    margin-left: 4px;
}

.signup-link a:hover {
    text-decoration: underline;
}

.modal-enter-active,
.modal-leave-active {
    transition: opacity 0.3s;
}

.modal-enter-from,
.modal-leave-to {
    opacity: 0;
}

.modal-enter-active .modal-container,
.modal-leave-active .modal-container {
    transition: transform 0.3s, opacity 0.3s;
}

.modal-enter-from .modal-container,
.modal-leave-to .modal-container {
    transform: scale(0.9);
    opacity: 0;
}

@media (max-width: 480px) {
    .modal-container {
        padding: 32px 24px;
    }

    .modal-title {
        font-size: 20px;
    }

    .social-btn {
        font-size: 14px;
        padding: 10px 16px;
    }
}
</style>
