import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/home',
      name: 'home',
      component: () => import('../views/HomeView.vue')
    },
    {
      path: '/demo1',
      name: 'demo1',
      component: () => import('../views/Demo1View.vue')
    },
    {
      path: '/admin/person',
      name: 'person',
      component: () => import('../views/PersonView.vue')
    },
    {
      path: '/login',
      name: 'login',
      component: () => import('../views/LoginView.vue')
    },
    {
      path: '/admin/detail',
      name: 'adminDetail',
      component: () => import('../views/Admin/AdminDetail.vue')
    },
    {
      path: '/admin/save',
      name: 'save',
      component: () => import('../views/Admin/AdminSaveView.vue')
    },
    {
      path: '/admin/saveAdmin',
      name: 'saveAdmin',
      component: () => import('../views/Admin/AdminAdd.vue')
    },
    {
      path: '/user/saveUser',
      name: 'saveUser',
      component: () => import('../views/User/UserAdd.vue')
    },
    {
      path: '/admin/adminManager',
      name: 'adminManager',
      component: () => import('../views/Admin/AdminManagerView.vue')
    },
    {
      path: '/user/userManager',
      name: 'userManager',
      component: () => import('../views/User/UserManagerView.vue')
    },
    {
      path: '/user/detail',
      name: 'userDetail',
      component: () => import('../views/User/UserDetail.vue')
    },
    {
      path: '/scenic/scenicManager',
      name: 'scenicManager',
      component: () => import('../views/Scenic/ScenicManagerView.vue')
    },
    {
      path: '/scenic/detail',
      name: 'scenicDetail',
      component: () => import('../views/Scenic/ScenicDetail.vue')
    },
    {
      path: '/scenic/Add',
      name: 'scenicAdd',
      component: () => import('../views/Scenic/ScenicAdd.vue')
    },
    {
      path: '/guide/guideManager',
      name: 'guideManager',
      component: () => import('../views/Guide/GuideManagerView.vue')
    },
    {
      path: '/comment/commentManager',
      name: 'commentManager',
      component: () => import('../views/Comment/CommentManager.vue')
    },
    {
      path: '/guide/guideDetail',
      name: 'guideDetail',
      component: () => import('../views/Guide/GuideDetail.vue')
    },
    {
      path: '/message/Notification',
      name: 'Notification',
      component: () => import('../views/Message/Notification.vue')
    },
    {
      path: '/message/Send',
      name: 'Send',
      component: () => import('../views/Message/Send.vue')
    },
    {
      path: '/data',
      name: 'Data',
      component: () => import('../views/DataView.vue')
    }
  ]
})

export default router
