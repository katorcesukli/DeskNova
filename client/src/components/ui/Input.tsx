import React from 'react';
import cn from '../../lib/cn';

interface Props extends React.InputHTMLAttributes<HTMLInputElement> {
  label?: string;
}

export default function Input({ label, className, ...rest }: Props) {
  return (
    <div>
      {label && <label className="block text-sm font-medium text-gray-700 mb-1">{label}</label>}
      <input className={cn('w-full border rounded px-3 py-2', className || '')} {...rest} />
    </div>
  );
}
