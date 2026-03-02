import React from 'react';
import cn from '../lib/cn';

// building blocks exported for flexibility
export function Pagination({ children }: { children: React.ReactNode }) {
  return <nav className="flex items-center justify-center mt-6">{children}</nav>;
}

export function PaginationContent({ children }: { children: React.ReactNode }) {
  return <ul className="flex items-center space-x-1">{children}</ul>;
}

export function PaginationItem({ children }: { children: React.ReactNode }) {
  return <li>{children}</li>;
}

interface LinkProps extends React.AnchorHTMLAttributes<HTMLAnchorElement> {
  isActive?: boolean;
}

export function PaginationLink({ isActive, className, ...rest }: LinkProps) {
  return (
    <a
      {...rest}
      className={cn(
        'px-3 py-1 rounded text-sm cursor-pointer',
        isActive ? 'bg-blue-600 text-white' : 'text-gray-700 hover:bg-gray-100',
        className || ''
      )}
    />
  );
}

export function PaginationPrevious({ href = '#', onClick, disabled = false }: { href?: string; onClick?: () => void; disabled?: boolean }) {
  return (
    <a
      href={href}
      onClick={e => {
        if (disabled) {
          e.preventDefault();
          return;
        }
        if (onClick) {
          e.preventDefault();
          onClick();
        }
      }}
      className={cn(
        'px-2 py-1 rounded text-sm cursor-pointer',
        disabled ? 'text-gray-400 cursor-not-allowed' : 'text-gray-700 hover:bg-gray-100'
      )}
    >
      ‹
    </a>
  );
}

export function PaginationNext({ href = '#', onClick, disabled = false }: { href?: string; onClick?: () => void; disabled?: boolean }) {
  return (
    <a
      href={href}
      onClick={e => {
        if (disabled) {
          e.preventDefault();
          return;
        }
        if (onClick) {
          e.preventDefault();
          onClick();
        }
      }}
      className={cn(
        'px-2 py-1 rounded text-sm cursor-pointer',
        disabled ? 'text-gray-400 cursor-not-allowed' : 'text-gray-700 hover:bg-gray-100'
      )}
    >
      ›
    </a>
  );
}

export function PaginationEllipsis() {
  return <span className="px-2">…</span>;
}

// default export convenience component that generates pages given props
interface DefaultPaginationProps {
  currentPage: number;
  totalPages: number;
  onPageChange: (p: number) => void;
}

export default function DefaultPagination({ currentPage, totalPages, onPageChange }: DefaultPaginationProps) {
  const pages = [] as number[];
  for (let i = 1; i <= totalPages; i++) pages.push(i);

  return (
    <Pagination>
      <PaginationContent>
        <PaginationItem>
          <PaginationPrevious disabled={currentPage <= 1} onClick={() => onPageChange(currentPage - 1)} />
        </PaginationItem>
        {pages.map(p => (
          <PaginationItem key={p}>
            <PaginationLink
              onClick={() => onPageChange(p)}
              isActive={p === currentPage}
            >
              {p}
            </PaginationLink>
          </PaginationItem>
        ))}
        <PaginationItem>
          <PaginationNext disabled={currentPage >= totalPages} onClick={() => onPageChange(currentPage + 1)} />
        </PaginationItem>
      </PaginationContent>
    </Pagination>
  );
}
