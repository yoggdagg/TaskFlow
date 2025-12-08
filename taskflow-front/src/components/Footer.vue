<template>
    <footer class="app-footer">
        <nav class="nav-container">
            <button v-for="item in navItems" :key="item.name" @click="navigateTo(item.path)" class="nav-item"
                :class="{ active: isActive(item.path) }">
                <div class="nav-icon">
                    <svg viewBox="0 0 24 24" class="icon">
                        <component :is="item.icon" />
                    </svg>
                </div>
                <span class="nav-label">{{ item.label }}</span>
                <div v-if="isActive(item.path)" class="active-indicator"></div>
            </button>
        </nav>
    </footer>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'

const router = useRouter()
const route = useRoute()

// 네비게이션 아이템
const navItems = ref([
    {
        name: 'home',
        path: '/',
        label: '홈',
        icon: 'HomeIcon'
    },
    {
        name: 'tasks',
        path: '/tasks',
        label: '할 일',
        icon: 'TasksIcon'
    },
    {
        name: 'calendar',
        path: '/calendar',
        label: '캘린더',
        icon: 'CalendarIcon'
    },
    {
        name: 'stats',
        path: '/stats',
        label: '통계',
        icon: 'StatsIcon'
    },
    {
        name: 'settings',
        path: '/settings',
        label: '설정',
        icon: 'SettingsIcon'
    }
])

// 현재 활성 경로 확인
const isActive = (path) => {
    return route.path === path
}

// 페이지 이동
const navigateTo = (path) => {
    if (route.path !== path) {
        router.push(path)
    }
}
</script>

<script>
// SVG 아이콘 컴포넌트들
export default {
    components: {
        HomeIcon: {
            template: `
        <g>
          <path d="M3 9l9-7 9 7v11a2 2 0 01-2 2H5a2 2 0 01-2-2z"></path>
          <polyline points="9 22 9 12 15 12 15 22"></polyline>
        </g>
      `
        },
        TasksIcon: {
            template: `
        <g>
          <path d="M9 11l3 3L22 4"></path>
          <path d="M21 12v7a2 2 0 01-2 2H5a2 2 0 01-2-2V5a2 2 0 012-2h11"></path>
        </g>
      `
        },
        CalendarIcon: {
            template: `
        <g>
          <rect x="3" y="4" width="18" height="18" rx="2" ry="2"></rect>
          <line x1="16" y1="2" x2="16" y2="6"></line>
          <line x1="8" y1="2" x2="8" y2="6"></line>
          <line x1="3" y1="10" x2="21" y2="10"></line>
        </g>
      `
        },
        StatsIcon: {
            template: `
        <g>
          <line x1="18" y1="20" x2="18" y2="10"></line>
          <line x1="12" y1="20" x2="12" y2="4"></line>
          <line x1="6" y1="20" x2="6" y2="14"></line>
        </g>
      `
        },
        SettingsIcon: {
            template: `
        <g>
          <circle cx="12" cy="12" r="3"></circle>
          <path d="M12 1v6m0 6v6M3.93 3.93l4.24 4.24m5.66 5.66l4.24 4.24M1 12h6m6 0h6M3.93 20.07l4.24-4.24m5.66-5.66l4.24-4.24"></path>
        </g>
      `
        }
    }
}
</script>

<style scoped>
.app-footer {
    position: fixed;
    bottom: 0;
    left: 0;
    right: 0;
    background: white;
    border-top: 1px solid #e2e8f0;
    box-shadow: 0 -4px 12px rgba(0, 0, 0, 0.05);
    z-index: 100;
    padding-bottom: env(safe-area-inset-bottom);
}

.nav-container {
    display: flex;
    justify-content: space-around;
    align-items: center;
    max-width: 640px;
    margin: 0 auto;
    padding: 8px 16px;
}

.nav-item {
    position: relative;
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 4px;
    padding: 8px 12px;
    border: none;
    background: transparent;
    cursor: pointer;
    transition: all 0.2s;
    border-radius: 12px;
    flex: 1;
    max-width: 80px;
}

.nav-item:active {
    transform: scale(0.95);
}

.nav-icon {
    width: 24px;
    height: 24px;
    display: flex;
    align-items: center;
    justify-content: center;
    transition: all 0.2s;
}

.icon {
    width: 100%;
    height: 100%;
    stroke: #94a3b8;
    stroke-width: 2;
    stroke-linecap: round;
    stroke-linejoin: round;
    fill: none;
    transition: all 0.2s;
}

.nav-item.active .icon {
    stroke: #667eea;
    stroke-width: 2.5;
}

.nav-label {
    font-size: 11px;
    color: #94a3b8;
    font-weight: 500;
    transition: all 0.2s;
}

.nav-item.active .nav-label {
    color: #667eea;
    font-weight: 600;
}

.active-indicator {
    position: absolute;
    top: 4px;
    width: 4px;
    height: 4px;
    background: #667eea;
    border-radius: 50%;
    animation: indicator-pop 0.3s ease;
}

@keyframes indicator-pop {
    0% {
        transform: scale(0);
    }

    50% {
        transform: scale(1.5);
    }

    100% {
        transform: scale(1);
    }
}

/* 호버 효과 (데스크톱) */
@media (hover: hover) {
    .nav-item:hover {
        background: #f8fafc;
    }

    .nav-item:hover .icon {
        stroke: #667eea;
        transform: translateY(-2px);
    }

    .nav-item:hover .nav-label {
        color: #667eea;
    }
}

/* 반응형 */
@media (max-width: 480px) {
    .nav-container {
        padding: 6px 8px;
    }

    .nav-item {
        padding: 6px 8px;
        gap: 2px;
    }

    .nav-icon {
        width: 22px;
        height: 22px;
    }

    .nav-label {
        font-size: 10px;
    }
}

/* 큰 화면에서는 사이드바 스타일로 변경 */
@media (min-width: 768px) {
    .app-footer {
        position: fixed;
        top: 0;
        bottom: 0;
        left: 0;
        right: auto;
        width: 80px;
        border-top: none;
        border-right: 1px solid #e2e8f0;
        box-shadow: 4px 0 12px rgba(0, 0, 0, 0.05);
    }

    .nav-container {
        flex-direction: column;
        height: 100%;
        padding: 24px 8px;
        justify-content: flex-start;
        gap: 8px;
    }

    .nav-item {
        max-width: 100%;
        width: 100%;
        padding: 12px 8px;
    }

    .nav-icon {
        width: 28px;
        height: 28px;
    }

    .nav-label {
        font-size: 12px;
    }

    .active-indicator {
        left: 0;
        top: 50%;
        transform: translateY(-50%);
        width: 3px;
        height: 60%;
        border-radius: 0 3px 3px 0;
    }
}

/* iOS 세이프 에리어 대응 */
@supports (padding-bottom: env(safe-area-inset-bottom)) {
    .app-footer {
        padding-bottom: calc(8px + env(safe-area-inset-bottom));
    }
}
</style>
