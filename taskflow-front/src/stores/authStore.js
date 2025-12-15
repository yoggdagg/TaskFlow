// stores/auth.js
import { defineStore } from "pinia";
import { ref, computed } from "vue";
import { auth } from "@/api/auth";

export const useAuthStore = defineStore("auth", () => {
  // State
  const user = ref(null);
  const accessToken = ref(null); // 메모리에만 저장
  const isAuthenticated = computed(() => !!accessToken.value);
  let refreshTokenTimeout = null;

  // 로그인
  const login = async (email, password) => {
    try {
      const response = await auth.login(email, password);

      // Access Token만 메모리(Pinia)에 저장
      accessToken.value = response.accessToken;
      user.value = {
        name: response.name || response.username,
        email: response.email,
        id: response.id,
      };

      // Refresh Token은 서버가 HttpOnly Cookie로 자동 설정
      // Access Token 자동 갱신 타이머 시작
      startRefreshTokenTimer();

      return response;
    } catch (error) {
      throw error;
    }
  };

  // Refresh Token으로 Access Token 갱신
  const refreshAccessToken = async () => {
    try {
      const response = await auth.refresh();
      accessToken.value = response.accessToken;

      // 갱신 성공 시 타이머 재시작
      startRefreshTokenTimer();

      return response;
    } catch (error) {
      // Refresh Token도 만료된 경우 로그아웃
      console.error("토큰 갱신 실패:", error);
      logout();
      throw error;
    }
  };

  // Access Token 만료 전 자동 갱신 타이머
  const startRefreshTokenTimer = () => {
    if (!accessToken.value) return;

    try {
      // JWT 디코딩하여 만료 시간 확인
      const jwtToken = JSON.parse(atob(accessToken.value.split(".")[1]));
      const expires = new Date(jwtToken.exp * 1000);
      const now = Date.now();

      // 만료 1분 전에 갱신 (최소 10초는 확보)
      const timeout = Math.max(expires.getTime() - now - 60 * 1000, 10000);

      // 기존 타이머 클리어
      if (refreshTokenTimeout) {
        clearTimeout(refreshTokenTimeout);
      }

      refreshTokenTimeout = setTimeout(() => {
        refreshAccessToken();
      }, timeout);

      console.log(`토큰 자동 갱신 예정: ${Math.round(timeout / 1000)}초 후`);
    } catch (error) {
      console.error("토큰 타이머 설정 실패:", error);
    }
  };

  // 로그아웃
  const logout = async () => {
    try {
      // 서버에 로그아웃 요청 (HttpOnly Cookie 삭제)
      await auth.logout();
    } catch (error) {
      console.error("로그아웃 요청 실패:", error);
    } finally {
      // 로컬 상태 초기화
      accessToken.value = null;
      user.value = null;

      // 타이머 정리
      if (refreshTokenTimeout) {
        clearTimeout(refreshTokenTimeout);
        refreshTokenTimeout = null;
      }
    }
  };

  // 앱 시작 시 토큰 복구 시도 (새로고침 대응)
  const initAuth = async () => {
    try {
      // HttpOnly Cookie에 Refresh Token이 있으면 자동 로그인
      const response = await auth.refresh();
      accessToken.value = response.accessToken;
      user.value = response.user;
      startRefreshTokenTimer();

      console.log("자동 로그인 성공");
      return true;
    } catch (error) {
      console.log("저장된 세션 없음");
      return false;
    }
  };

  return {
    // State
    user,
    accessToken,
    isAuthenticated,

    // Actions
    login,
    logout,
    refreshAccessToken,
    initAuth,
  };
});
