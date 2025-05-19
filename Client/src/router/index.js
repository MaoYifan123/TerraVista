import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '../views/Home.vue'
import { useUserStore } from '@/stores/user'

const routes = [
  {
    path: '/',
    name: 'home',
    component: HomeView,
    meta: {
      title: 'Home',
      // keepAlive: true
    }
  },
  {
    path: '/login',
    name: 'login',
    component: () => import('../views/Login.vue'),
    meta: {
      title: '登录'
    }
  },
  {
    path: '/register',
    name: 'register',
    component: () => import('../views/Register.vue'),
    meta: {
      title: '注册'
    }
  },
  {
    path: '/about',
    name: 'about',
    // route level code-splitting
    // this generates a separate chunk (about.[hash].js) for this route
    // which is lazy-loaded when the route is visited.
    component: () => import(/* webpackChunkName: "about" */ '../views/About.vue'),
    meta: {
      title: '关于'
    }
  },
  {
    path: '/map',
    name: 'map',
    component: () => import('../views/Map.vue'),
    meta: {
      title: '地图',
      // keepAlive: true
    }
  },
  {
    path: '/AIRecommend',
    name: 'AI',
    component: () => import('../views/AIRecommend.vue'),
    meta: {
      title: 'AI推荐',
    }
  },
  {
    path: '/admin/pois',
    name: 'admin-poi-management',
    component: () => import('../views/AdminPoiManagement.vue'),
    meta: {
      title: 'POI管理',
      requiresAdmin: true
    }
  }
]

const router = createRouter({
  history: createWebHistory(process.env.BASE_URL),
  routes
})

// 路由守卫
router.beforeEach(async (to, from, next) => {
  // 设置标题
  document.title = to.meta.title || '默认标题'

  const publicPages = ['/login', '/register']
  const authRequired = !publicPages.includes(to.path)
  const userStore = useUserStore()

  // 如果有token但未加载用户信息，先获取用户信息
  if (userStore.token && !userStore.user) {
    await userStore.fetchUserInfo()
  }

  if (authRequired && !userStore.isLoggedIn) {
    return next('/login')
  }

  if (to.meta.requiresAdmin && !userStore.isAdmin) {
    return next('/')
  }

  next()
})

export default router
