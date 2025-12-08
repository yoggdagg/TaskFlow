import auth from "@/api/auth";

// 토큰 갱신 상태 관리
let isRefreshing = false;
let failedQueue = [];

/**
 * 대기 중인 요청들을 처리하는 함수
 * @param {Error|null} error - 에러 객체
 * @param {string|null} token - 새로 발급받은 토큰
 */
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

/**
 * Axios 인스턴스에 인터셉터를 설정하는 함수
 * @param {AxiosInstance} axiosInstance - Axios 인스턴스
 */
export const interceptorSetting = (axiosInstance) => {
  // ==================== 요청 인터셉터 ====================
  axiosInstance.interceptors.request.use(
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

  // ==================== 응답 인터셉터 ====================
  axiosInstance.interceptors.response.use(
    (response) => {
      // 2xx 범위의 상태 코드는 이 함수가 실행됨
      return response;
    },
    async (error) => {
      const originalRequest = error.config;

      // 네트워크 에러 또는 응답이 없는 경우
      if (!error.response) {
        console.error("Network error:", error.message);
        return Promise.reject(error);
      }

      // 401 에러이고, 재시도하지 않은 요청인 경우
      if (error.response.status === 401 && !originalRequest._retry) {
        // 이미 토큰 갱신 중이면 대기열에 추가
        if (isRefreshing) {
          return new Promise((resolve, reject) => {
            failedQueue.push({ resolve, reject });
          })
            .then((token) => {
              originalRequest.headers.Authorization = `Bearer ${token}`;
              return axiosInstance(originalRequest);
            })
            .catch((err) => {
              return Promise.reject(err);
            });
        }

        originalRequest._retry = true;
        isRefreshing = true;

        const refreshToken = localStorage.getItem("refreshToken");

        // Refresh Token이 없으면 로그인 페이지로 이동
        if (!refreshToken) {
          isRefreshing = false;
          localStorage.clear();
          window.location.href = "/login";
          return Promise.reject(error);
        }

        try {
          // auth를 사용하여 토큰 갱신 (무한 루프 방지)
          const response = await auth.post("/auth/refresh", {
            refreshToken: refreshToken,
          });

          const { accessToken, refreshToken: newRefreshToken } = response.data;

          // 새로운 토큰 저장
          localStorage.setItem("accessToken", accessToken);

          // Refresh Token Rotation이 있는 경우
          if (newRefreshToken) {
            localStorage.setItem("refreshToken", newRefreshToken);
          }

          // axios 인스턴스의 기본 헤더 업데이트
          axiosInstance.defaults.headers.common[
            "Authorization"
          ] = `Bearer ${accessToken}`;

          // 실패했던 요청의 헤더 업데이트
          originalRequest.headers.Authorization = `Bearer ${accessToken}`;

          // 대기 중인 다른 요청들 처리
          processQueue(null, accessToken);

          // 원래 요청 재시도
          return axiosInstance(originalRequest);
        } catch (refreshError) {
          // 토큰 갱신 실패 시
          processQueue(refreshError, null);

          // 로컬 스토리지 정리 및 로그인 페이지로 이동
          localStorage.clear();
          window.location.href = "/login";

          return Promise.reject(refreshError);
        } finally {
          isRefreshing = false;
        }
      }

      // 403 에러 처리 (권한 없음)
      if (error.response.status === 403) {
        console.error("Access denied:", error.response.data);
        // 필요시 권한 없음 페이지로 이동
        // window.location.href = "/forbidden";
      }

      // 500번대 서버 에러
      if (error.response.status >= 500) {
        console.error("Server error:", error.response.data);
        // 필요시 에러 페이지로 이동 또는 toast 메시지 표시
      }

      return Promise.reject(error);
    }
  );
};
