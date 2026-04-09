import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/Login.vue')
  },
  {
    path: '/',
    name: 'Layout',
    component: () => import('../views/Layout.vue'),
    children: [
      {
        path: '',
        redirect: '/dashboard'
      },
      {
        path: '/dashboard',
        name: 'Dashboard',
        component: () => import('../views/Dashboard.vue'),
        meta: { title: '首页' }
      },
      {
        path: '/system/user',
        name: 'SystemUser',
        component: () => import('../views/system/User.vue'),
        meta: { title: '用户管理' }
      },
      {
        path: '/system/role',
        name: 'SystemRole',
        component: () => import('../views/system/Role.vue'),
        meta: { title: '角色管理' }
      },
      {
        path: '/system/menu',
        name: 'SystemMenu',
        component: () => import('../views/system/Menu.vue'),
        meta: { title: '菜单管理' }
      },
      {
        path: '/community/community',
        name: 'Community',
        component: () => import('../views/community/Community.vue'),
        meta: { title: '小区管理' }
      },
      {
        path: '/community/building',
        name: 'Building',
        component: () => import('../views/community/Building.vue'),
        meta: { title: '楼栋管理' }
      },
      {
        path: '/community/unit',
        name: 'Unit',
        component: () => import('../views/community/Unit.vue'),
        meta: { title: '单元管理' }
      },
      {
        path: '/community/room',
        name: 'Room',
        component: () => import('../views/community/Room.vue'),
        meta: { title: '房屋管理' }
      },
      {
        path: '/owner/list',
        name: 'Owner',
        component: () => import('../views/owner/Owner.vue'),
        meta: { title: '业主档案' }
      },
      {
        path: '/owner/binding',
        name: 'OwnerBinding',
        component: () => import('../views/owner/Binding.vue'),
        meta: { title: '人房绑定' }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
