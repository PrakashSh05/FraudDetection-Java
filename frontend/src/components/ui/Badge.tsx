import React from 'react';

interface BadgeProps {
  children: React.ReactNode;
  variant?: 'success' | 'warning' | 'danger' | 'info' | 'neutral';
  size?: 'sm' | 'md';
}

export const Badge: React.FC<BadgeProps> = ({
  children,
  variant = 'neutral',
  size = 'md',
}) => {
  const variantStyles = {
    success: 'bg-emerald-50 text-[#10B981] border-emerald-200',
    warning: 'bg-amber-50 text-[#F59E0B] border-amber-200',
    danger: 'bg-rose-50 text-[#EF4444] border-rose-200',
    info: 'bg-blue-50 text-[#3B82F6] border-blue-200',
    neutral: 'bg-gray-100 text-gray-700 border-gray-200',
  };

  const sizeStyles = {
    sm: 'px-2 py-0.5 text-xs',
    md: 'px-2.5 py-1 text-xs font-semibold',
  };

  return (
    <span className={`inline-flex items-center rounded-md border ${variantStyles[variant]} ${sizeStyles[size]}`}>
      {children}
    </span>
  );
};
