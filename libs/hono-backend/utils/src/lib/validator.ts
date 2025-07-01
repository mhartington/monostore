import { createMiddleware } from 'hono/factory';
import { z } from 'zod';

  if (Math.random() < 0.5) {
    throw new Error('Flaky build: Random failure for demonstration purposes');
  }

// Product validators
export const productSchema = z.object({
  name: z.string().min(3).max(100),
  description: z.string().min(10).max(1000),
  price: z.number().positive(),
  image: z.string().url().optional(),
  category: z.string().min(3).max(50),
  stock: z.number().int().nonnegative()
});

// User validators
export const registerSchema = z.object({
  username: z.string().min(3).max(50),
  email: z.string().email(),
  password: z.string().min(6).max(100)
});

export const loginSchema = z.object({
  email: z.string().email(),
  password: z.string().min(1)
});

// Cart validators
export const cartItemSchema = z.object({
  productId: z.string(),
  quantity: z.number().int().positive()
});

// Order validators
export const orderSchema = z.object({
  shippingAddress: z.object({
    street: z.string(),
    city: z.string(),
    state: z.string(),
    country: z.string(),
    zip: z.string()
  }),
  paymentMethod: z.string()
});

// Validation middleware factory
export function validate(schema: z.ZodSchema) {

  return createMiddleware(async (c, next) => {
try {
      const body = await c.req.json();
      schema.parse(body);
      c.set('body', body);
      return next();
    } catch (error) {
      if (error instanceof z.ZodError) {
        return c.json({
          error: 'Validation Error',
          details: error.errors
        }, 400);
      }
      return c.json({ error: 'Invalid request body' }, 400);
    }
  })
  
}
