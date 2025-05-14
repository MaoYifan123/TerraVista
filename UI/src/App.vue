<template>
  <nav>
    <router-link to="/">Home</router-link> |
    <router-link to="/about">About</router-link> |
    <router-link to="/map">Map</router-link> |
    <router-link to="/AIRecommend">AI</router-link> |
    <router-link to="/admin/pois" v-if="isAdmin">POI管理</router-link> |
    <router-link to="/login" v-if="!isLoggedIn">Login</router-link>
    <a href="#" @click.prevent="handleLogout" v-else>Logout</a>
  </nav>
  <router-view/>
</template>

<script>
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'

export default {
  name: 'App',
  setup() {
    const router = useRouter()
    const userStore = useUserStore()
    const isLoggedIn = computed(() => userStore.isLoggedIn)
    const isAdmin = computed(() => userStore.isAdmin)

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
      handleLogout
    }
  }
}
</script>

<style>
#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;
}

nav {
  padding: 30px;
}

nav a {
  font-weight: bold;
  color: #2c3e50;
  text-decoration: none;
  margin: 0 10px;
}

nav a.router-link-exact-active {
  color: #42b983;
}

nav a:hover {
  color: #42b983;
}
</style>
