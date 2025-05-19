<template>
  <div class="checkout-container">
    <h1>Checkout</h1>

    <!-- Resumen del pedido -->
    <div class="order-summary">
      <h2>Order Summary</h2>
      <ul>
        <li v-for="item in cartStore.cartItems" :key="item.id" class="order-item">
          <span>{{ item.product.name }} (x{{ item.quantity }})</span>
          <span>{{ (item.product.price * item.quantity).toFixed(2) }} €</span>
        </li>
      </ul>
      <p class="total">
        <strong>Total:</strong> {{ cartStore.totalPrice.toFixed(2) }} €
      </p>
    </div>

    <!-- Formulario de pago -->
    <form @submit.prevent="handlePayment">
      <div id="card-element"></div>
      <button type="submit" :disabled="loading">
        {{ loading ? "Processing..." : "Pay Now" }}
      </button>
    </form>

    <p v-if="error" class="error">{{ error }}</p>

    <!-- Modal personalizado -->
    <div v-if="showModal" class="modal-overlay">
      <div class="modal">
        <h2>Payment Successful!</h2>
        <p>Your payment has been processed successfully.</p>
        <button @click="redirectToProducts">Go to Products</button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue';
import { loadStripe } from '@stripe/stripe-js';
import { useCartStore } from '@/stores/cartStore';
import { useRouter } from 'vue-router';
import api from '@/services/api';

const stripePromise = loadStripe('pk_test_51RIEl1Fy8A88fQ62E9WtyKYUxrcftGtZMFbpaEyrkH9eOGfd5Y3D2pJ55qMF55bNiih0r09FpgkTGrlvUIrylgjA00CcKmZLLy'); // Clave pública de Stripe
const cartStore = useCartStore();
const router = useRouter();
const error = ref<string | null>(null);
const loading = ref(false);
const showModal = ref(false);

let cardElement: any = null;

onMounted(async () => {
  try {
    const stripe = await stripePromise;
    if (!stripe) throw new Error("Stripe.js no se cargó correctamente");

    // Configurar Stripe Elements
    const elements = stripe.elements();

    // Crear y montar el elemento de tarjeta
    cardElement = elements.create('card');
    cardElement.mount('#card-element');
  } catch (err: any) {
    console.error("Error al inicializar Stripe Elements:", err.message);
    error.value = "Error al cargar el formulario de pago.";
  }
});

onUnmounted(() => {
  if (cardElement) {
    cardElement.destroy();
  }
});

const handlePayment = async () => {
  loading.value = true;
  error.value = null;

  try {
    const stripe = await stripePromise;
    if (!stripe) throw new Error("Stripe.js no se cargó correctamente");

    // Crear PaymentIntent en el backend
    const { data } = await api.post('/payments/create-payment-intent', {
      amount: Math.round(cartStore.totalPrice * 100), // Convertir a centavos
      items: cartStore.cartItems.map(item => ({
        productId: item.product.id,
        quantity: item.quantity
      }))
    });

    const clientSecret = data.clientSecret;

    // Confirmar el pago
    const { error: stripeError } = await stripe.confirmCardPayment(clientSecret, {
      payment_method: {
        card: cardElement,
      },
    });

    if (stripeError) {
      error.value = stripeError.message ?? "An unknown error occurred";
    } else {
      // Mostrar el modal personalizado
      showModal.value = true;

      // Vaciar el carrito
      await cartStore.clearCart();
    }
  } catch (err: any) {
    console.error("Error procesando el pago:", err.message);
    error.value = err.message || "Error procesando el pago";
  } finally {
    loading.value = false;
  }
};

const redirectToProducts = () => {
  showModal.value = false;
  router.push('/products');
};
</script>

<style scoped>
.checkout-container {
  max-width: 600px;
  margin: 0 auto;
  padding: 20px;
  border: 1px solid #ddd;
  border-radius: 8px;
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
}

.order-summary {
  margin-bottom: 20px;
}

.order-summary ul {
  list-style: none;
  padding: 0;
}

.order-summary .order-item {
  display: flex;
  justify-content: space-between;
  margin-bottom: 10px;
}

.order-summary .total {
  font-size: 1.2rem;
  font-weight: bold;
  margin-top: 10px;
}

button {
  margin-top: 20px;
  padding: 10px;
  background-color: #6772e5;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

button:disabled {
  background-color: #bbb;
  cursor: not-allowed;
}

.error {
  color: red;
  margin-top: 10px;
}

/* Estilos para el modal */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.modal {
  background: white;
  padding: 20px;
  border-radius: 8px;
  text-align: center;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

.modal button {
  margin-top: 20px;
  padding: 10px 20px;
  background-color: #6772e5;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.modal button:hover {
  background-color: #ffec82;
}
</style>