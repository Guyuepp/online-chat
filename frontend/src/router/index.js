import { createRouter, createWebHashHistory } from 'vue-router'
import AuthView    from '../views/AuthView.vue'
import ProfileView from '../views/ProfileView.vue'

const routes = [
  { path: '/',        redirect: '/auth' },
  { path: '/auth',    component: AuthView,    meta: { guest: true } },
  { path: '/profile', component: ProfileView, meta: { requiresAuth: true } }
]

const router = createRouter({
  history: createWebHashHistory(),
  routes
})

router.beforeEach((to) => {
  const token = localStorage.getItem('token')
  if (to.meta.requiresAuth && !token) return '/auth'
  if (to.meta.guest && token)         return '/profile'
})

export default router
