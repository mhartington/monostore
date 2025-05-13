import { serve } from '@hono/node-server';
import { Hono } from 'hono';
import { cors } from 'hono/cors';
import { logger } from 'hono/logger';
import { HTTPException } from 'hono/http-exception';


import productRoutes from './routes/products.js';
import userRoutes from './routes/users.js';
import cartRoutes from './routes/cart.js';
import orderRoutes from './routes/orders.js';



const app = new Hono();


app.use('*', logger());
app.use('*', cors({
  origin: ['http://localhost:5173', 'http://localhost:4200'],
  allowMethods: ['GET', 'POST', 'PUT', 'DELETE', 'OPTIONS'],
  allowHeaders: ['Content-Type', 'Authorization'],
  exposeHeaders: ['Content-Length'],
  maxAge: 600,
  credentials: true,
}));


app.get('/', (c) => {
  return c.json({
    message: 'E-commerce API is running',
    version: '1.0.0',
    endpoints: {
      products: '/api/products',
      users: '/api/users',
      cart: '/api/cart',
      orders: '/api/orders'
    }
  });
});


app.route('/api/products', productRoutes);
app.route('/api/users', userRoutes);
app.route('/api/cart', cartRoutes);
app.route('/api/orders', orderRoutes);


app.onError((err, c) => {
  console.error(`[ERROR] ${err.message}`);
  const status = (err as HTTPException).status || 500;
  const message = status === 500 ? 'Internal Server Error' : err.message;
  return c.json({ error: message }, status);  
  
});


app.notFound((c) => {
  return c.json({ error: 'Not Found' }, 404);
});

const port = 3000;

console.log(`Server starting on port ${port}...`);
serve({
  fetch: app.fetch,
  port
});

console.log(`Server running at http://localhost:${port}`);
