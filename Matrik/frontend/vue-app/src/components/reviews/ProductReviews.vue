<template>
  <div class="reviews-container">
    <h2>Customer Reviews</h2>

    <!-- Mostrar promedio de calificaciones -->
    <div class="reviews-summary">
      <div class="average-rating">
        <span class="stars">
          <span v-for="n in 5" :key="n" class="star" :class="{ filled: n <= averageRating }">★</span>
        </span>
        <span class="average-text">{{ averageRating.toFixed(1) }} out of 5</span>
      </div>
      <p class="total-reviews">{{ reviewStore.reviews.length }} global ratings</p>
    </div>

    <!-- Botón para agregar una reseña -->
    <button v-if="authStore.user && !showForm && !userHasReview && hasPurchased" class="add-review-button"
      @click="showForm = true">
      Write a customer review
    </button>

    <!-- Formulario para agregar o editar una reseña -->
    <div v-if="showForm" class="add-review-form">
      <h3>{{ editingReviewId ? 'Edit your review' : 'Write a review' }}</h3>
      <form @submit.prevent="submitReview">
        <label for="rating">Rating:</label>
        <select v-model="newReview.rating" id="rating" required>
          <option v-for="n in 5" :key="n" :value="n">{{ n }} ★</option>
        </select>

        <label for="comment">Comment:</label>

        <textarea v-model="newReview.comment_review" id="comment" required></textarea>


        <button type="submit" :disabled="reviewStore.loading">
          {{ editingReviewId ? 'Update Review' : 'Submit Review' }}
        </button>
        <button type="button" @click="cancelForm">Cancel</button>
      </form>
    </div>

    <!-- Contenedor con scroll para las reseñas -->
    <div v-if="reviewStore.reviews.length > 0" class="reviews-list-container">
      <div class="reviews-list">
        <div v-for="review in reviewStore.reviews" :key="review.id" class="review-item">
          <div class="review-header">
            <span class="review-user">{{ review.user.username }}</span>
            <span class="review-date">{{ formatDate(review.createdAt) }}</span>
          </div>
          <div class="review-rating">
            <span class="stars">
              <span v-for="n in 5" :key="n" class="star" :class="{ filled: n <= review.rating }">★</span>
            </span>
          </div>

          <p class="review-comment">{{ review.comment_review }}</p>

          <div v-if="authStore.user?.username === review.user.username" class="review-options">
            <button @click="startEditingReview(review)">Edit</button>
            <button @click="reviewStore.deleteReview(review.id)">Delete</button>
          </div>
        </div>
      </div>
    </div>
    <p v-else>No reviews yet. Be the first to review this product!</p>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue';
import { useAuthStore } from '@/stores/authStore';
import { useReviewStore } from '@/stores/reviewStore';
import { Review } from '@/types/reviews';
import api from '@/services/api';

const props = defineProps<{
  productId: number;
}>();

const authStore = useAuthStore();
const reviewStore = useReviewStore();
const hasPurchased = ref(false);

const newReview = ref({
  rating: 5,

  comment_review: '',

});
const showForm = ref(false);
const editingReviewId = ref<number | null>(null);

const fetchReviews = async () => {
  await reviewStore.fetchReviews(props.productId);
};

const checkIfUserPurchased = async () => {
  try {

    if(!authStore.user) return;

    const response = await api.get(`/orders/has-purchased/${props.productId}`);
    hasPurchased.value = response.data.hasPurchased;
    console.log('Has purchased:', hasPurchased.value);
  } catch (error) {
    console.error('Error checking purchase status:', error);
  }
};

const userHasReview = computed(() => {
  if (!authStore.user) return false;
  return reviewStore.reviews.some(
    (review) => review.user.username === authStore.user?.username
  );
});

const submitReview = async () => {
  await reviewStore.submitReview(props.productId, newReview.value, editingReviewId.value);
  resetForm();
};

const startEditingReview = (review: Review) => {
  newReview.value.rating = review.rating;

  newReview.value.comment_review = review.comment_review;

  editingReviewId.value = review.id;
  showForm.value = true;
};

const cancelForm = () => {
  resetForm();
};

const resetForm = () => {
  newReview.value.rating = 5;

  newReview.value.comment_review = '';

  editingReviewId.value = null;
  showForm.value = false;
};

const formatDate = (date: string) => {
  return new Date(date).toLocaleDateString();
};

const averageRating = computed(() => {
  if (reviewStore.reviews.length === 0) return 0;
  const total = reviewStore.reviews.reduce((sum, review) => sum + review.rating, 0);
  return total / reviewStore.reviews.length;
});

onMounted(async () => {
  await fetchReviews();
  await checkIfUserPurchased();
});
</script>

<style scoped>
.reviews-container {
  margin-top: 2rem;
  font-family: Arial, sans-serif;
}

.reviews-summary {
  margin-bottom: 1.5rem;
  border-bottom: 1px solid #ddd;
  padding-bottom: 1rem;
}

.average-rating {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.stars {
  display: inline-flex;
}

.star {
  font-size: 1.2rem;
  color: var(--border-color);
}

.star.filled {
  color: var(--primary-color);
}

.average-text {
  font-weight: bold;
  font-size: 1rem;
}

.total-reviews {
  color: #666;
  font-size: 0.9rem;
  margin-top: 0.5rem;
}

.add-review-button {
  background-color: var(--primary-color);
  color: white;
  border: none;
  padding: 0.5rem 1rem;
  border-radius: 4px;
  cursor: pointer;
  font-size: 1rem;
  margin-bottom: 1.5rem;
  /* Separación con el primer comentario */
}

.add-review-button:hover {
  background-color: var(--primary-color-hover);
}

.add-review-form {
  margin-top: 1.5rem;
  padding: 1rem;
  border: 1px solid #ddd;
  border-radius: 4px;
  background-color: var(--secondary-background-color);
}

.add-review-form label {
  display: block;
  margin-bottom: 0.5rem;
  font-weight: bold;
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
  margin-right: 0.5rem;
  /* Separación entre botones */
}

.add-review-form button:disabled {
  background-color: #ccc;
  cursor: not-allowed;
}

.reviews-list-container {
  max-height: 400px;
  /* Limitar la altura del contenedor */
  overflow-y: auto;
  /* Habilitar scroll vertical */
  border: 1px solid #ddd;
  border-radius: 4px;
  padding: 1rem;
  background-color: var(--secondary-background-color);
}

.reviews-list {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.review-item {
  padding-bottom: 1rem;
  border-bottom: 1px solid #ddd;
}

.review-header {
  display: flex;
  justify-content: space-between;
  font-weight: bold;
  font-size: 0.95rem;
  /* Reducir el tamaño de fuente */
}

.review-rating {
  margin: 0.5rem 0;
}

.review-comment {
  margin-top: 0.5rem;
  line-height: 1.5;
  font-size: 0.95rem;
  /* Reducir el tamaño de fuente */
}

.review-options {
  margin-top: 0.5rem;
  /* Separación entre opciones y comentario */
}

.review-options button {
  background: none;
  border: none;
  color: var(--primary-color);
  cursor: pointer;
  font-size: 0.9rem;
  margin-right: 0.5rem;
  /* Separación entre botones */
}

.review-options button:hover {
  text-decoration: underline;
}
</style>