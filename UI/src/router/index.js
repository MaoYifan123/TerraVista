import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '../views/Home.vue'

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
]

const router = createRouter({
  history: createWebHistory(process.env.BASE_URL),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  // 设置标题
  document.title = to.meta.title || '默认标题'

  // 检查是否需要登录
  const publicPages = ['/login', '/register']
  const authRequired = !publicPages.includes(to.path)
  const token = localStorage.getItem('token')

  if (authRequired && !token) {
    next('/login')
  } else {
    next()
  }
})

export default router
