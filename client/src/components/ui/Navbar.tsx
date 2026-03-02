import React from 'react';
import Button from './Button';

export default function Navbar({ title, onLogout }: { title?: string; onLogout?: () => void }) {
  return (
    <header className="flex justify-between items-center mb-8">
      <h1 className="text-3xl font-extrabold text-gray-900">{title || 'DeskNova'}</h1>
      {onLogout && <Button variant="danger" onClick={onLogout}>Logout</Button>}
    </header>
  );
}
