const API_URL = 'http://localhost:3000/api';

// Common fetch options for authenticated requests
const authFetchOptions: RequestInit = {
  credentials: 'include',
  headers: {
    'Content-Type': 'application/json'
  }
};

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
  const response = await fetch(`${API_URL}/cart`, authFetchOptions);
  if (!response.ok) throw new Error('Failed to fetch cart');
  return response.json();
}

export async function addToCart(productId: string, quantity: number) {
  const response = await fetch(`${API_URL}/cart/items`, {
    ...authFetchOptions,
    method: 'POST',
    body: JSON.stringify({ productId, quantity })
  });
  if (!response.ok) throw new Error('Failed to add to cart');
  return response.json();
}

export async function updateCartItem(productId: string, quantity: number) {
  const response = await fetch(`${API_URL}/cart/items/${productId}`, {
    ...authFetchOptions,
    method: 'PUT',
    body: JSON.stringify({ quantity })
  });
  if (!response.ok) throw new Error('Failed to update cart');
  return response.json();
}

export async function removeFromCart(productId: string) {
  const response = await fetch(`${API_URL}/cart/items/${productId}`, {
    ...authFetchOptions,
    method: 'DELETE'
  });
  if (!response.ok) throw new Error('Failed to remove from cart');
  return response.json();
}

export async function login(email: string, password: string) {
  const response = await fetch(`${API_URL}/users/login`, {
    ...authFetchOptions,
    method: 'POST',
    body: JSON.stringify({ email, password })
  });
  if (!response.ok) throw new Error('Failed to login');
  return response.json();
}

export async function logout() {
  const response = await fetch(`${API_URL}/users/logout`, {
    ...authFetchOptions,
    method: 'POST'
  });
  if (!response.ok) throw new Error('Failed to logout');
  return response.json();
}

export async function checkout(shippingAddress: any) {
  const response = await fetch(`${API_URL}/orders`, {
    ...authFetchOptions,
    method: 'POST',
    body: JSON.stringify({ 
      shippingAddress: shippingAddress, 
      paymentMethod: 'credit_card' 
    })
  });
  if (!response.ok) throw new Error('Failed to create order');
  return response.json();
}
