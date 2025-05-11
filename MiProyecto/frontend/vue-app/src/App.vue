<script setup lang="ts">
import { RouterLink, RouterView } from 'vue-router'
import AppNavbar from '@/components/AppNavbar.vue';
import {watch} from "vue";



</script>

<template>

    <AppNavbar />
    <main class="main-content">
      <RouterView />
    </main>
</template>

<style>
  @import "styles/main.css";
</style>
