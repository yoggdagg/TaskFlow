// api/auth.js
import axios from "axios";

const authApi = axios.create({
  baseURL: import.meta.env.VITE_API_URL || "http://localhost:8080",
  timeout: 10000,
  headers: {
    "Content-Type": "application/json",
  },
  withCredentials: true, // 쿠키 전송 활성화
});

// 요청 인터셉터: Access Token 자동 추가
authApi.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem("accessToken");
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// 응답 인터셉터: 401 에러 시 토큰 갱신 시도
authApi.interceptors.response.use(
  (response) => response,
  async (error) => {
    const originalRequest = error.config;

    // 401 에러 && 재시도하지 않은 요청
    if (error.response?.status === 401 && !originalRequest._retry) {
      originalRequest._retry = true;

      try {
        // Refresh Token으로 새 Access Token 받기
        const response = await axios.post(
          `${import.meta.env.VITE_API_URL}/api/member/refresh`,
          {},
          { withCredentials: true }
        );

        const { accessToken } = response.data;
        localStorage.setItem("accessToken", accessToken);

        // 원래 요청 재시도
        originalRequest.headers.Authorization = `Bearer ${accessToken}`;
        return authApi(originalRequest);
      } catch (refreshError) {
        // Refresh Token도 만료됨 - 로그아웃 처리
        localStorage.removeItem("accessToken");
        localStorage.removeItem("user");
        window.location.href = "/login";
        return Promise.reject(refreshError);
      }
    }

    return Promise.reject(error);
  }
);

export const auth = {
  /**
   * 일반 로그인
   */
  async login(email, password) {
    try {
      const response = await authApi.post("/api/member/login", {
        email: email,
        password: password,
      });

      // localStorage에 저장
      if (response.data.accessToken) {
        localStorage.setItem("accessToken", response.data.accessToken);
      }
      if (response.data.member) {
        localStorage.setItem("user", JSON.stringify(response.data.member));
      }

      return response.data;
    } catch (error) {
      throw error;
    }
  },

  /**
   * 회원가입
   */
  async register(email, password, name) {
    try {
      const response = await authApi.post("/api/member/regist", {
        email: email,
        password: password,
        username: name, // 백엔드에서 username 필드 사용
      });
      return response.data;
    } catch (error) {
      throw error;
    }
  },

  /**
   * Access Token 갱신
   */
  async refresh() {
    try {
      const response = await authApi.post("/api/member/refresh");

      // 새 Access Token 저장
      if (response.data.accessToken) {
        localStorage.setItem("accessToken", response.data.accessToken);
      }

      return response.data;
    } catch (error) {
      throw error;
    }
  },

  /**
   * 로그아웃 - Refresh Token 쿠키 삭제
   */
  async logout() {
    try {
      const response = await authApi.post("/api/member/logout");

      console.log("✅ 백엔드 로그아웃 성공:", response.data);

      // localStorage 정리
      localStorage.removeItem("accessToken");
      localStorage.removeItem("user");

      return response.data;
    } catch (error) {
      console.error("❌ Logout error:", error);

      // 에러가 발생해도 프론트엔드는 로그아웃 처리
      localStorage.removeItem("accessToken");
      localStorage.removeItem("user");

      throw error;
    }
  },
};
