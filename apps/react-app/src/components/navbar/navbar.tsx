import { Link } from 'react-router-dom';
import { ShoppingCart, User, LogOut } from 'lucide-react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { getCart, logout } from '../../api';
import { useAuth } from '../../context/auth-context';
import toast from 'react-hot-toast';

export default function Navbar() {
  const { isAuthenticated, checkAuth, isLoading } = useAuth();
  const queryClient = useQueryClient();

  const { data: cartData } = useQuery({
    queryKey: ['cart'],
    queryFn: getCart,
    enabled: isAuthenticated
  });

  const logoutMutation = useMutation({
    mutationFn: logout,
    onSuccess: async () => {
      await checkAuth();
      queryClient.invalidateQueries();
      toast.success('Logged out successfully');
    }
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
            {!isLoading && (
              isAuthenticated ? (
                <>
                  <Link to="/cart" className="relative">
                    <ShoppingCart className="h-6 w-6 text-gray-600" />
                    {itemCount > 0 && (
                      <span className="absolute -top-2 -right-2 bg-blue-500 text-white text-xs rounded-full h-5 w-5 flex items-center justify-center">
                        {itemCount}
                      </span>
                    )}
                  </Link>
                  <button
                    onClick={() => logoutMutation.mutate()}
                    className="flex items-center text-gray-600 hover:text-gray-800"
                    disabled={logoutMutation.isPending}
                  >
                    <LogOut className="h-6 w-6" />
                  </button>
                </>
              ) : (
                <Link to="/login">
                  <User className="h-6 w-6 text-gray-600" />
                </Link>
              )
            )}
          </div>
        </div>
      </div>
    </nav>
  );
}
