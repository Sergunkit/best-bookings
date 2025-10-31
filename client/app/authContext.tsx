import React, {
  useState, useEffect, type PropsWithChildren, useMemo, useContext, useCallback,
} from 'react';
import { createContext } from 'react';
import client from '@api';
import type { UserDto } from '@api';

interface AuthContextType {
  isAuthenticated: boolean;
  user: UserDto | null;
  login: (userData: UserDto, accessToken: string, refreshToken: string) => void;
  logout: () => void;
  checkAuth: () => Promise<void>;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

const AuthProvider: React.FC<PropsWithChildren> = ({ children }) => {
  const [isAuthenticated, setIsAuthenticated] = useState<boolean>(false);
  const [user, setUser] = useState<UserDto | null>(null);
  const [loading, setLoading] = useState(true);

  const login = (userData: UserDto, accessToken: string, refreshToken: string) => {
    setUser(userData);
    setIsAuthenticated(true);
    localStorage.setItem('user', JSON.stringify(userData));
    localStorage.setItem('accessToken', accessToken);
    localStorage.setItem('refreshToken', refreshToken);
  };

  const logout = () => {
    setUser(null);
    setIsAuthenticated(false);
    localStorage.removeItem('user');
    localStorage.removeItem('accessToken');
    localStorage.removeItem('refreshToken');
  };

  const checkAuth = useCallback(async () => {
    setLoading(true);
    try {
      const accessToken = localStorage.getItem('accessToken');
      if (!accessToken) {
        setLoading(false);
        return;
      }

      const userResponse = await client.userMeGet();
      // Note: login function here is called with only userData, as tokens are already handled.
      // If userMeGet returns new tokens, they should be passed to login.
      // For now, assuming userMeGet only returns user data.
      setUser(userResponse);
      setIsAuthenticated(true);
    } catch (error) {
      console.error('checkAuth failed:', error);
      logout();
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    // eslint-disable-next-line @typescript-eslint/no-floating-promises
    checkAuth();
  }, [checkAuth]);

  const value = useMemo(() => ({
    isAuthenticated,
    user,
    login,
    logout,
    checkAuth,
  }), [checkAuth, isAuthenticated, user]);

  if (loading) {
    return <div>Loading...</div>;
  }

  return (
    <AuthContext.Provider value={value}>
      {children}
    </AuthContext.Provider>
  );
};

const useAuth = (): AuthContextType => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
};

export default AuthProvider;
export { AuthContext, useAuth };
