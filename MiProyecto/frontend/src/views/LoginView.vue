<template>
  <div>
    <h1>Iniciar Sesión</h1>
    <form @submit.prevent="login">
      <div>
        <label for="username">Usuario:</label>
        <input type="text" id="username" v-model="credentials.username" required />
      </div>
      <div>
        <label for="password">Contraseña:</label>
        <input type="password" id="password" v-model="credentials.password" required />
      </div>
      <button type="submit">Iniciar Sesión</button>
    </form>
  </div>
</template>

<script>
export default {
  data() {
    return {
      credentials: {
        username: '',
        password: '',
      },
    };
  },
  methods: {
    async login() {
      try {
        // Lógica para enviar las credenciales al backend
        console.log('Credenciales:', this.credentials);
      } catch (error) {
        console.error('Error al iniciar sesión:', error);
      }
    },
  },
};
</script>

<style scoped>
/* Estilos opcionales */
form {
  max-width: 300px;
  margin: 0 auto;
}
</style>