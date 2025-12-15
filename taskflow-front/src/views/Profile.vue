<!-- src/views/Profile.vue -->
<template>
    <div class="profile-page">
        <div class="profile-card" v-if="!loading && profile">
            <div class="header">
                <div class="avatar">
                    <span v-if="initials">{{ initials }}</span>
                </div>
                <div class="user-main">
                    <h1 class="username">{{ profile.name || profile.username }}</h1>
                    <p class="email">{{ profile.email }}</p>
                </div>
            </div>

            <div class="info-grid">
                <div class="info-item">
                    <span class="label">역할</span>
                    <span class="value">{{ profile.role || 'USER' }}</span>
                </div>
                <div class="info-item">
                    <span class="label">가입일</span>
                    <span class="value">{{ formattedCreatedAt }}</span>
                </div>
            </div>
        </div>

        <!-- 로딩 중 -->
        <div v-else-if="loading" class="loading">
            <div class="spinner"></div>
            <p>프로필 정보를 불러오는 중입니다...</p>
        </div>

        <!-- 비로그인 또는 오류 -->
        <div v-else class="error">
            <p>프로필 정보를 가져오지 못했습니다. 다시 로그인해 주세요.</p>
        </div>
    </div>
</template>

<script setup>
import { ref, computed, onMounted } from "vue";
import { memberApi } from "@/api/index";

const loading = ref(true);
const profile = ref(null);

const initials = computed(() => {
    if (!profile.value || !profile.value.name) return "";
    const parts = profile.value.name.split(" ");
    if (parts.length === 1) return parts[0].charAt(0).toUpperCase();
    return (
        parts[0].charAt(0).toUpperCase() +
        parts[parts.length - 1].charAt(0).toUpperCase()
    );
});

const formattedCreatedAt = computed(() => {
    if (!profile.value || !profile.value.createdAt) return "-";
    const d = new Date(profile.value.createdAt);
    return d.toLocaleDateString();
});

onMounted(async () => {
    try {
        const { data } = await fetchProfile(); // /api/member/profile 호출
        profile.value = data;
    } catch (e) {
        console.error("프로필 조회 실패:", e);
        profile.value = null;
    } finally {
        loading.value = false;
    }
});
</script>

<style scoped>
.profile-page {
    min-height: 100vh;
    padding: 32px 16px;
    display: flex;
    justify-content: center;
    align-items: flex-start;
    background: linear-gradient(135deg, #0f172a 0%, #111827 50%, #020617 100%);
    color: #e5e7eb;
}

.profile-card {
    width: 100%;
    max-width: 720px;
    background: rgba(15, 23, 42, 0.9);
    border-radius: 24px;
    padding: 28px 24px 32px;
    box-shadow:
        0 24px 60px rgba(15, 23, 42, 0.8),
        0 0 0 1px rgba(148, 163, 184, 0.3);
    backdrop-filter: blur(20px);
}

.header {
    display: flex;
    gap: 18px;
    align-items: center;
    margin-bottom: 24px;
}

.avatar {
    width: 64px;
    height: 64px;
    border-radius: 999px;
    background: radial-gradient(circle at 0% 0%, #4f46e5, #06b6d4);
    display: flex;
    align-items: center;
    justify-content: center;
    font-weight: 700;
    font-size: 24px;
    color: white;
    box-shadow:
        0 12px 30px rgba(59, 130, 246, 0.7),
        0 0 0 3px rgba(56, 189, 248, 0.4);
}

.user-main .username {
    font-size: 1.6rem;
    font-weight: 700;
    margin: 0;
}

.user-main .email {
    margin-top: 4px;
    font-size: 0.95rem;
    color: #9ca3af;
}

.info-grid {
    margin-top: 20px;
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(180px, 1fr));
    gap: 12px 20px;
}

.info-item {
    display: flex;
    flex-direction: column;
    gap: 2px;
}

.label {
    font-size: 0.8rem;
    text-transform: uppercase;
    letter-spacing: 0.08em;
    color: #6b7280;
}

.value {
    font-size: 0.98rem;
    color: #e5e7eb;
}

.loading,
.error {
    margin-top: 80px;
    text-align: center;
    color: #e5e7eb;
}

.spinner {
    margin: 0 auto 12px;
    width: 28px;
    height: 28px;
    border-radius: 50%;
    border: 3px solid rgba(148, 163, 184, 0.4);
    border-top-color: #60a5fa;
    animation: spin 0.9s linear infinite;
}

@keyframes spin {
    to {
        transform: rotate(360deg);
    }
}

@media (max-width: 768px) {
    .profile-page {
        padding: 24px 10px;
    }

    .profile-card {
        padding: 22px 18px 26px;
    }
}
</style>
