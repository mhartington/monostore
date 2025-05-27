import { Hono } from 'hono';
import { carts, products } from '@monostore/backend-model';

import { auth } from '@monostore/backend-middleware';
import { validate, cartItemSchema } from '@monostore/backend-utils';

type Variables = {
  user: {
    id: string | number;
  };
  body: {
    productId: string;
    quantity: number;
  };
};

const app = new Hono<{ Variables: Variables }>();

// Initialize cart if it doesn't exist
function initializeCart(userId: string | number) {
  if (!carts[userId]) {
    carts[userId] = {
      items: [],
      total: 0,
    };
  }
  return carts[userId];
}

// Get cart
app.get('/', auth, (c) => {
  const { id: userId } = c.get('user');
  const cart = initializeCart(userId);
  return c.json({ cart });
});

// Add item to cart
app.post('/items', auth, validate(cartItemSchema), (c) => {
  const { id: userId } = c.get('user');
  const { productId, quantity } = c.get('body');

  // Find product
  const product = products.find((p) => p.id === productId);

  if (!product) {
    return c.json({ error: 'Product not found' }, 404);
  }

  if (quantity > product.stock) {
    return c.json({ error: 'Not enough stock available' }, 400);
  }

  const cart = initializeCart(userId);

  // Check if product already in cart
  const existingItem = cart.items.find(
    (item: { product: { id: any } }) => item.product.id === productId,
  );

  if (existingItem) {
    // Ensure the new quantity doesn't exceed stock
    const newQuantity = existingItem.quantity + quantity;

    if (newQuantity > product.stock) {
      return c.json({ error: 'Not enough stock available' }, 400);
    }

    existingItem.quantity = newQuantity;
    existingItem.subtotal = newQuantity * product.price;
  } else {
    // Add new item
    cart.items.push({
      product: {
        id: product.id,
        name: product.name,
        price: product.price,
        image: product.image,
      },
      quantity,
      subtotal: quantity * product.price,
    });
  }
  // Recalculate cart total
  cart.total = cart.items.reduce(
    (sum: any, item: { subtotal: any }) => sum + item.subtotal,
    0,
  );

  return c.json({
    message: 'Item added to cart',
    cart,
  });
});

// Update cart item
app.put('/items/:productId', auth, async (c) => {
  const { id: userId } = c.get('user');
  const productId = c.req.param('productId');
  const body = await c.req.json();
  const { quantity } = body;

  if (isNaN(quantity) || quantity < 1) {
    return c.json({ error: 'Invalid quantity' }, 400);
  }

  const cart = initializeCart(userId);
  const itemIndex = cart.items.findIndex(
    (item: { product: { id: string } }) => item.product.id === productId,
  );

  if (itemIndex === -1) {
    return c.json({ error: 'Item not found in cart' }, 404);
  }

  const product = products.find((p) => p.id === productId);

  if (quantity > product!.stock) {
    return c.json({ error: 'Not enough stock available' }, 400);
  }

  // Update item
  cart.items[itemIndex].quantity = quantity;
  cart.items[itemIndex].subtotal =
    quantity * cart.items[itemIndex].product.price;

  // Recalculate cart total
  cart.total = cart.items.reduce(
    (sum: any, item: { subtotal: any }) => sum + item.subtotal,
    0,
  );

  return c.json({
    message: 'Cart item updated',
    cart,
  });
});

// Remove item from cart
app.delete('/items/:productId', auth, (c) => {
  const { id: userId } = c.get('user');
  const productId = c.req.param('productId');

  const cart = initializeCart(userId);
  const itemIndex = cart.items.findIndex(
    (item: { product: { id: string } }) => item.product.id === productId,
  );

  if (itemIndex === -1) {
    return c.json({ error: 'Item not found in cart' }, 404);
  }

  // Remove item
  cart.items.splice(itemIndex, 1);

  // Recalculate cart total
  cart.total = cart.items.reduce(
    (sum: any, item: { subtotal: any }) => sum + item.subtotal,
    0,
  );

  return c.json({
    message: 'Item removed from cart',
    cart,
  });
});

// Clear cart
app.delete('/', auth, (c) => {
  const { id: userId } = c.get('user');

  carts[userId] = {
    items: [],
    total: 0,
  };

  return c.json({
    message: 'Cart cleared',
    cart: carts[userId],
  });
});

export { app as cartRoute };
