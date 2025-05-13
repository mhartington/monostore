import { Link } from 'react-router-dom';
import { ShoppingCart, User } from 'lucide-react';
import { useQuery } from '@tanstack/react-query';
import { getCart } from '../../api';

export default function Navbar() {
  const { data: cartData } = useQuery({
    queryKey: ['cart'],
    queryFn: getCart,
    enabled: !!localStorage.getItem('token')
  });

  const itemCount = cartData?.cart?.items?.length || 0;

  return (
    <nav className="bg-white shadow-sm">
      <div className="container mx-auto px-4">
        <div className="flex items-center justify-between h-16">
          <Link to="/" className="text-xl font-bold text-gray-800">
            E-Shop
          </Link>
          
          <div className="flex items-center space-x-4">
            <Link to="/cart" className="relative">
              <ShoppingCart className="h-6 w-6 text-gray-600" />
              {itemCount > 0 && (
                <span className="absolute -top-2 -right-2 bg-blue-500 text-white text-xs rounded-full h-5 w-5 flex items-center justify-center">
                  {itemCount}
                </span>
              )}
            </Link>
            <Link to="/login">
              <User className="h-6 w-6 text-gray-600" />
            </Link>
          </div>
        </div>
      </div>
    </nav>
  );
}
