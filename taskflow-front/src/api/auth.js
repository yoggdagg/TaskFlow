// /src/api;/auth.js
// 인증 전용이기 때문에 인터셉터 없음
import axios from "axios";

const auth = axios.create({
  baseURL: import.meta.env.VITE_API_URL,
  timeout: 100000,
  headers: {
    "Content-Type": "application/json",
  },
});

export default auth;
