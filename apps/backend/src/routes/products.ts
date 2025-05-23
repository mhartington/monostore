import { Hono } from 'hono';
import { products, generateId, Product } from '../data/index';
import { auth, adminOnly } from '../middleware/auth';
import { validate, productSchema } from '../utils/validators';
type Variables = {
  body: Product
}

const app = new Hono<{Variables: Variables}>();

// Get all products
app.get('/', (c) => {
  
  const { category, sort } = c.req.query();
  let filteredProducts = [...products];
  
  // Filter by category
  if (category) {
    filteredProducts = filteredProducts.filter(p => p.category === category);
  }
  
  // Sort products
  if (sort) {
    if (sort === 'price-asc') {
      filteredProducts.sort((a, b) => a.price - b.price);
    } else if (sort === 'price-desc') {
      filteredProducts.sort((a, b) => b.price - a.price);
    } else if (sort === 'latest') {
      filteredProducts.sort((a, b) => new Date(b.created_at).getTime() - new Date(a.created_at).getTime());
    }
  }
  
  return c.json({ products: filteredProducts });
});

// Get product by ID
app.get('/:id', (c) => {
  const id = c.req.param('id');
  const product = products.find(p => p.id === id);
  
  if (!product) {
    return c.json({ error: 'Product not found' }, 404);
  }
  
  return c.json({ product });
});

// Create new product (admin only)
app.post('/', auth, adminOnly, validate(productSchema), (c) => {
  const body = c.get('body');
  
  const newProduct = {
    id: generateId(),
    ...body,
    created_at: new Date().toISOString()
  };
  
  products.push(newProduct);
  
  return c.json({ 
    message: 'Product created successfully',
    product: newProduct 
  }, 201);
});

// Update product (admin only)
app.put('/:id', auth, adminOnly, validate(productSchema), (c) => {
  const id = c.req.param('id');
  const body = c.get('body');
  
  const index = products.findIndex(p => p.id === id);
  
  if (index === -1) {
    return c.json({ error: 'Product not found' }, 404);
  }
  
  products[index] = {
    ...products[index],
    ...body,
    updated_at: new Date().toISOString()
  };
  
  return c.json({ 
    message: 'Product updated successfully',
    product: products[index] 
  });
});

// Delete product (admin only)
app.delete('/:id', auth, adminOnly, (c) => {
  const id = c.req.param('id');
  const index = products.findIndex(p => p.id === id);
  
  if (index === -1) {
    return c.json({ error: 'Product not found' }, 404);
  }
  
  const deletedProduct = products.splice(index, 1)[0];
  
  return c.json({ 
    message: 'Product deleted successfully',
    product: deletedProduct 
  });
});

export default app;
