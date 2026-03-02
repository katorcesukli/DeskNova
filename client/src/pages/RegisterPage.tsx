import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import Input from '../components/ui/Input';
import Button from '../components/ui/Button';

const BASE_URL = 'http://localhost:8080/api';

const RegisterPage: React.FC = () => {
  const navigate = useNavigate();
  const [firstName, setFirstName] = useState('');
  const [lastName, setLastName] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');

    if (!firstName || !lastName) {
      setError('First and last name required');
      return;
    }
    const emailPattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailPattern.test(email)) {
      setError('Invalid email');
      return;
    }
    if (password.length < 4) {
      setError('Password must be at least 4 characters');
      return;
    }

    try {
      const payload = { firstName, lastName, email, password, role: 'CLIENT' };
      const res = await fetch(`${BASE_URL}/auth/register`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload),
      });
      if (!res.ok) {
        const err = await res.json();
        throw new Error(err.message || 'Registration failed');
      }
      alert('Registration successful!');
      navigate('/login');
    } catch (err: unknown) {
      console.error(err);
      if (err instanceof Error) {
        setError(err.message);
      } else {
        setError('Registration error');
      }
    }
  };

  return (
    <div className="min-h-screen flex items-center justify-center bg-gray-100">
      <div className="w-full max-w-md bg-white rounded-lg shadow p-6">
        <h2 className="text-2xl font-bold mb-4 text-center">Create Account</h2>
        {error && <p className="text-red-600 text-sm mb-2">{error}</p>}
        <form onSubmit={handleSubmit} className="space-y-4">
          <Input type="text" placeholder="First Name" value={firstName} onChange={(e) => setFirstName(e.target.value)} label="First Name" />
          <Input type="text" placeholder="Last Name" value={lastName} onChange={(e) => setLastName(e.target.value)} label="Last Name" />
          <Input type="email" placeholder="Email" value={email} onChange={(e) => setEmail(e.target.value)} label="Email" />
          <Input type="password" placeholder="Password" value={password} onChange={(e) => setPassword(e.target.value)} label="Password" />
          <Button type="submit" className="w-full" variant="primary">Register</Button>
        </form>
        <p className="text-center mt-4 text-sm">
          Already have an account?{' '}
          <Link to="/login" className="text-blue-600 hover:underline">
            Login
          </Link>
        </p>
      </div>
    </div>
  );
};

export default RegisterPage;
