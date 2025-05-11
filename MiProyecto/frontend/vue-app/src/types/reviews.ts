export interface Review {
    id: number;
    user: { username: string };
    rating: number;
    commentario: string;
    createdAt: string;
  }