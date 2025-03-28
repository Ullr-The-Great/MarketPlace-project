import { createRouter, createWebHistory } from 'vue-router';
import ProductList from '../views/items/products/ProductList.vue';
import LoginView from '../views/LoginView.vue';
import HomeView from '../views/HomeView.vue';

const routes = [
  { path: '/',  component: HomeView},
  { path: '/api/productsList', component: ProductList },
  { path: '/login', component: LoginView },

];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

export default router;
