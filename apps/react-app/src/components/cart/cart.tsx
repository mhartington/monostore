import { Link, useNavigate } from 'react-router-dom';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { Trash2, Minus, Plus } from 'lucide-react';
import toast from 'react-hot-toast';
import { getCart, updateCartItem, removeFromCart } from '../../api';

export default function Cart() {
  const navigate = useNavigate();
  const queryClient = useQueryClient();

  const { data, isLoading } = useQuery({
    queryKey: ['cart'],
    queryFn: getCart,
    enabled: !!localStorage.getItem('token')
  });

  const updateItemMutation = useMutation({
    mutationFn: ({ productId, quantity }: { productId: string; quantity: number }) =>
      updateCartItem(productId, quantity),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['cart'] });
    },
    onError: () => {
      toast.error('Failed to update item');
    }
  });

  const removeItemMutation = useMutation({
    mutationFn: (productId: string) => removeFromCart(productId),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['cart'] });
      toast.success('Item removed');
    },
    onError: () => {
      toast.error('Failed to remove item');
    }
  });

  if (!localStorage.getItem('token')) {
    return (
      <div className="text-center py-12">
        <h2 className="text-2xl font-semibold mb-4">Please login to view your cart</h2>
        <Link to="/login" className="text-blue-600 hover:text-blue-700">
          Go to Login
        </Link>
      </div>
    );
  }

  if (isLoading) return <div>Loading...</div>;

  const cart = data?.cart;
  if (!cart || cart.items.length === 0) {
    return (
      <div className="text-center py-12">
        <h2 className="text-2xl font-semibold mb-4">Your cart is empty</h2>
        <Link to="/" className="text-blue-600 hover:text-blue-700">
          Continue Shopping
        </Link>
      </div>
    );
  }

  return (
    <div className="max-w-4xl mx-auto">
      <h1 className="text-2xl font-bold mb-8">Shopping Cart</h1>
      <div className="bg-white rounded-lg shadow-sm">
        {cart.items.map((item) => (
          <div
            key={item.product.id}
            className="flex items-center p-6 border-b last:border-b-0"
          >
            <img
              src={item.product.image}
              alt={item.product.name}
              className="w-24 h-24 object-cover rounded"
            />
            <div className="ml-6 flex-1">
              <h3 className="text-lg font-semibold">{item.product.name}</h3>
              <p className="text-gray-600">${item.product.price.toFixed(2)}</p>
            </div>
            <div className="flex items-center space-x-4">
              <button
                onClick={() => updateItemMutation.mutate({
                  productId: item.product.id,
                  quantity: Math.max(1, item.quantity - 1)
                })}
                className="p-2 rounded-full hover:bg-gray-100"
              >
                <Minus className="h-4 w-4" />
              </button>
              <span className="text-lg font-medium">{item.quantity}</span>
              <button
                onClick={() => updateItemMutation.mutate({
                  productId: item.product.id,
                  quantity: item.quantity + 1
                })}
                className="p-2 rounded-full hover:bg-gray-100"
              >
                <Plus className="h-4 w-4" />
              </button>
              <button
                onClick={() => removeItemMutation.mutate(item.product.id)}
                className="p-2 text-red-500 hover:bg-red-50 rounded-full"
              >
                <Trash2 className="h-5 w-5" />
              </button>
            </div>
          </div>
        ))}
        <div className="p-6 border-t">
          <div className="flex justify-between items-center mb-6">
            <span className="text-lg font-semibold">Total:</span>
            <span className="text-2xl font-bold">${cart.total.toFixed(2)}</span>
          </div>
          <button
            onClick={() => navigate('/checkout')}
            className="w-full bg-blue-600 text-white py-3 px-6 rounded-lg hover:bg-blue-700"
          >
            Proceed to Checkout
          </button>
        </div>
      </div>
    </div>
  );
}
