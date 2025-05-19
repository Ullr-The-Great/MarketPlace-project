import { defineStore } from 'pinia';
import { ref } from 'vue';
import api from '@/services/api';
import type { Review } from '@/types/reviews';

export const useReviewStore = defineStore('review', () => {
  const reviews = ref<Review[]>([]);
  const userReview = ref<Review | null>(null);
  const loading = ref(false);
  const error = ref<string | null>(null);

  const fetchReviews = async (productId: number) => {
    if (!productId || productId === 0) {
      console.warn('El productId no está definido o es inválido.');
      return;
    }

    loading.value = true;
    try {
      const response = await api.get(`/reviews/product/${productId}`);
      reviews.value = response.data;
    } catch (err) {
      console.error('Error fetching reviews:', err);
      error.value = 'Error fetching reviews';
    } finally {
      loading.value = false;
    }
  };

  const submitReview = async (productId: number, newReview: { rating: number; comment_review: string }, editingReviewId: number | null) => {
    loading.value = true;
    try {
      const response = await api.post(`/reviews`, null, {
        params: {
          productId,
          rating: newReview.rating,
          comment_review: newReview.comment_review,
        },
      });

      if (editingReviewId) {
        const index = reviews.value.findIndex((r) => r.id === editingReviewId);
        if (index !== -1) reviews.value[index] = response.data;
      } else {
        reviews.value.push(response.data);
      }
    } catch (err) {
      console.error('Error submitting review:', err);
      error.value = 'Error submitting review';
    } finally {
      loading.value = false;
    }
  };

  const deleteReview = async (reviewId: number) => {
    loading.value = true;
    try {
      await api.delete(`/reviews/${reviewId}`);
      reviews.value = reviews.value.filter((review) => review.id !== reviewId);
      if (userReview.value?.id === reviewId) {
        userReview.value = null;
      }
    } catch (err) {
      console.error('Error deleting review:', err);
      error.value = 'Error deleting review';
    } finally {
      loading.value = false;
    }
  };

  return {
    reviews,
    userReview,
    loading,
    error,
    fetchReviews,
    submitReview,
    deleteReview,
  };
});