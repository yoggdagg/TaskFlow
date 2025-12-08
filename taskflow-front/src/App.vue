<script setup>
import Nav from '@/components/Navbar.vue'
import Footer from '@/components/Footer.vue'
import Home from '@/views/Home.vue'
</script>

<template>
  <Nav />

  <div>
    <Home />
  </div>
  <Footer.vue />

</template>

<style scoped>
.logo {
  height: 6em;
  padding: 1.5em;
  will-change: filter;
  transition: filter 300ms;
}

.logo:hover {
  filter: drop-shadow(0 0 2em #646cffaa);
}

.logo.vue:hover {
  filter: drop-shadow(0 0 2em #42b883aa);
}
</style>
