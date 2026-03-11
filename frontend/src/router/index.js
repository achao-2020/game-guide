import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/login',
      name: 'Login',
      component: () => import('@/views/Login.vue'),
      meta: { requiresAuth: false }
    },
    {
      path: '/register',
      name: 'Register',
      component: () => import('@/views/Register.vue'),
      meta: { requiresAuth: false }
    },
    // 用户端路由
    {
      path: '/',
      component: () => import('@/layout/UserLayout.vue'),
      redirect: '/home',
      meta: { requiresAuth: true },
      children: [
        {
          path: 'home',
          name: 'Home',
          component: () => import('@/views/Home.vue'),
          meta: { title: '首页' }
        },
        {
          path: 'games-list',
          name: 'GamesList',
          component: () => import('@/views/GamesList.vue'),
          meta: { title: '游戏列表' }
        },
        {
          path: 'guides-list',
          name: 'GuidesList',
          component: () => import('@/views/GuidesList.vue'),
          meta: { title: '攻略列表' }
        },
        {
          path: 'game/:id',
          name: 'GameDetail',
          component: () => import('@/views/GameDetail.vue'),
          meta: { title: '游戏详情' }
        },
        {
          path: 'guide/:id',
          name: 'GuideDetail',
          component: () => import('@/views/GuideDetail.vue'),
          meta: { title: '攻略详情' }
        },
        {
          path: 'search',
          name: 'Search',
          component: () => import('@/views/Search.vue'),
          meta: { title: '搜索结果' }
        }
      ]
    },
    // 管理后台路由
    {
      path: '/admin',
      component: () => import('@/layout/MainLayout.vue'),
      redirect: '/admin/dashboard',
      meta: { requiresAuth: true, requiresAdmin: true },
      children: [
        {
          path: 'dashboard',
          name: 'AdminDashboard',
          component: () => import('@/views/Dashboard.vue'),
          meta: { title: '控制台', requiresAdmin: true }
        },
        {
          path: 'games',
          name: 'AdminGames',
          component: () => import('@/views/Games.vue'),
          meta: { title: '游戏管理', requiresAdmin: true }
        },
        {
          path: 'guides',
          name: 'AdminGuides',
          component: () => import('@/views/Guides.vue'),
          meta: { title: '攻略管理', requiresAdmin: true }
        },
        {
          path: 'guides/add',
          name: 'AdminGuideAdd',
          component: () => import('@/views/GuideEdit.vue'),
          meta: { title: '新增攻略', requiresAdmin: true }
        },
        {
          path: 'guides/edit/:id',
          name: 'AdminGuideEdit',
          component: () => import('@/views/GuideEdit.vue'),
          meta: { title: '编辑攻略', requiresAdmin: true }
        },
        {
          path: 'categories',
          name: 'AdminCategories',
          component: () => import('@/views/Categories.vue'),
          meta: { title: '分类管理', requiresAdmin: true }
        },
        {
          path: 'tags',
          name: 'AdminTags',
          component: () => import('@/views/Tags.vue'),
          meta: { title: '标签管理', requiresAdmin: true }
        }
      ]
    }
  ]
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  const role = localStorage.getItem('role')
  
  // 需要登录的页面
  if (to.meta.requiresAuth && !token) {
    next('/login')
    return
  }
  
  // 已登录用户访问登录/注册页
  if ((to.path === '/login' || to.path === '/register') && token) {
    // 根据角色跳转到不同页面
    if (role === 'ADMIN') {
      next('/admin/dashboard')
    } else {
      next('/home')
    }
    return
  }
  
  // 需要管理员权限的页面
  if (to.meta.requiresAdmin && role !== 'ADMIN') {
    next('/home')
    return
  }
  
  next()
})

export default router



