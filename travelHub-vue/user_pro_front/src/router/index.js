import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      redirect: '/home',
    },
    {
      path: '/home',
      name: 'home',
      component: () => import('../views/HomeView.vue')
    },
    {
      path:'/demo1',
      name: 'demo1',
      component: () => import('../views/guide/GuideView.vue')
    },
    {
      path: '/login',
      name: 'login',
      component: () => import('../views/LoginView.vue')
    },
    {
      path: '/register',
      name: 'register',
      component: () => import('../views/RegisterView.vue')
    },
    {
      path: '/guide',
      name: 'guide-view',
      component: () => import('../views/guide/GuideView.vue')
    },
    {
      path: '/guide/detail',
      name: 'guideDetail',
      component: () => import('../views/guide/GuideDetail.vue')
    },
    {
      path: '/scenic',
      name: 'scenicView',
      component: () => import('../views/scenic/ScenicView.vue')
    },
    {
      path: '/scenic/select',
      name: 'scenicVSelectView',
      component: () => import('../views/scenic/ScenicSelect.vue')
    },
    {
      path: '/scenic/detail',
      name: 'scenicDetail',
      component: () => import('../views/scenic/ScenicDetail.vue')
    },
    {
      path: '/guide/edit',
      name: 'guide-edit',
      component: () => import('../views/guide/GuideEdit.vue')
    },
    {
      path: '/guide/guideManager',
      name: 'guide-guideManager',
      component: () => import('../views/guide/GuideManaegr.vue')
    },
    {
      path: '/person',
      name: 'person',
      component: () => import('../views/UserDetail.vue'),
      meta: { backgroundImage: './assets/images/person1.jpg' }
    },
    {
      path: '/message',
      name: 'message',
      component: () => import('../views/MessageView.vue')
    }, 
    {
      path: '/plan',
      name: 'plan',
      component: () => import('../views/plan/PlanEditView.vue')
    },
    {
      path: '/aiChat',
      name: 'aiChat',
      component: () => import('../views/aiChat/aiChat.vue')
    },
  ]
})

export default router
