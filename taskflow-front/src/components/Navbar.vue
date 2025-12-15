<template>
    <header class="navbar">
        <div class="navbar-container">
            <!-- 로고 & 타이틀 -->
            <div class="navbar-brand">
                <div class="brand-icon">
                    <svg viewBox="0 0 24 24" class="logo-icon">
                        <path d="M9 11l3 3L22 4"></path>
                        <path d="M21 12v7a2 2 0 01-2 2H5a2 2 0 01-2-2V5a2 2 0 012-2h11"></path>
                    </svg>
                </div>
                <div class="brand-text">
                    <h1 class="app-name">TaskFlow</h1>
                    <p class="app-subtitle">{{ greeting }}</p>
                </div>
            </div>

            <!-- 우측 액션 버튼들 -->
            <div class="navbar-actions">
                <!-- 검색 버튼 -->
                <button @click="toggleSearch" class="action-btn" :class="{ active: showSearch }" aria-label="검색">
                    <svg viewBox="0 0 24 24" class="icon">
                        <circle cx="11" cy="11" r="8"></circle>
                        <path d="m21 21-4.35-4.35"></path>
                    </svg>
                </button>

                <!-- 로그인 상태에 따라 분기 -->
                <template v-if="isLoggedIn">
                    <!-- 알림 버튼 -->
                    <button @click="toggleNotifications" class="action-btn notification-btn" aria-label="알림">
                        <svg viewBox="0 0 24 24" class="icon">
                            <path d="M18 8A6 6 0 0 0 6 8c0 7-3 9-3 9h18s-3-2-3-9"></path>
                            <path d="M13.73 21a2 2 0 0 1-3.46 0"></path>
                        </svg>
                        <span v-if="notificationCount > 0" class="badge">{{ notificationCount }}</span>
                    </button>

                    <!-- 프로필 버튼 -->
                    <button @click="toggleProfileMenu" class="profile-btn" aria-label="프로필">
                        <div class="avatar">
                            <img v-if="userProfileImage" :src="userProfileImage" :alt="userName" class="avatar-image" />
                            <span v-else>{{ userInitial }}</span>
                        </div>
                    </button>
                </template>

                <!-- 비로그인 상태: 로그인 버튼 -->
                <template v-else>
                    <button @click="openLoginModal" class="login-btn">
                        <svg viewBox="0 0 24 24" class="icon">
                            <path d="M15 3h4a2 2 0 0 1 2 2v14a2 2 0 0 1-2 2h-4"></path>
                            <polyline points="10 17 15 12 10 7"></polyline>
                            <line x1="15" y1="12" x2="3" y2="12"></line>
                        </svg>
                        <span>로그인</span>
                    </button>
                </template>
            </div>
        </div>

        <!-- 검색 바 (토글) -->
        <transition name="slide-down">
            <div v-if="showSearch" class="search-container">
                <div class="search-wrapper">
                    <svg viewBox="0 0 24 24" class="search-icon">
                        <circle cx="11" cy="11" r="8"></circle>
                        <path d="m21 21-4.35-4.35"></path>
                    </svg>
                    <input ref="searchInput" v-model="searchQuery" type="text" placeholder="할 일 검색..."
                        class="search-input" @keyup.escape="closeSearch" />
                    <button v-if="searchQuery" @click="clearSearch" class="clear-btn">
                        <svg viewBox="0 0 24 24">
                            <line x1="18" y1="6" x2="6" y2="18"></line>
                            <line x1="6" y1="6" x2="18" y2="18"></line>
                        </svg>
                    </button>
                </div>
            </div>
        </transition>

        <!-- 프로필 드롭다운 메뉴 -->
        <transition name="fade">
            <div v-if="showProfileMenu" class="dropdown-overlay" @click="closeProfileMenu">
                <div class="profile-dropdown" @click.stop>
                    <div class="profile-header">
                        <div class="profile-avatar">
                            <img v-if="userProfileImage" :src="userProfileImage" :alt="userName" class="avatar-image" />
                            <span v-else>{{ userInitial }}</span>
                        </div>
                        <div class="profile-info">
                            <p class="profile-name">{{ userName }}</p>
                            <p class="profile-email">{{ userEmail }}</p>
                        </div>
                    </div>
                    <div class="dropdown-divider"></div>
                    <nav class="dropdown-menu">
                        <a href="#" class="menu-item" @click.prevent="goToProfile">
                            <svg viewBox="0 0 24 24" class="menu-icon">
                                <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"></path>
                                <circle cx="12" cy="7" r="4"></circle>
                            </svg>
                            <span>프로필</span>
                        </a>
                        <a href="#" class="menu-item" @click.prevent="goToSettings">
                            <svg viewBox="0 0 24 24" class="menu-icon">
                                <circle cx="12" cy="12" r="3"></circle>
                                <path
                                    d="M12 1v6m0 6v6M3.93 3.93l4.24 4.24m5.66 5.66l4.24 4.24M1 12h6m6 0h6M3.93 20.07l4.24-4.24m5.66-5.66l4.24-4.24">
                                </path>
                            </svg>
                            <span>설정</span>
                        </a>
                        <a href="#" class="menu-item menu-item-logout" @click.prevent="logout">
                            <svg viewBox="0 0 24 24" class="menu-icon">
                                <path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4"></path>
                                <polyline points="16 17 21 12 16 7"></polyline>
                                <line x1="21" y1="12" x2="9" y2="12"></line>
                            </svg>
                            <span>로그아웃</span>
                        </a>
                    </nav>
                </div>
            </div>
        </transition>

        <!-- 로그인 모달 -->
        <LoginModal v-if="showLoginModal" v-model:show="showLoginModal" @login-success="handleLoginSuccess" />
    </header>
</template>

<script setup>
import { ref, computed, watch, nextTick, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import LoginModal from './LoginModal.vue'
import { auth } from '@/api/auth'
import { useAuthStore } from '@/stores/authStore';

const router = useRouter()
const authStore = useAuthStore();

// 상태 관리
const showSearch = ref(false)
const showProfileMenu = ref(false)
const showLoginModal = ref(false)
const searchQuery = ref('')
const searchInput = ref(null)
const notificationCount = ref(3)

// 로그인 상태 및 사용자 정보
const isLoggedIn = ref(false)
const userName = ref('')
const userEmail = ref('')
const userProfileImage = ref('')

// 컴포넌트 마운트 시 로그인 상태 확인
onMounted(() => {
    checkLoginStatus()
})

const checkLoginStatus = () => {
    const token = authStore.accessToken;
    const userData = authStore.user;

    if (token && userData) {
        isLoggedIn.value = true;
        userName.value = userData.name || userData.username || '사용자';
        userEmail.value = userData.email || '';
        userProfileImage.value = userData.profileImage || '';

        console.log('✅ 로그인 상태 확인:', {
            isLoggedIn: isLoggedIn.value,
            userName: userName.value,
            userEmail: userEmail.value
        });
    } else {
        console.log('❌ 로그인되지 않음');
        isLoggedIn.value = false;
        userName.value = '';
        userEmail.value = '';
        userProfileImage.value = '';
    }
};

// 인사말
const greeting = computed(() => {
    const hour = new Date().getHours()
    if (hour < 12) return '좋은 아침이에요 ☀️'
    if (hour < 18) return '좋은 오후에요 🌤️'
    return '좋은 저녁이에요 🌙'
})

// 사용자 이니셜
const userInitial = computed(() => {
    if (!userName.value) return 'U'
    return userName.value.charAt(0).toUpperCase()
})

// 로그인 모달 열기
const openLoginModal = () => {
    console.log('로그인 모달 열기')
    showLoginModal.value = true
}

// 로그인 성공 처리
const handleLoginSuccess = (userData) => {
    console.log('로그인 성공 처리:', userData)

    isLoggedIn.value = true
    userName.value = userData.username || userData.name || '사용자'
    userEmail.value = userData.email || ''
    userProfileImage.value = userData.profileImage || ''
    showLoginModal.value = false

    // localStorage에 저장 (이미 GoogleCallback에서 저장되었지만 일반 로그인용)
    if (userData.accessToken) {
        localStorage.setItem('accessToken', userData.accessToken)
    }
    if (userData.member) {
        localStorage.setItem('user', JSON.stringify(userData.member))
    }
}

// 검색 토글
const toggleSearch = async () => {
    showSearch.value = !showSearch.value
    if (showSearch.value) {
        await nextTick()
        searchInput.value?.focus()
    }
}

const closeSearch = () => {
    showSearch.value = false
    searchQuery.value = ''
}

const clearSearch = () => {
    searchQuery.value = ''
    searchInput.value?.focus()
}

// 알림 토글
const toggleNotifications = () => {
    router.push('/notifications')
}

// 프로필 메뉴 토글
const toggleProfileMenu = () => {
    showProfileMenu.value = !showProfileMenu.value
}

const closeProfileMenu = () => {
    showProfileMenu.value = false
}

// 메뉴 액션
const goToProfile = () => {
    router.push('/member/profile')
    closeProfileMenu()
}

const goToSettings = () => {
    router.push('/settings')
    closeProfileMenu()
}

const logout = () => {
    // localStorage 정리
    localStorage.removeItem('accessToken')
    localStorage.removeItem('user')

    // 상태 초기화
    isLoggedIn.value = false
    userName.value = ''
    userEmail.value = ''
    userProfileImage.value = ''

    closeProfileMenu()
    const response = auth.logout()

    console.log('✅ 로그아웃 완료')
    alert('로그아웃되었습니다.')

    // 메인 페이지로 이동
    router.push('/')
}

// 검색 감시
watch(searchQuery, (newValue) => {
    if (newValue.length > 0) {
        console.log('검색:', newValue)
    }
})

// 전역 이벤트로 로그인 상태 업데이트
window.addEventListener('storage', (e) => {
    if (e.key === 'accessToken' || e.key === 'user') {
        checkLoginStatus()
    }
})

// 라우터 변경 시 로그인 상태 재확인
watch(() => router.currentRoute.value.path, () => {
    checkLoginStatus()
})
</script>

<style scoped>
/* 프로필 이미지 스타일 */
.avatar-image {
    width: 100%;
    height: 100%;
    border-radius: 50%;
    object-fit: cover;
}

.profile-avatar .avatar-image {
    border: 2px solid rgba(255, 255, 255, 0.5);
}

/* 기존 스타일들 */
.navbar {
    position: sticky;
    top: 0;
    background: white;
    border-bottom: 1px solid #e2e8f0;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
    z-index: 50;
}

.navbar-container {
    max-width: 640px;
    margin: 0 auto;
    padding: 16px 20px;
    display: flex;
    justify-content: space-between;
    align-items: center;
}

.navbar-brand {
    display: flex;
    align-items: center;
    gap: 12px;
}

.brand-icon {
    width: 40px;
    height: 40px;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    border-radius: 12px;
    display: flex;
    align-items: center;
    justify-content: center;
    flex-shrink: 0;
}

.logo-icon {
    width: 24px;
    height: 24px;
    stroke: white;
    stroke-width: 2;
    stroke-linecap: round;
    stroke-linejoin: round;
    fill: none;
}

.brand-text {
    display: flex;
    flex-direction: column;
    gap: 2px;
}

.app-name {
    font-size: 20px;
    font-weight: 700;
    color: #1e293b;
    margin: 0;
    line-height: 1;
}

.app-subtitle {
    font-size: 12px;
    color: #64748b;
    margin: 0;
    line-height: 1;
}

.navbar-actions {
    display: flex;
    align-items: center;
    gap: 8px;
}

.action-btn {
    width: 40px;
    height: 40px;
    border: none;
    background: transparent;
    border-radius: 10px;
    cursor: pointer;
    display: flex;
    align-items: center;
    justify-content: center;
    transition: all 0.2s;
    position: relative;
}

.action-btn:hover {
    background: #f1f5f9;
}

.action-btn.active {
    background: #667eea;
}

.action-btn.active .icon {
    stroke: white;
}

.action-btn .icon {
    width: 20px;
    height: 20px;
    stroke: #64748b;
    stroke-width: 2;
    stroke-linecap: round;
    stroke-linejoin: round;
    fill: none;
    transition: stroke 0.2s;
}

/* 로그인 버튼 스타일 */
.login-btn {
    display: flex;
    align-items: center;
    gap: 6px;
    padding: 10px 18px;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    color: white;
    border: none;
    border-radius: 10px;
    font-size: 14px;
    font-weight: 600;
    cursor: pointer;
    transition: all 0.2s;
}

.login-btn:hover {
    transform: translateY(-1px);
    box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
}

.login-btn .icon {
    width: 18px;
    height: 18px;
    stroke: white;
    stroke-width: 2;
    stroke-linecap: round;
    stroke-linejoin: round;
    fill: none;
}

.notification-btn {
    position: relative;
}

.badge {
    position: absolute;
    top: 6px;
    right: 6px;
    background: #ef4444;
    color: white;
    font-size: 10px;
    font-weight: 700;
    min-width: 16px;
    height: 16px;
    padding: 0 4px;
    border-radius: 8px;
    display: flex;
    align-items: center;
    justify-content: center;
    border: 2px solid white;
}

.profile-btn {
    border: none;
    background: transparent;
    cursor: pointer;
    padding: 0;
}

.avatar {
    width: 40px;
    height: 40px;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    color: white;
    font-weight: 700;
    font-size: 16px;
    transition: all 0.2s;
    overflow: hidden;
}

.profile-btn:hover .avatar {
    transform: scale(1.05);
    box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
}

.search-container {
    max-width: 640px;
    margin: 0 auto;
    padding: 0 20px 16px;
}

.search-wrapper {
    position: relative;
    display: flex;
    align-items: center;
    background: #f8fafc;
    border-radius: 12px;
    padding: 0 16px;
    border: 2px solid transparent;
    transition: all 0.2s;
}

.search-wrapper:focus-within {
    background: white;
    border-color: #667eea;
    box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

.search-icon {
    width: 18px;
    height: 18px;
    stroke: #94a3b8;
    stroke-width: 2;
    stroke-linecap: round;
    fill: none;
    flex-shrink: 0;
}

.search-input {
    flex: 1;
    border: none;
    background: transparent;
    padding: 12px 12px 12px 8px;
    font-size: 15px;
    outline: none;
    color: #1e293b;
}

.search-input::placeholder {
    color: #94a3b8;
}

.clear-btn {
    width: 24px;
    height: 24px;
    border: none;
    background: #e2e8f0;
    border-radius: 6px;
    cursor: pointer;
    display: flex;
    align-items: center;
    justify-content: center;
    transition: all 0.2s;
    flex-shrink: 0;
}

.clear-btn:hover {
    background: #cbd5e1;
}

.clear-btn svg {
    width: 12px;
    height: 12px;
    stroke: #64748b;
    stroke-width: 2;
}

.dropdown-overlay {
    position: fixed;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: rgba(0, 0, 0, 0.3);
    backdrop-filter: blur(2px);
    z-index: 100;
    display: flex;
    justify-content: center;
    padding-top: 80px;
}

.profile-dropdown {
    position: absolute;
    top: 70px;
    right: 20px;
    background: white;
    border-radius: 16px;
    box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);
    min-width: 280px;
    overflow: hidden;
    animation: dropdown-appear 0.2s ease;
}

@keyframes dropdown-appear {
    from {
        opacity: 0;
        transform: translateY(-10px);
    }

    to {
        opacity: 1;
        transform: translateY(0);
    }
}

.profile-header {
    display: flex;
    align-items: center;
    gap: 12px;
    padding: 20px;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.profile-avatar {
    width: 48px;
    height: 48px;
    background: rgba(255, 255, 255, 0.2);
    backdrop-filter: blur(10px);
    border: 2px solid rgba(255, 255, 255, 0.5);
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    color: white;
    font-weight: 700;
    font-size: 20px;
    overflow: hidden;
}

.profile-info {
    flex: 1;
}

.profile-name {
    font-size: 16px;
    font-weight: 700;
    color: white;
    margin: 0 0 4px 0;
}

.profile-email {
    font-size: 13px;
    color: rgba(255, 255, 255, 0.9);
    margin: 0;
}

.dropdown-divider {
    height: 1px;
    background: #e2e8f0;
    margin: 0;
}

.dropdown-menu {
    padding: 8px;
}

.menu-item {
    display: flex;
    align-items: center;
    gap: 12px;
    padding: 12px 16px;
    border-radius: 8px;
    text-decoration: none;
    color: #1e293b;
    font-size: 14px;
    font-weight: 500;
    transition: all 0.2s;
    cursor: pointer;
}

.menu-item:hover {
    background: #f8fafc;
}

.menu-item-logout {
    color: #ef4444;
}

.menu-item-logout:hover {
    background: #fee2e2;
}

.menu-icon {
    width: 18px;
    height: 18px;
    stroke: currentColor;
    stroke-width: 2;
    stroke-linecap: round;
    stroke-linejoin: round;
    fill: none;
}

.slide-down-enter-active,
.slide-down-leave-active {
    transition: all 0.3s ease;
}

.slide-down-enter-from {
    opacity: 0;
    transform: translateY(-10px);
}

.slide-down-leave-to {
    opacity: 0;
    transform: translateY(-10px);
}

.fade-enter-active,
.fade-leave-active {
    transition: opacity 0.2s;
}

.fade-enter-from,
.fade-leave-to {
    opacity: 0;
}

@media (max-width: 480px) {
    .navbar-container {
        padding: 12px 16px;
    }

    .app-subtitle {
        display: none;
    }

    .profile-dropdown {
        right: 16px;
        left: 16px;
        min-width: auto;
    }

    .login-btn span {
        display: none;
    }

    .login-btn {
        padding: 10px;
        width: 40px;
        height: 40px;
    }
}
</style>
