const API_URL = 'http://localhost:3000/api';

export async function getProducts(category?: string) {
  const url = new URL(`${API_URL}/products`);
  if (category) {
    url.searchParams.set('category', category);
  }
  const response = await fetch(url);
  if (!response.ok) throw new Error('Failed to fetch products');
  return response.json();
}

export async function getProduct(id: string) {
  const response = await fetch(`${API_URL}/products/${id}`);
  if (!response.ok) throw new Error('Failed to fetch product');
  return response.json();
}

export async function getCart() {
  const token = localStorage.getItem('token')
  const response = await fetch(`${API_URL}/cart`, {
    headers: {
      'Authorization': `Bearer ${token}`
    }
  });
  if (!response.ok) throw new Error('Failed to fetch cart');
  return response.json();
}

export async function addToCart(productId: string, quantity: number) {
  const response = await fetch(`${API_URL}/cart/items`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${localStorage.getItem('token')}`
    },
    body: JSON.stringify({ productId, quantity })
  });
  if (!response.ok) throw new Error('Failed to add to cart');
  return response.json();
}

export async function updateCartItem(productId: string, quantity: number) {
  const response = await fetch(`${API_URL}/cart/items/${productId}`, {
    method: 'PUT',
    headers: {
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${localStorage.getItem('token')}`
    },
    body: JSON.stringify({ quantity })
  });
  if (!response.ok) throw new Error('Failed to update cart');
  return response.json();
}

export async function removeFromCart(productId: string) {
  const response = await fetch(`${API_URL}/cart/items/${productId}`, {
    method: 'DELETE',
    headers: {
      'Authorization': `Bearer ${localStorage.getItem('token')}`
    }
  });
  if (!response.ok) throw new Error('Failed to remove from cart');
  return response.json();
}

export async function login(email: string, password: string) {
  const response = await fetch(`${API_URL}/users/login`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ email, password })
  });
  if (!response.ok) throw new Error('Failed to login');
  const data = await response.json();
  localStorage.setItem('token', data.token);
  return data;
}

export async function checkout(shippingAddress: any) {
  const body = JSON.stringify({ shippingAddress: shippingAddress, paymentMethod: 'credit_card' })
  console.log(body)
  const response = await fetch(`${API_URL}/orders`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${localStorage.getItem('token')}`
    },
    body
  });
  if (!response.ok) throw new Error('Failed to create order');
  return response.json();
}
