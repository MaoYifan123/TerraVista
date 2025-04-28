<template>
  <nav>
    <router-link to="/">Home</router-link> |
    <router-link to="/about">About</router-link> |
    <router-link to="/map">Map</router-link> |
    <router-link to="/login" v-if="!isLoggedIn">Login</router-link>
    <a href="#" @click.prevent="handleLogout" v-else>Logout</a>
  </nav>
  <router-view/>
</template>

<script>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'

export default {
  name: 'App',
  setup() {
    const router = useRouter()
    const isLoggedIn = ref(false)

    const checkLoginStatus = () => {
      isLoggedIn.value = !!localStorage.getItem('token')
    }

    const handleLogout = () => {
      localStorage.removeItem('token')
      isLoggedIn.value = false
      router.push('/login')
    }

    onMounted(() => {
      checkLoginStatus()
    })

    return {
      isLoggedIn,
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
