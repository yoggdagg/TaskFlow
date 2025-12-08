import { createWebHistory, createRouter } from "vue-router";

// lazy 로딩으로 처리
const routes = [
  {
    path: "/",
    name: "Home",
    component: () => import("@/views/Home.vue"),
  },
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

// 네비게이션 가드
router.beforeEach((to, from) => {
  const token = localStorage.getItem("accessToken");

  if (to.meta.requiredAut && !token) {
    return "/login"; // 권한이 없으니 로그인 페이지로
  }
});

export default router;
