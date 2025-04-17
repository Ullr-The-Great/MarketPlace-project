<script setup lang="ts">
import { RouterLink, RouterView } from 'vue-router'
import AppNavbar from '@/components/AppNavbar.vue';
import {watch} from "vue";



</script>

<template>
  <div id="app">
    <AppNavbar />
    <main class="main-content">
      <RouterView />
    </main>
  </div>
</template>

<style>
:root {
  --navbar-height: 60px;
  --primary-color: #131921;
  --secondary-color: #febd69;
  --text-color-light: #ffffff;
  --text-color-dark: #131921;
}

body {
  margin: 0;
  padding: 0;

  font-family: 'Open Sans', Arial, sans-serif;
}

#app {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
  width: 10000000px
}

.main-content {
  flex: 1;
  padding-top: var(--navbar-height);
}

</style>
