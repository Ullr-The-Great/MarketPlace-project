<template>
  <div class="login-view">
    <h1>Login</h1>
    <form @submit.prevent="handleLogin">
      <div class="form-group">
        <label>Name</label>
        <input v-model="username" type="username" required>
      </div>
      <div class="form-group">
        <label>Password</label>
        <input v-model="password" type="password" required>
      </div>
      <button type="submit" :disabled="authStore.loading">
        {{ authStore.loading ? 'Logging in...' : 'Login' }}
      </button>
      <p v-if="authStore.error" class="error">{{ authStore.error }}</p>
    </form>
  <div class="register-query">
    <h2>¿No estás registrado en Matrik?</h2>
    <p>Crea una cuenta</p>
    <button class="register-btn">
      <router-link to="/register" class="register-btn-router">
        Crear cuenta
      </router-link>
     
    </button>
  </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { useAuthStore } from '@/stores/authStore';
import { useRouter } from 'vue-router';

const username = ref('');
const password = ref('');
const authStore = useAuthStore();
const router = useRouter();

const handleLogin = async () => {
  try {
    await authStore.login({
      username: username.value,
      password: password.value
    });
  } catch (error) {
    
  }
};
</script>

<style scoped>
.login-view {
  max-width: 400px;
  margin: 0 auto;
  padding: 20px;
  height: auto;
  display: flex;
  flex-direction: column;
  gap: 20px;
  place-self: end;
  font-family: Arial, Helvetica, sans-serif;
  font-size: 14px;
}
.form-group {
  margin-bottom: 15px;
}
label {
  display: block;
  margin-bottom: 5px;
}
input {
  width: 100%;
  padding: 8px;
  box-sizing: border-box;
  border-radius: 3px;
}
button {
  padding: 1.5vh;
  background-color: var(--primary-color);
  color: white;
  border: none;
  cursor: pointer;
  border-radius: 3px;
  width: 100%;
  margin-top: 5vh;
}

button:hover{
  background-color: var(--primary-color-hover);
}

.error {
  color: red;
  margin-top: 10px;
}

.register-query{
  border-top: 1px solid rgba(134, 134, 134, 0.6);
  padding-top: 40px;
  display: flex;
  flex-direction: column;
  gap: 20px;
  
}

.register-query h2{
  font-weight: bold;
}


.register-btn{
  background-color: var( --primary-background-color);
  border: solid 1px rgb(41, 41, 41, 0.2);
}

.register-btn:hover{
  background-color: var(--primary-background-color);
  border: solid 1px rgb(41, 41, 41, 0.4);
}

.register-btn-router{
  color: var(--nav-text);
}

</style>