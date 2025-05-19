export interface Review {
    id: number;
    user: { username: string };
    rating: number;
    comment_review: string;

    createdAt: string;
  }