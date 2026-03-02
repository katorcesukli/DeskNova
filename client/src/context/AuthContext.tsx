/* eslint-disable react-refresh/only-export-components */
import React, { createContext, useState, type ReactNode } from 'react';
import { useNavigate } from 'react-router-dom';

interface User {
  id?: number | string;
  firstName: string;
  lastName: string;
  email: string;
  role: string;
}

interface AuthContextType {
  user: User | null;
  token: string | null;
  login: (user: User, token: string) => void;
  logout: () => void;
}

export const AuthContext = createContext<AuthContextType>({
  user: null,
  token: null,
  login: () => {},
  logout: () => {},
});

export const AuthProvider: React.FC<{ children: ReactNode }> = ({ children }) => {
  // read initial values from localStorage
  const [token, setToken] = useState<string | null>(() => localStorage.getItem('jwtToken'));
  const [user, setUser] = useState<User | null>(() => {
    const u = localStorage.getItem('loggedUser');
    return u ? JSON.parse(u) : null;
  });
  const navigate = useNavigate();

  const login = (u: User, t: string) => {
    setUser(u);
    setToken(t);
    localStorage.setItem('jwtToken', t);
    localStorage.setItem('loggedUser', JSON.stringify(u));
    redirectByRole(u.role);
  };

  const logout = () => {
    setUser(null);
    setToken(null);
    localStorage.removeItem('jwtToken');
    localStorage.removeItem('loggedUser');
    navigate('/login');
  };

  const redirectByRole = (role: string) => {
    switch (role.toUpperCase()) {
      case 'ADMIN':
        navigate('/admin');
        break;
      case 'AGENT':
        navigate('/agent');
        break;
      case 'CLIENT':
        navigate('/client');
        break;
      default:
        navigate('/login');
    }
  };

  return (
    <AuthContext.Provider value={{ user, token, login, logout }}>
      {children}
    </AuthContext.Provider>
  );
};
