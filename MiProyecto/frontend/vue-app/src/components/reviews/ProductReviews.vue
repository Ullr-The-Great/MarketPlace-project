<template>
    <div class="reviews-container">
      <h2>Reviews</h2>
  
      <!-- Mostrar reseñas existentes -->
      <div v-if="reviews.length > 0" class="reviews-list">
        <div v-for="review in reviews" :key="review.id" class="review-item">
          <div class="review-header">
            <span class="review-user">{{ review.user.username }}</span>
            <span class="review-rating">Rating: {{ review.rating }}/5</span>
          </div>
          <p class="review-comment">{{ review.commentario }}</p>
        </div>
      </div>
      <p v-else>No reviews yet. Be the first to review this product!</p>
  
      <!-- Formulario para agregar una reseña -->
      <div v-if="authStore.user" class="add-review-form">
        <h3>Add a Review</h3>
        <form @submit.prevent="submitReview">
          <label for="rating">Rating:</label>
          <select v-model="newReview.rating" id="rating" required>
            <option v-for="n in 5" :key="n" :value="n">{{ n }}</option>
          </select>
  
          <label for="comment">Comment:</label>
          <textarea v-model="newReview.commentario" id="comment" required></textarea>
  
          <button type="submit" :disabled="loading">Submit Review</button>
        </form>
      </div>
      <p v-else>Please log in to leave a review.</p>
    </div>
  </template>
  
  <script setup lang="ts">
import { ref } from 'vue';
import { useAuthStore } from '../../stores/authStore'
import api from '@/services/api';

interface Review {
  id: number;
  user: {
    username: string;
  };
  rating: number;
  commentario: string;
}
const authStore = useAuthStore(); // Initialize the auth store
const reviews = ref<Review[]>([]);
const newReview = ref({
  rating: 5,
  commentario: ''
});
const loading = ref(false);

const submitReview = async () => {
  loading.value = true;
  try {
    const response = await api.post('/reviews', {
      productId: 1, // Asegúrate de pasar el ID correcto del producto
      rating: newReview.value.rating,
      commentario: newReview.value.commentario
    });
    reviews.value.push(response.data); // Actualiza la lista de reseñas
    newReview.value.commentario = '';
    newReview.value.rating = 5;
  } catch (error) {
    console.error('Error submitting review:', error);
  } finally {
    loading.value = false;
  }
};
</script>
  
  <style scoped>
  .reviews-container {
    margin-top: 2rem;
  }
  
  .reviews-list {
    margin-bottom: 2rem;
  }
  
  .review-item {
    border-bottom: 1px solid #ddd;
    padding: 1rem 0;
  }
  
  .review-header {
    display: flex;
    justify-content: space-between;
    font-weight: bold;
  }
  
  .review-comment {
    margin-top: 0.5rem;
  }
  
  .add-review-form {
    margin-top: 2rem;
  }
  
  .add-review-form label {
    display: block;
    margin-bottom: 0.5rem;
  }
  
  .add-review-form textarea {
    width: 100%;
    height: 100px;
    margin-bottom: 1rem;
  }
  
  .add-review-form button {
    padding: 0.5rem 1rem;
    background-color: var(--primary-color);
    color: white;
    border: none;
    border-radius: 4px;
    cursor: pointer;
  }
  
  .add-review-form button:disabled {
    background-color: #ccc;
    cursor: not-allowed;
  }
  </style>