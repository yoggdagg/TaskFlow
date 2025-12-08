import axios from "axios";

const publicApi = axios.create({
  baseURL: import.meta.env.VITE_API_URL,
  timeout: 100000,
  headers: {
    "Content-Type": "application/json",
  },
});

export default publicApi;
