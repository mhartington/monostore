export type Product = {
  id: string;
  name: string;
  description: string;
  price: number;
  image: string;
  category: string;
  stock: number;
  created_at: string;
  updated_at?: string;
};
export const products: Array<Product> = [
  {
    id: '1',
    name: 'Premium Wireless Headphones',
    description: 'High-quality wireless headphones with noise cancellation',
    price: 299.99,
    image: 'https://images.pexels.com/photos/3394651/pexels-photo-3394651.jpeg',
    category: 'electronics',
    stock: 50,
    created_at: new Date().toISOString(),
  },
  {
    id: '2',
    name: 'Smartphone 13 Pro',
    description: 'Latest smartphone with advanced camera features',
    price: 999.99,
    image: 'https://images.pexels.com/photos/404280/pexels-photo-404280.jpeg',
    category: 'electronics',
    stock: 20,
    created_at: new Date().toISOString(),
  },
  {
    id: '3',
    name: 'Designer Watch',
    description: 'Elegant designer watch with leather strap',
    price: 299.99,
    image: 'https://images.pexels.com/photos/277390/pexels-photo-277390.jpeg',
    category: 'accessories',
    stock: 15,
    created_at: new Date().toISOString(),
  },
  {
    id: '4',
    name: 'Premium Cotton T-Shirt',
    description: 'Soft and comfortable cotton t-shirt',
    price: 29.99,
    image: 'https://images.pexels.com/photos/5698851/pexels-photo-5698851.jpeg',
    category: 'clothing',
    stock: 100,
    created_at: new Date().toISOString(),
  },
  {
    id: '5',
    name: 'Wireless Gaming Mouse',
    description: 'High-performance wireless gaming mouse',
    price: 79.99,
    image: 'https://images.pexels.com/photos/5082581/pexels-photo-5082581.jpeg',
    category: 'electronics',
    stock: 30,
    created_at: new Date().toISOString(),
  },
];

export type User = {
  id: string;
  username: string;
  email: string;
  password: string;
  role: string;
  created_at: string;
};
export const users: Array<User> = [
  {
    id: '1',
    username: 'johndoe',
    email: 'john@example.com',
    password: 'password123', // In reality, this would be hashed
    role: 'customer',
    created_at: new Date().toISOString(),
  },
];

export interface CartItem {
  product: {
    id: string;
    name: string;
    price: number;
    image: string;
  };
  quantity: number;
  subtotal: number;
}
export interface Cart {
  items: CartItem[];
  total: number;
}
export const carts: Record<string | number, Cart> = {};

export type Order = {
  id: string;
  user_id: string;
  items: Array<CartItem>;
  total: number;
  shippingAddress: string;
  paymentMethod: string;
  status: string;
  created_at: string;
  cancelled_at?: string;
}
export const orders: Array<Order> = [];

export function generateId(): string {
  return (
    Math.random().toString(36).substring(2, 15) +
    Math.random().toString(36).substring(2, 15)
  );
}
