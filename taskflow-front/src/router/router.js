// /srcs/router/router.js
import { createWebHistory, createRouter } from "vue-router";
import GoogleCallback from "@/views/oauth/GoogleCallback.vue";
import KakaoCallback from "@/views/oauth/KakaoCallback.vue";
import NaverCallback from "@/views/oauth/NaverCallback.vue";
import { useAuthStore } from "@/stores/authStore";
import Profile from "@/views/Profile.vue";

// lazy 로딩으로 처리
const routes = [
  {
    path: "/",
    name: "Home",
    component: () => import("@/views/Home.vue"),
  },
  {
    path: "/signup",
    name: "Signup",
    component: () => import("@/views/Signup.vue"),
  },
  {
    path: "/oauth/google/callback",
    name: "GoogleCallback",
    component: GoogleCallback,
  },
  {
    path: "/oauth/kakao/callback",
    name: "KakaoCallback",
    component: KakaoCallback,
  },
  {
    path: "/oauth/naver/callback",
    name: "NaverCallback",
    component: NaverCallback,
  },
  {
    path: "/member/profile",
    name: "Profile",
    component: Profile,
    meta: {
      requiresAuth: true,
    },
  },
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

// 토큰 유효성 검증 함수
const isValidToken = (token) => {
  if (!token) return false;

  try {
    // JWT 토큰 파싱
    const payload = JSON.parse(atob(token.split(".")[1]));
    const exp = payload.exp * 1000; // 초를 밀리초로 변환

    // 만료 확인
    if (Date.now() >= exp) {
      console.log("⚠️ Access Token 만료됨");
      return false;
    }

    return true;
  } catch (error) {
    console.error("❌ 토큰 검증 실패:", error);
    return false;
  }
};

// 네비게이션 가드
router.beforeEach((to, from) => {
  if (to.meta.requiresAuth) {
    const authStore = useAuthStore();
    const token = authStore.accessToken;

    console.log("🔍 현재 토큰:", token);
    console.log("🔍 스토어 상태:", authStore);
    console.log("🔍 isAuthenticated:", authStore.isAuthenticated);
    // 토큰 유효성 검증
    if (!isValidToken(token)) {
      console.log("❌ 인증 실패 - 로그인 페이지로 이동");
      alert("로그인이 필요한 페이지입니다.");

      // 로그인 페이지로 리다이렉트 (원래 가려던 경로 저장)
      return {
        path: "/",
        query: { redirect: to.fullPath },
      };
    } else {
      console.log("✅ 인증 성공 - 페이지 접근 허용");
      return;
    }
  } else {
    // 인증이 필요 없는 경우
    return;
  }
});

export default router;
