import React from 'react';

export default function Modal({ children, onClose }: { children: React.ReactNode; onClose?: () => void }) {
  return (
    
    <div className="fixed inset-0 z-40 flex items-center justify-center">
      {/* Overlay with glassy blur and semi-transparent background */}
      <div className="absolute inset-0 bg-white/10 backdrop-blur-md" />

      {/* Dialog content with glassmorphism */}
      <div className="relative w-full max-w-lg rounded-lg border bg-secondary shadow-2xl p-6">
        {onClose && (
          <div className="mb-2">
            <button onClick={onClose} className="text-sm text-blue-200 hover:underline hover:cursor-pointer">Close</button>
          </div>
        )}
        {children}
      </div>
    </div>
  );
}
