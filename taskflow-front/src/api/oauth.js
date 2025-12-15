// src/api/oauth.js
import instance from "./interceptors";

/**
 * 구글 로그인 - Authorization Code 요청
 */
export const googleLogin = () => {
  const clientId = import.meta.env.VITE_GOOGLE_CLIENT_ID;
  const redirectUri = encodeURIComponent(
    import.meta.env.VITE_GOOGLE_REDIRECT_URI ||
      "http://localhost:5173/oauth/google/callback"
  );
  const scope = encodeURIComponent("email profile");
  const responseType = "code";

  const googleAuthUrl = `https://accounts.google.com/o/oauth2/v2/auth?client_id=${clientId}&redirect_uri=${redirectUri}&response_type=${responseType}&scope=${scope}`;

  window.location.href = googleAuthUrl;
};

/**
 * 구글 로그인 콜백 처리
 * @param {string} code - Authorization Code
 * @returns {Promise} - { accessToken, user } (Refresh Token은 HttpOnly 쿠키로 자동 설정)
 */
export const googleCallback = async (code) => {
  try {
    const response = await instance.post(
      "/oauth/google/callback",
      { code },
      { withCredentials: true } // 쿠키 수신을 위해 필수
    );
    return response.data; // { accessToken, user }
  } catch (error) {
    console.error("Google OAuth callback error:", error);
    throw error;
  }
};

/**
 * 카카오 로그인 - Authorization Code 요청
 */
export const kakaoLogin = () => {
  const clientId = import.meta.env.VITE_KAKAO_CLIENT_ID;
  const redirectUri = encodeURIComponent(
    import.meta.env.VITE_KAKAO_REDIRECT_URI ||
      "http://localhost:5173/oauth/kakao/callback"
  );
  const responseType = "code";

  const kakaoAuthUrl = `https://kauth.kakao.com/oauth/authorize?client_id=${clientId}&redirect_uri=${redirectUri}&response_type=${responseType}`;

  window.location.href = kakaoAuthUrl;
};

/**
 * 카카오 로그인 콜백 처리
 * @param {string} code - Authorization Code
 * @returns {Promise} - { accessToken, user }
 */
export const kakaoCallback = async (code) => {
  try {
    const response = await instance.post(
      "/oauth/kakao/callback",
      { code },
      { withCredentials: true }
    );
    return response.data;
  } catch (error) {
    console.error("Kakao OAuth callback error:", error);
    throw error;
  }
};

/**
 * 네이버 로그인 - Authorization Code 요청
 */
export const naverLogin = () => {
  const clientId = import.meta.env.VITE_NAVER_CLIENT_ID;
  const redirectUri = encodeURIComponent(
    import.meta.env.VITE_NAVER_REDIRECT_URI ||
      "http://localhost:5173/oauth/naver/callback"
  );
  const state = Math.random().toString(36).substring(2, 15); // CSRF 방지용 state
  const responseType = "code";

  // state를 sessionStorage에 저장 (콜백에서 검증용)
  sessionStorage.setItem("naver_oauth_state", state);

  const naverAuthUrl = `https://nid.naver.com/oauth2.0/authorize?client_id=${clientId}&redirect_uri=${redirectUri}&response_type=${responseType}&state=${state}`;

  window.location.href = naverAuthUrl;
};

/**
 * 네이버 로그인 콜백 처리
 * @param {string} code - Authorization Code
 * @param {string} state - CSRF 방지 토큰
 * @returns {Promise} - { accessToken, user }
 */
export const naverCallback = async (code, state) => {
  try {
    // state 검증
    const savedState = sessionStorage.getItem("naver_oauth_state");
    if (state !== savedState) {
      throw new Error("Invalid state parameter - CSRF 공격 가능성");
    }
    sessionStorage.removeItem("naver_oauth_state");

    const response = await instance.post(
      "/oauth/naver/callback",
      { code, state },
      { withCredentials: true }
    );
    return response.data;
  } catch (error) {
    console.error("Naver OAuth callback error:", error);
    throw error;
  }
};

/**
 * 로그아웃 - Refresh Token 쿠키 삭제
 */
export const logout = async () => {
  try {
    await instance.post("/auth/logout", {}, { withCredentials: true });
  } catch (error) {
    console.error("Logout error:", error);
    throw error;
  }
};
