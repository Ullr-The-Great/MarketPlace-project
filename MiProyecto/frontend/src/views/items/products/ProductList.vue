<template>
    <div>
    <h1>Productos</h1>
    <table>
      <thead>
        <tr>
          <th>Nombre</th>
          <th>Precio</th>
          <th>Descripción</th>
          <th>Stock</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="(product) in products" :key="product.id">
          <td>{{ product.name }}</td>
          <td>{{ product.price }}</td>
          <td>{{ product.description }}</td>
          <td>{{ product.stock }}</td>
        </tr>
      </tbody>
    </table>
  </div>
</template>

<script type="ts">
import api from '@/services/api';

export default {
  data() {
    return {
      products: [],
    };
  },
  async created() {
    const response = await api.getProducts();
    this.products = response.data;
  },
  onMounted:function () {
    const response = api.getProducts();
    this.products = response.data;
  },
};


</script>

<style scoped>

</style>