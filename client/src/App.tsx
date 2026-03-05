import React, { type ReactNode } from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider, AuthContext } from './context/AuthContext';

import LoginPage from './pages/LoginPage';
import RegisterPage from './pages/RegisterPage';
import AdminPage from './pages/AdminPage';
import AgentPage from './pages/AgentPage';
import ClientPage from './pages/ClientPage';

// simple wrapper to protect routes
const PrivateRoute: React.FC<{ children: ReactNode; roles?: string[] }> = ({ children, roles }) => {
  const { user } = React.useContext(AuthContext);
  if (!user) {
    return <Navigate to="/login" replace />;
  }
  if (roles && !roles.includes(user.role.toUpperCase())) {
    return <Navigate to="/login" replace />;
  }
  return children;
};

function App() {
  return (
    <Router>
      <AuthProvider>
        <Routes>
          <Route path="/" element={<Navigate to="/login" />} />
          <Route path="/login" element={<LoginPage />} />
          <Route path="/register" element={<RegisterPage />} />
          <Route
            path="/admin"
            element={
              <PrivateRoute roles={["ADMIN"]}>
                <AdminPage />
              </PrivateRoute>
            }
          />
          <Route
            path="/agent"
            element={
              <PrivateRoute roles={["AGENT"]}>
                <AgentPage />
              </PrivateRoute>
            }
          />
          <Route
            path="/client"
            element={
              <PrivateRoute roles={["CLIENT"]}>
                <ClientPage />
              </PrivateRoute>
            }
          />
        </Routes>
      </AuthProvider>
    </Router>
  );
}

export default App;
