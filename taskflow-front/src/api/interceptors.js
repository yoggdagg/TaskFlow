// src/api/interceptors.js
import axios from "axios";
import router from "@/router/router";
import { useAuthStore } from "@/stores/authStore";

// axios 인스턴스 생성
const instance = axios.create({
  baseURL: import.meta.env.VITE_API_URL || "http://localhost:8080",
  timeout: 10000,
  headers: {
    "Content-Type": "application/json",
  },
  withCredentials: true, // 쿠키 전송을 위해 필수
});

// 토큰 갱신 중복 요청 방지
let isRefreshing = false;
let failedQueue = [];

const processQueue = (error, token = null) => {
  failedQueue.forEach((prom) => {
    if (error) {
      prom.reject(error);
    } else {
      prom.resolve(token);
    }
  });
  failedQueue = [];
};

// Request Interceptor - 모든 요청에 Access Token 자동 추가
instance.interceptors.request.use(
  (config) => {
    const authStore = useAuthStore();
    const accessToken = authStore.accessToken;

    if (accessToken) {
      config.headers["Authorization"] = `Bearer ${accessToken}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Response Interceptor - 토큰 만료 시 자동 갱신
instance.interceptors.response.use(
  (response) => {
    return response;
  },
  async (error) => {
    const originalRequest = error.config;
    const authStore = useAuthStore();

    // 401 에러이고, 재시도하지 않은 요청일 경우
    if (error.response?.status === 401 && !originalRequest._retry) {
      // 토큰 갱신 중인 경우, 대기열에 추가
      if (isRefreshing) {
        return new Promise((resolve, reject) => {
          failedQueue.push({ resolve, reject });
        })
          .then((token) => {
            originalRequest.headers["Authorization"] = `Bearer ${token}`;
            return instance(originalRequest);
          })
          .catch((err) => {
            return Promise.reject(err);
          });
      }

      originalRequest._retry = true;
      isRefreshing = true;

      try {
        // Refresh Token은 HttpOnly 쿠키로 자동 전송됨
        const response = await axios.post(
          `${
            import.meta.env.VITE_API_URL || "http://localhost:8080"
          }/auth/refresh`,
          {},
          { withCredentials: true }
        );

        const { accessToken } = response.data;

        // 새 Access Token을 Pinia에 저장
        authStore.setAccessToken(accessToken);

        // 대기 중인 요청들 처리
        processQueue(null, accessToken);

        // 원래 요청에 새 토큰 적용 후 재시도
        originalRequest.headers["Authorization"] = `Bearer ${accessToken}`;
        return instance(originalRequest);
      } catch (refreshError) {
        // Refresh Token도 만료된 경우
        processQueue(refreshError, null);
        authStore.logout();
        router.push("/login");
        return Promise.reject(refreshError);
      } finally {
        isRefreshing = false;
      }
    }

    return Promise.reject(error);
  }
);

export default instance;
