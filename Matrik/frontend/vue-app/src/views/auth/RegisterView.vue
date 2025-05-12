<template>
    <div class="register-container">
      <div class="register-card">
        <h2>Create your account</h2>
        
        <form @submit.prevent="handleSubmit" class="register-form">
          <div class="form-group">
            <label for="username">Username</label>
            <input
              v-model="form.username"
              type="text"
              id="username"
              required
              autocomplete="username"
            >
            <div v-if="errors.username" class="error-message">{{ errors.username }}</div>
          </div>
  
          <div class="form-group">
            <label for="email">Email</label>
            <input
              v-model="form.email"
              type="email"
              id="email"
              required
              autocomplete="email"
            >
            <div v-if="errors.email" class="error-message">{{ errors.email }}</div>
          </div>
  
          <div class="form-group">
            <label for="password">Password</label>
            <input
              v-model="form.password"
              type="password"
              id="password"
              required
              autocomplete="new-password"
            >
          </div>
  
          <div class="form-group">
            <label for="confirmPassword">Confirm Password</label>
            <input
              v-model="form.confirmPassword"
              type="password"
              id="confirmPassword"
              required
              autocomplete="new-password"
            >
            <div v-if="errors.password" class="error-message">{{ errors.password }}</div>
          </div>
  
          <button type="submit" :disabled="loading" class="register-button">
            <span v-if="!loading">Register</span>
            <span v-else>Creating account...</span>
          </button>
  
          <div class="login-link">
            Already have an account? <router-link to="/login">Login here</router-link>
          </div>
        </form>
      </div>
    </div>
  </template>
  
  <script setup lang="ts">
  import { ref } from 'vue';
  import { useRouter } from 'vue-router';
  import axios from 'axios';
  import type { AxiosError } from 'axios';
  import api from "@/services/api"
  
  interface RegisterForm {
    username: string;
    email: string;
    password: string;
    confirmPassword: string;
  }
  
  const form = ref<RegisterForm>({
    username: '',
    email: '',
    password: '',
    confirmPassword: ''
  });
  
  const errors = ref({
    username: '',
    email: '',
    password: ''
  });
  
  const loading = ref(false);
  const router = useRouter();
  
  const validateForm = () => {
    let isValid = true;
    errors.value = { username: '', email: '', password: '' };
  
    // Username validation
    if (!form.value.username.trim()) {
      errors.value.username = 'Username is required';
      isValid = false;
    }
  
    // Email validation
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!form.value.email) {
      errors.value.email = 'Email is required';
      isValid = false;
    } else if (!emailRegex.test(form.value.email)) {
      errors.value.email = 'Invalid email format';
      isValid = false;
    }
  
    // Password validation
    if (form.value.password.length < 6) {
      errors.value.password = 'Password must be at least 6 characters';
      isValid = false;
    } else if (form.value.password !== form.value.confirmPassword) {
      errors.value.password = 'Passwords do not match';
      isValid = false;
    }
  
    return isValid;
  };
  
  const handleSubmit = async () => {
    if (!validateForm()) return;
  
    loading.value = true;
    console.log("antes de el post");
    try {
      const response = await api.post('/users/register', {
        username: form.value.username,
        email: form.value.email,
        password: form.value.password
      });
  
      if (response.status === 201) {
        router.push('/login');
      }
    } catch (error) {
        console.log("En el error", error)
      const axiosError = error as AxiosError;
      if (axiosError.response) {
        const status = axiosError.response.status;
        const data = axiosError.response.data as { message?: string };
  
        if (status === 409) {
          errors.value.username = 'Username or email already exists';
        } else {
          errors.value.password = data.message || 'Registration failed';
        }
      } else {
        errors.value.password = 'Network error, please try again';
      }
    } finally {
      loading.value = false;
    }
  };
  </script>
  
  <style scoped>
    @import "../../styles/register.css";
  </style>