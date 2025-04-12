import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '../views/HomeView.vue'
import ProductListView from '../views/products/ProductListView.vue' 
import CartView from '../views/cart/CartView.vue'
import LoginView from '../views/auth/LoginView.vue'
import { useAuthStore } from '../stores/authStore'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: HomeView,
    },
    {
      path: '/about',
      name: 'about',
      component: () => import('../views/AboutView.vue'),
    },
    {
      path: '/products',
      name:'products',
      component: ProductListView,
    },
    {
      path: '/cart',
      name: 'cart',
      component: CartView,
    },
    {
      path: '/login',
      name: 'login',
      component: LoginView,
    }
  ],
});

router.beforeEach(async (to, from, next) => {
  const authStore = useAuthStore();

  await authStore.checkAuth();

  if (to.meta.requiresAuth && !authStore.isAuthenticated){
    next({name: 'login', query: { redirect: to.fullPath}});
    return;
  }

  if(to.meta.requiresAuth && authStore.isAuthenticated){
    next( { name: 'home'});
    return;
  }
  next();
});

export default router
