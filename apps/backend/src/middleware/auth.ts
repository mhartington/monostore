import jwt from 'jsonwebtoken';
import { User } from '../data/index.js';
import {createMiddleware} from 'hono/factory';

// JWT secret (in production, this would be in environment variables)
const JWT_SECRET = '0a732767d5ee374d4a3478077f84a009863';

// Generate JWT token
export function generateToken(user: User) {
  const payload = {
    id: user.id,
    email: user.email,
    role: user.role
  };
  
  return jwt.sign(payload, JWT_SECRET, { expiresIn: '1d' });
}

// Auth middleware
export const auth = createMiddleware(async (c, next) => {
  const authHeader = c.req.header('Authorization');
  
  if (!authHeader || !authHeader.startsWith('Bearer ')) {
    return c.json({ error: 'Unauthorized - No token provided' }, 401);
  }
  
  const token = authHeader.split(' ')[1];
  
  try {
    const decoded = jwt.verify(token, JWT_SECRET);
    c.set('user', decoded);
    return next();
  } catch (error) {
    return c.json({ error: 'Unauthorized - Invalid token' }, 401);
  }
})

// Admin middleware (checks if user has admin role)
export const adminOnly = createMiddleware(async (c, next) => {
  const user = c.get('user');
  
  if (!user || user.role !== 'admin') {
    return c.json({ error: 'Forbidden - Admin access required' }, 403);
  }
  
  return next();
});
