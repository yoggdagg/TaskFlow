// src/api/index.js
import instance from "./interceptors";

// 회원 관련 API
export const memberApi = {
  // 프로필 조회
  getProfile: () => instance.get("/api/member/profile"),

  // 로그인
  login: (email, password) =>
    instance.post("/api/member/login", { email, password }),

  // 로그아웃
  logout: () => instance.post("/api/member/logout"),

  // 회원가입
  register: (userData) => instance.post("/api/member/regist", userData),

  // 회원정보 조회
  fetchMyInfo: () => instance.get("/api/member/fetchMyInfo"),
};

// OAuth 관련 API
export const oauthApi = {
  // 구글 콜백 처리
  googleCallback: (code) =>
    instance.get("/oauth/google/callback", { params: { code } }),

  // 토큰 갱신
  refreshToken: () =>
    instance.post("/auth/refresh", {}, { withCredentials: true }),

  // OAuth 로그아웃
  logout: () => instance.post("/auth/logout", {}, { withCredentials: true }),
};
