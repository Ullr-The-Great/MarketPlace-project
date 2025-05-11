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
      path: '/products/:id',
      name: 'product',
      component: () => import('../views/products/ProductView.vue'),
      props: true
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
    },
    {
      path: '/register',
      name: 'register',
      component: () => import('@/views/auth/RegisterView.vue'),
      meta: { requiresAuth: false }
    },
    {
      path: '/checkout',
      name: 'checkout',
      component: () => import('@/views/CheckoutView.vue'),
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
