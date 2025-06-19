import { createContext, useContext, useState, useEffect } from 'react';
import { useQuery, useQueryClient } from '@tanstack/react-query';

// Import the API functions
const API_URL = 'http://localhost:3000/api';

// Define the user type
type User = {
  id: string;
  email: string;
  username: string;
  role: string;
};

type AuthContextType = {
  user: User | null;
  isAuthenticated: boolean;
  isLoading: boolean;
  checkAuth: () => Promise<void>;
};

const AuthContext = createContext<AuthContextType | null>(null);

export function AuthProvider({ children }: { children: React.ReactNode }) {
  const [user, setUser] = useState<User | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const queryClient = useQueryClient();

  // Use React Query for profile fetching
  const { data: profileData, refetch } = useQuery({
    queryKey: ['auth-profile'],
    queryFn: async () => {
      const response = await fetch(`${API_URL}/users/profile`, {
        credentials: 'include',
      });
      if (!response.ok) {
        throw new Error('Not authenticated');
      }
      return response.json();
    },
    retry: false,
    enabled: false, // Don't fetch automatically
  });

  // Function to check authentication status
  const checkAuth = async () => {
    setIsLoading(true);
    try {
      const result = await refetch();
      if (result.isSuccess) {
        setUser(result.data.user);
        // Invalidate any queries that depend on auth state
        queryClient.invalidateQueries({ queryKey: ['cart'] });
      } else {
        setUser(null);
      }
    } catch (error) {
      setUser(null);
    } finally {
      setIsLoading(false);
    }
  };

  // Check auth status on mount
  useEffect(() => {
    checkAuth();
  }, []);

  // Update user when profile data changes
  useEffect(() => {
    if (profileData?.user) {
      setUser(profileData.user);
    }
  }, [profileData]);

  const value = {
    user,
    isAuthenticated: !!user,
    isLoading,
    checkAuth,
  };

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth() {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
} 
