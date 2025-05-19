<template>
  <div class="app-container">
    <BackgroundCarousel />
    <nav class="main-nav">
      <div class="nav-content">
        <div class="logo-section">
          <h2 class="nav-logo">TerraVista</h2>
        </div>
        <div class="nav-links">
          <router-link to="/">主页</router-link>
          <router-link to="/admin/pois" v-if="isAdmin">POI管理</router-link>
          <router-link to="/login" v-if="!isLoggedIn">Login</router-link>
          <a href="#" @click.prevent="handleLogout" v-else>Logout</a>
        </div>
      </div>
    </nav>
    <main class="main-content" :class="{ 'map-page': isMapRoute }">
      <router-view/>
    </main>
    <footer class="team-footer" v-if="!isMapRoute">
      <div class="footer-content">
        <p>团队：TerraVista</p>
        <p>联系我们：guohanyao@whu.edu.cn</p>
      </div>
    </footer>
  </div>
</template>

<script>
import { ref, onMounted, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '@/stores/user'
import BackgroundCarousel from '@/components/BackgroundCarousel.vue'

export default {
  name: 'App',
  components: {
    BackgroundCarousel
  },
  setup() {
    const router = useRouter()
    const route = useRoute()
    const userStore = useUserStore()
    const isLoggedIn = computed(() => userStore.isLoggedIn)
    const isAdmin = computed(() => userStore.isAdmin)
    const isMapRoute = computed(() => route.path === '/map')

    const handleLogout = () => {
      userStore.logout()
      router.push('/login')
    }

    onMounted(() => {
      if (userStore.token && !userStore.user) {
        userStore.fetchUserInfo()
      }
    })

    return {
      isLoggedIn,
      isAdmin,
      isMapRoute,
      handleLogout
    }
  }
}
</script>

<style>
body {
  margin: 0;
  padding: 0;
}

#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  color: #2c3e50;
  min-height: 100vh;
}

.app-container {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
  position: relative;
  z-index: 1;
}

.main-nav {
  background-color: rgba(255, 255, 255, 0.9);
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  position: sticky;
  top: 0;
  z-index: 1000;
  backdrop-filter: blur(8px);
}

.nav-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  max-width: 1200px;
  margin: 0 auto;
  padding: 15px 20px;
}

.logo-section {
  display: flex;
  align-items: center;
}

.nav-logo {
  margin: 0;
  font-size: 1.8rem;
  color: #42b983;
  font-weight: 700;
}

.nav-links {
  display: flex;
  gap: 20px;
  align-items: center;
}

.nav-links a {
  font-weight: 600;
  color: #2c3e50;
  text-decoration: none;
  padding: 8px 12px;
  border-radius: 6px;
  transition: all 0.3s ease;
}

.nav-links a:hover {
  background-color: rgba(66, 185, 131, 0.1);
  color: #42b983;
}

.nav-links a.router-link-exact-active {
  color: #ffffff;
  background-color: #42b983;
}

.main-content {
  flex: 1;
  padding: 20px;
  max-width: 1200px;
  width: 100%;
  margin: 0 auto;
  box-sizing: border-box;
  position: relative;
  z-index: 2;
}

.main-content.map-page {
  padding: 0;
  max-width: none;
  width: 100vw;
  height: calc(100vh - 66px);
  margin: 0;
  position: relative;
  top: 0;
  z-index: 1;
  display: flex;
  flex-direction: column;
}

.team-footer {
  margin-top: auto;
  padding: 20px;
  background-color: rgba(255, 255, 255, 0.9);
  border-top: 1px solid #eaeaea;
  color: #6c757d;
  text-align: center;
  box-shadow: 0 -2px 10px rgba(0, 0, 0, 0.05);
  position: relative;
  z-index: 2;
  backdrop-filter: blur(8px);
}

.footer-content {
  max-width: 1200px;
  margin: 0 auto;
}

.footer-content p {
  margin: 5px 0;
}
</style>
