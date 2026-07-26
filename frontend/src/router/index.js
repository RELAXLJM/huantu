import { createRouter, createWebHashHistory } from 'vue-router'

const routes = [
  {
    path: '/',
    component: () => import('../layouts/AdminLayout.vue'),
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('../views/Dashboard.vue'),
        meta: { title: '数据概览' }
      },
      {
        path: 'scenics',
        name: 'Scenics',
        component: () => import('../views/ScenicList.vue'),
        meta: { title: '景点管理' }
      },
      {
        path: 'posts',
        name: 'Posts',
        component: () => import('../views/PostAudit.vue'),
        meta: { title: '帖子管理' }
      },
      {
        path: 'rankings',
        name: 'Rankings',
        component: () => import('../views/RankingConfig.vue'),
        meta: { title: '榜单配置' }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHashHistory(),
  routes
})

export default router
