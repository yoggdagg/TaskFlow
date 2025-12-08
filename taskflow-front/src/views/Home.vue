<template>
    <div class="home-container">
        <!-- 오늘의 요약 카드 -->
        <div class="summary-card">
            <div class="summary-header">
                <h2 class="summary-title">오늘의 현황</h2>
                <div class="progress-badge">
                    <span class="progress-text">{{ completionRate }}%</span>
                </div>
            </div>
            <div class="stats-grid">
                <div class="stat-item">
                    <span class="stat-icon">📋</span>
                    <div class="stat-content">
                        <span class="stat-value">{{ totalTasks }}</span>
                        <span class="stat-label">전체</span>
                    </div>
                </div>
                <div class="stat-item">
                    <span class="stat-icon">✅</span>
                    <div class="stat-content">
                        <span class="stat-value">{{ completedTasks }}</span>
                        <span class="stat-label">완료</span>
                    </div>
                </div>
                <div class="stat-item">
                    <span class="stat-icon">⏰</span>
                    <div class="stat-content">
                        <span class="stat-value">{{ pendingTasks }}</span>
                        <span class="stat-label">진행중</span>
                    </div>
                </div>
            </div>
        </div>

        <!-- 할 일 추가 -->
        <div class="add-task-card">
            <div class="add-task-wrapper">
                <input v-model="newTaskTitle" @keyup.enter="addTask" type="text" placeholder="새로운 할 일을 입력하세요..."
                    class="task-input" />
                <button @click="addTask" class="add-button" :disabled="!newTaskTitle.trim()">
                    <svg class="add-icon" viewBox="0 0 24 24">
                        <line x1="12" y1="5" x2="12" y2="19"></line>
                        <line x1="5" y1="12" x2="19" y2="12"></line>
                    </svg>
                </button>
            </div>
        </div>

        <!-- 할 일 목록 -->
        <div class="tasks-container">
            <div class="tasks-header">
                <h3 class="tasks-title">할 일 목록</h3>
                <span class="tasks-count">{{ pendingTasks }}개 남음</span>
            </div>

            <transition-group name="task" tag="div" class="task-list">
                <div v-for="task in tasks" :key="task.id" class="task-item"
                    :class="{ 'task-completed': task.completed }">
                    <div class="task-main" @click="toggleTask(task.id)">
                        <div class="task-checkbox">
                            <div v-if="task.completed" class="checkbox-check">
                                <svg viewBox="0 0 24 24">
                                    <polyline points="20 6 9 17 4 12"></polyline>
                                </svg>
                            </div>
                        </div>
                        <span class="task-text">{{ task.title }}</span>
                    </div>
                    <button @click.stop="deleteTask(task.id)" class="task-delete">
                        <svg viewBox="0 0 24 24">
                            <path d="M3 6h18M19 6v14a2 2 0 01-2 2H7a2 2 0 01-2-2V6m3 0V4a2 2 0 012-2h4a2 2 0 012 2v2">
                            </path>
                        </svg>
                    </button>
                </div>
            </transition-group>

            <!-- Empty State -->
            <div v-if="tasks.length === 0" class="empty-state">
                <div class="empty-icon">🎯</div>
                <p class="empty-title">할 일이 없습니다</p>
                <p class="empty-desc">위에서 새로운 작업을 추가해보세요</p>
            </div>
        </div>
    </div>
</template>

<script setup>
import { ref, computed } from 'vue'

// 할 일 목록
const tasks = ref([
    { id: 1, title: 'Vue 3 Composition API 학습', completed: false },
    { id: 2, title: 'Spring Boot 백엔드 API 구축', completed: true },
    { id: 3, title: 'PWA manifest 설정', completed: false },
    { id: 4, title: 'JWT 인증 구현', completed: false }
])

const newTaskTitle = ref('')

// 통계 계산
const totalTasks = computed(() => tasks.value.length)
const completedTasks = computed(() => tasks.value.filter(t => t.completed).length)
const pendingTasks = computed(() => tasks.value.filter(t => !t.completed).length)
const completionRate = computed(() =>
    totalTasks.value === 0 ? 0 : Math.round((completedTasks.value / totalTasks.value) * 100)
)

// 할 일 추가
const addTask = () => {
    const title = newTaskTitle.value.trim()
    if (title) {
        tasks.value.unshift({
            id: Date.now(),
            title,
            completed: false
        })
        newTaskTitle.value = ''
    }
}

// 완료 토글
const toggleTask = (id) => {
    const task = tasks.value.find(t => t.id === id)
    if (task) {
        task.completed = !task.completed
    }
}

// 할 일 삭제
const deleteTask = (id) => {
    tasks.value = tasks.value.filter(t => t.id !== id)
}
</script>

<style scoped>
.home-container {
    max-width: 640px;
    margin: 0 auto;
    padding: 20px;
    background: #f8fafc;
    min-height: 100vh;
}

/* 요약 카드 */
.summary-card {
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    border-radius: 24px;
    padding: 24px;
    margin-bottom: 20px;
    box-shadow: 0 8px 24px rgba(102, 126, 234, 0.3);
}

.summary-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;
}

.summary-title {
    font-size: 20px;
    font-weight: 700;
    color: white;
    margin: 0;
}

.progress-badge {
    background: rgba(255, 255, 255, 0.2);
    backdrop-filter: blur(10px);
    padding: 8px 16px;
    border-radius: 20px;
    border: 1px solid rgba(255, 255, 255, 0.3);
}

.progress-text {
    font-size: 16px;
    font-weight: 700;
    color: white;
}

.stats-grid {
    display: grid;
    grid-template-columns: repeat(3, 1fr);
    gap: 12px;
}

.stat-item {
    background: rgba(255, 255, 255, 0.15);
    backdrop-filter: blur(10px);
    border-radius: 16px;
    padding: 16px 12px;
    text-align: center;
    border: 1px solid rgba(255, 255, 255, 0.2);
}

.stat-icon {
    font-size: 32px;
    display: block;
    margin-bottom: 8px;
}

.stat-content {
    display: flex;
    flex-direction: column;
    gap: 2px;
}

.stat-value {
    font-size: 24px;
    font-weight: 700;
    color: white;
}

.stat-label {
    font-size: 12px;
    color: rgba(255, 255, 255, 0.9);
}

/* 할 일 추가 카드 */
.add-task-card {
    background: white;
    border-radius: 20px;
    padding: 20px;
    margin-bottom: 20px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.add-task-wrapper {
    display: flex;
    gap: 12px;
    align-items: center;
}

.task-input {
    flex: 1;
    padding: 14px 18px;
    border: 2px solid #e2e8f0;
    border-radius: 12px;
    font-size: 15px;
    outline: none;
    transition: all 0.2s;
    font-family: inherit;
}

.task-input:focus {
    border-color: #667eea;
    background: #f8f9ff;
}

.task-input::placeholder {
    color: #94a3b8;
}

.add-button {
    width: 48px;
    height: 48px;
    border: none;
    border-radius: 12px;
    background: #667eea;
    cursor: pointer;
    display: flex;
    align-items: center;
    justify-content: center;
    transition: all 0.2s;
    flex-shrink: 0;
}

.add-button:hover:not(:disabled) {
    background: #5568d3;
    transform: scale(1.05);
}

.add-button:active:not(:disabled) {
    transform: scale(0.95);
}

.add-button:disabled {
    background: #cbd5e1;
    cursor: not-allowed;
}

.add-icon {
    width: 20px;
    height: 20px;
    stroke: white;
    stroke-width: 3;
    stroke-linecap: round;
}

/* 할 일 목록 컨테이너 */
.tasks-container {
    background: white;
    border-radius: 20px;
    padding: 24px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
    min-height: 400px;
}

.tasks-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;
}

.tasks-title {
    font-size: 18px;
    font-weight: 700;
    color: #1e293b;
    margin: 0;
}

.tasks-count {
    font-size: 14px;
    color: #64748b;
    background: #f1f5f9;
    padding: 4px 12px;
    border-radius: 12px;
    font-weight: 500;
}

/* 할 일 아이템 */
.task-list {
    display: flex;
    flex-direction: column;
    gap: 10px;
}

.task-item {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 16px;
    background: #f8fafc;
    border-radius: 14px;
    border: 2px solid transparent;
    transition: all 0.2s;
}

.task-item:hover {
    background: #f1f5f9;
    border-color: #e2e8f0;
}

.task-item.task-completed {
    opacity: 0.6;
}

.task-main {
    display: flex;
    align-items: center;
    gap: 14px;
    flex: 1;
    cursor: pointer;
    user-select: none;
}

.task-checkbox {
    width: 22px;
    height: 22px;
    border: 2px solid #cbd5e1;
    border-radius: 6px;
    display: flex;
    align-items: center;
    justify-content: center;
    transition: all 0.2s;
    flex-shrink: 0;
}

.task-completed .task-checkbox {
    background: #667eea;
    border-color: #667eea;
}

.checkbox-check svg {
    width: 14px;
    height: 14px;
    stroke: white;
    stroke-width: 3;
    fill: none;
    stroke-linecap: round;
    stroke-linejoin: round;
}

.task-text {
    font-size: 15px;
    color: #1e293b;
    line-height: 1.5;
    word-break: break-word;
}

.task-completed .task-text {
    text-decoration: line-through;
    color: #94a3b8;
}

.task-delete {
    width: 36px;
    height: 36px;
    border: none;
    background: transparent;
    color: #94a3b8;
    cursor: pointer;
    border-radius: 8px;
    display: flex;
    align-items: center;
    justify-content: center;
    transition: all 0.2s;
    flex-shrink: 0;
}

.task-delete:hover {
    background: #fee2e2;
    color: #ef4444;
}

.task-delete svg {
    width: 18px;
    height: 18px;
    stroke: currentColor;
    stroke-width: 2;
    fill: none;
    stroke-linecap: round;
    stroke-linejoin: round;
}

/* Empty State */
.empty-state {
    text-align: center;
    padding: 60px 20px;
}

.empty-icon {
    font-size: 72px;
    margin-bottom: 16px;
    opacity: 0.5;
}

.empty-title {
    font-size: 18px;
    font-weight: 600;
    color: #64748b;
    margin: 0 0 8px 0;
}

.empty-desc {
    font-size: 14px;
    color: #94a3b8;
    margin: 0;
}

/* 애니메이션 */
.task-enter-active {
    transition: all 0.3s ease-out;
}

.task-leave-active {
    transition: all 0.2s ease-in;
}

.task-enter-from {
    opacity: 0;
    transform: translateY(-20px);
}

.task-leave-to {
    opacity: 0;
    transform: translateX(20px);
}

/* 반응형 */
@media (max-width: 480px) {
    .home-container {
        padding: 16px;
    }

    .summary-card {
        padding: 20px;
    }

    .summary-title {
        font-size: 18px;
    }

    .stat-icon {
        font-size: 28px;
    }

    .stat-value {
        font-size: 20px;
    }

    .tasks-container {
        padding: 20px;
    }
}
</style>
