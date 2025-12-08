import { defineConfig } from "vite";
import vue from "@vitejs/plugin-vue";
import { VitePWA } from "vite-plugin-pwa";
import path from "path";
import { fileURLToPath } from "url";

// ES 모듈에서 __dirname 대체
const __dirname = path.dirname(fileURLToPath(import.meta.url));

export default defineConfig({
  plugins: [
    vue(),
    VitePWA({
      registerType: "autoUpdate",
      devOptions: {
        enabled: true,
      },
      manifest: {
        name: "TaskFlow",
        short_name: "TaskFlow",
        description: "일정관리 ToDo 애플리케이션",
        theme_color: "#ffffff",
        icons: [
          {
            src: "icon-192x192.png",
            sizes: "192x192",
            type: "image/png",
          },
        ],
      },
    }),
  ],
  // resolve는 plugins 밖, 최상위 속성
  resolve: {
    alias: {
      "@": path.resolve(__dirname, "./src"),
    },
  },
});
