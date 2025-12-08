import axios from "axios";
import { interceptorSetting } from "@/api/interceptors";

const api = axios.create({
  baseURL: import.meta.env.VITE_API_URL,
  timeout: 10000,
  headers: {
    "Content-Type": "application/json",
  },
});

// 인터셉터 적용
interceptorSetting(api);

export default api;
