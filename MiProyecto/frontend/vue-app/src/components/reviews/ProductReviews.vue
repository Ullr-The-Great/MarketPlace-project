<template>
  <div class="reviews-container">
    <h2>Reviews</h2>

    <button v-if="authStore.user && !showForm" @click="showForm = true">Crear Reseña</button>


     <!-- Formulario para agregar o editar una reseña -->
     <div v-if="showForm" class="add-review-form">
      <h3>{{ editingReviewId ? 'Editar Reseña' : 'Crear Reseña' }}</h3>
      <form @submit.prevent="submitReview">
        <label for="rating">Rating:</label>
        <select v-model="newReview.rating" id="rating" required>
          <option v-for="n in 5" :key="n" :value="n">{{ n }}</option>
        </select>

        <label for="comment">Comment:</label>
        <textarea v-model="newReview.commentario" id="comment" required></textarea>

        <button type="submit" :disabled="loading">
          {{ editingReviewId ? 'Actualizar Reseña' : 'Enviar Reseña' }}
        </button>
        <button type="button" @click="cancelForm">Cancelar</button>
      </form>
    </div>

    <!-- Mostrar reseñas existentes -->
    <div v-if="reviews.length > 0" class="reviews-list">
      <div v-for="review in reviews" :key="review.id" class="review-item">
        <div class="review-header">
          <span class="review-user">{{ review.user.username }}</span>
          <span class="review-rating">Rating: {{ review.rating }}/5</span>
          <!-- Opciones para la reseña del usuario actual -->
          <div v-if="authStore.user?.username === review.user.username" class="review-options">
            <button @click="toggleMenu(review.id)">⋮</button>
            <div v-if="review.id === editingReviewId" class="review-menu">
              <button @click="startEditingReview(review)">Editar Reseña</button>
              <button @click="deleteReview(review.id)">Eliminar Reseña</button>
            </div>
          </div>
        </div>
        <p class="review-comment">{{ review.commentario }}</p>
      </div>
    </div>
    <p v-else>No reviews yet. Be the first to review this product!</p>

    <!-- Botón para crear una nueva reseña -->
    

   
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { useAuthStore } from '@/stores/authStore';
import api from '@/services/api';

const props = defineProps<{
  productId: number;
}>();

const authStore = useAuthStore();
const reviews = ref<Review[]>([]);
const userReview = ref<Review | null>(null);
const newReview = ref({
  rating: 5,
  commentario: ''
});
const loading = ref(false);
const showForm = ref(false);
const editingReviewId = ref<number | null>(null);

const fetchReviews = async () => {
  if (!props.productId || props.productId === 0) {
    console.warn('El productId no está definido o es inválido.');
    return;
  }

  try {
    const response = await api.get(`/reviews/product/${props.productId}`);
    reviews.value = response.data;

    const currentUser = authStore.user;
    if (currentUser) {
      userReview.value = reviews.value.find(
        (review) => review.user.username === currentUser.username
      ) || null;
    }
  } catch (error) {
    console.error('Error fetching reviews:', error);
  }
};

const submitReview = async () => {
  loading.value = true;
  try {
    const response = await api.post(`/reviews`, null, {
      params: {
        productId: props.productId,
        rating: newReview.value.rating,
        commentario: newReview.value.commentario
      }
    });

    if (editingReviewId.value) {
      const index = reviews.value.findIndex((r) => r.id === editingReviewId.value);
      if (index !== -1) reviews.value[index] = response.data;
    } else {
      reviews.value.push(response.data);
    }

    resetForm();
  } catch (error) {
    console.error('Error submitting review:', error);
  } finally {
    loading.value = false;
  }
};

const deleteReview = async (reviewId: number) => {
  try {
    await api.delete(`/reviews/${reviewId}`);
    
    reviews.value = reviews.value.filter((review) => review.id !== reviewId);

    if (userReview.value?.id === reviewId) {
      userReview.value = null;
    }

    console.log(`Reseña con ID ${reviewId} eliminada correctamente.`);
  } catch (error) {
    console.error('Error deleting review:', error);
  }
};
const toggleMenu = (reviewId: number) => {
  editingReviewId.value = editingReviewId.value === reviewId ? null : reviewId;
};

const startEditingReview = (review: Review) => {
  newReview.value.rating = review.rating;
  newReview.value.commentario = review.commentario;
  editingReviewId.value = review.id;
  showForm.value = true;
};

const cancelForm = () => {
  resetForm();
};

const resetForm = () => {
  newReview.value.rating = 5;
  newReview.value.commentario = '';
  editingReviewId.value = null;
  showForm.value = false;
};

onMounted(fetchReviews);
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