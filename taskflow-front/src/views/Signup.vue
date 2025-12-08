<template>
    <div class="register-container">
        <div class="register-card">
            <h2>회원가입</h2>

            <!-- 소셜 로그인 버튼 -->
            <div class="social-login-section">
                <button @click="socialLogin('google')" class="social-btn google-btn" disabled>
                    <span class="icon">G</span>
                    구글로 시작하기
                </button>

                <button @click="socialLogin('naver')" class="social-btn naver-btn" disabled>
                    <span class="icon">N</span>
                    네이버로 시작하기
                </button>

                <button @click="socialLogin('kakao')" class="social-btn kakao-btn" disabled>
                    <span class="icon">K</span>
                    카카오로 시작하기
                </button>
            </div>

            <!-- 구분선 -->
            <div class="divider">
                <span>또는</span>
            </div>

            <!-- 일반 회원가입 폼 -->
            <form @submit.prevent="handleRegister" class="register-form">
                <div class="form-group">
                    <label for="email">이메일</label>
                    <input type="email" id="email" v-model="formData.email" @blur="validateEmail"
                        placeholder="example@email.com" required />
                    <span v-if="errors.email" class="error-message">{{ errors.email }}</span>
                </div>

                <div class="form-group">
                    <label for="password">비밀번호</label>
                    <input type="password" id="password" v-model="formData.password" @blur="validatePassword"
                        placeholder="8자 이상 입력해주세요" required />
                    <span v-if="errors.password" class="error-message">{{ errors.password }}</span>
                </div>

                <div class="form-group">
                    <label for="passwordConfirm">비밀번호 확인</label>
                    <input type="password" id="passwordConfirm" v-model="formData.passwordConfirm"
                        @blur="validatePasswordConfirm" placeholder="비밀번호를 다시 입력해주세요" required />
                    <span v-if="errors.passwordConfirm" class="error-message">
                        {{ errors.passwordConfirm }}
                    </span>
                </div>

                <div class="form-group">
                    <label for="name">이름</label>
                    <input type="text" id="name" v-model="formData.name" @blur="validateName" placeholder="홍길동"
                        required />
                    <span v-if="errors.name" class="error-message">{{ errors.name }}</span>
                </div>

                <button type="submit" class="register-btn" :disabled="isSubmitting">
                    {{ isSubmitting ? '처리 중...' : '회원가입' }}
                </button>
            </form>

            <div class="login-link">
                이미 계정이 있으신가요?
                <router-link to="/login">로그인하기</router-link>
            </div>
        </div>
    </div>
</template>

<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import { useFormValidation } from '@/composables/useFormValidation';
import auth from '@/api/auth';

const router = useRouter();
const isSubmitting = ref(false);

// 폼 유효성 검사 컴포저블 사용
const {
    formData,
    errors,
    validateEmail,
    validatePassword,
    validatePasswordConfirm,
    validateName,
    validateForm,
    resetForm
} = useFormValidation();

// 회원가입 처리
const handleRegister = async () => {
    // 유효성 검사
    if (!validateForm()) {
        alert('입력 정보를 확인해주세요.');
        return;
    }

    isSubmitting.value = true;

    try {
        const response = await auth.post('/api/member/regist', {
            email: formData.email,
            password: formData.password,
            name: formData.name
        });

        if (response.status === 200 || response.status === 201) {
            alert('회원가입이 완료되었습니다!');
            resetForm();
            router.push('/login');
        }
    } catch (error) {
        handleRegistrationError(error);
    } finally {
        isSubmitting.value = false;
    }
};

// 에러 처리 함수
const handleRegistrationError = (error) => {
    console.error('회원가입 실패:', error);

    if (error.response) {
        const { status, data } = error.response;
        const errorMessage = data?.message || data?.error;

        const errorMessages = {
            400: errorMessage || '잘못된 요청입니다. 입력 정보를 확인해주세요.',
            409: '이미 가입된 이메일입니다.',
            500: '서버 오류가 발생했습니다. 잠시 후 다시 시도해주세요.'
        };

        alert(errorMessages[status] || errorMessage || '회원가입 중 오류가 발생했습니다.');
    } else if (error.request) {
        alert('서버와 통신할 수 없습니다. 네트워크 연결을 확인해주세요.');
    } else {
        alert('요청 처리 중 오류가 발생했습니다.');
    }
};

// 소셜 로그인 처리 (현재는 비활성화)
const socialLogin = (provider) => {
    console.log(`${provider} 로그인은 준비 중입니다.`);
};
</script>

<style scoped>
.register-container {
    display: flex;
    justify-content: center;
    align-items: center;
    min-height: 100vh;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    padding: 20px;
}

.register-card {
    background: white;
    border-radius: 16px;
    padding: 40px;
    width: 100%;
    max-width: 450px;
    box-shadow: 0 10px 40px rgba(0, 0, 0, 0.1);
}

h2 {
    text-align: center;
    color: #333;
    margin-bottom: 30px;
    font-size: 28px;
}

.social-login-section {
    display: flex;
    flex-direction: column;
    gap: 12px;
    margin-bottom: 20px;
}

.social-btn {
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 10px;
    padding: 12px 20px;
    border: none;
    border-radius: 8px;
    font-size: 15px;
    font-weight: 500;
    cursor: pointer;
    transition: all 0.3s ease;
}

.social-btn:disabled {
    opacity: 0.5;
    cursor: not-allowed;
}

.social-btn .icon {
    font-weight: bold;
    font-size: 18px;
}

.google-btn {
    background: white;
    color: #333;
    border: 1px solid #ddd;
}

.google-btn:hover:not(:disabled) {
    background: #f8f9fa;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.naver-btn {
    background: #03C75A;
    color: white;
}

.naver-btn:hover:not(:disabled) {
    background: #02b350;
}

.kakao-btn {
    background: #FEE500;
    color: #000000;
}

.kakao-btn:hover:not(:disabled) {
    background: #f5dc00;
}

.divider {
    display: flex;
    align-items: center;
    text-align: center;
    margin: 25px 0;
    color: #999;
}

.divider::before,
.divider::after {
    content: '';
    flex: 1;
    border-bottom: 1px solid #ddd;
}

.divider span {
    padding: 0 15px;
    font-size: 14px;
}

.register-form {
    display: flex;
    flex-direction: column;
    gap: 20px;
}

.form-group {
    display: flex;
    flex-direction: column;
    gap: 8px;
}

.form-group label {
    font-size: 14px;
    font-weight: 500;
    color: #555;
}

.form-group input {
    padding: 12px 16px;
    border: 1px solid #ddd;
    border-radius: 8px;
    font-size: 15px;
    transition: border-color 0.3s ease;
}

.form-group input:focus {
    outline: none;
    border-color: #667eea;
}

.error-message {
    color: #e74c3c;
    font-size: 13px;
    margin-top: 4px;
}

.register-btn {
    padding: 14px;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    color: white;
    border: none;
    border-radius: 8px;
    font-size: 16px;
    font-weight: 600;
    cursor: pointer;
    transition: opacity 0.3s ease;
    margin-top: 10px;
}

.register-btn:hover:not(:disabled) {
    opacity: 0.9;
}

.register-btn:disabled {
    opacity: 0.6;
    cursor: not-allowed;
}

.login-link {
    text-align: center;
    margin-top: 20px;
    font-size: 14px;
    color: #666;
}

.login-link a {
    color: #667eea;
    text-decoration: none;
    font-weight: 500;
}

.login-link a:hover {
    text-decoration: underline;
}
</style>
