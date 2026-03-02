import React from 'react';
import cn from '../../lib/cn';

type Variant = 'default' | 'primary' | 'danger' | 'ghost';

interface Props extends React.ButtonHTMLAttributes<HTMLButtonElement> {
  variant?: Variant;
}

export default function Button({ variant = 'default', className, children, ...rest }: Props) {
  const base = 'inline-flex items-center justify-center rounded-md px-3 py-1.5 text-sm font-medium transition cursor-pointer disabled:cursor-not-allowed';
  const variants: Record<Variant, string> = {
    default: 'bg-gray-100 text-gray-800 hover:bg-gray-200',
    primary: 'bg-blue-600 text-white hover:bg-blue-700',
    danger: 'bg-red-600 text-white hover:bg-red-700',
    ghost: 'bg-transparent text-gray-700 hover:bg-gray-100',
  };
  return (
    <button className={cn(base, variants[variant], className || '')} {...rest}>
      {children}
    </button>
  );
}
