import { ReactNode } from 'react';

interface BadgeProps {
  children: ReactNode;
  variant?: 'success' | 'warning' | 'danger' | 'info' | 'neutral';
  size?: 'sm' | 'md';
}

export const Badge = ({
  children,
  variant = 'neutral',
  size = 'md',
}: BadgeProps) => {
  const sizeStyles = {
    sm: 'px-2 py-0.5 text-[10px] font-extrabold',
    md: 'px-2.5 py-1 text-xs font-extrabold',
  };

  const variantStyles = {
    success: 'bg-emerald-500/10 text-emerald-400 border-emerald-500/40 shadow-emerald-500/10',
    warning: 'bg-amber-500/10 text-amber-400 border-amber-500/40 shadow-amber-500/10',
    danger: 'bg-rose-500/20 text-rose-400 border-rose-500/40 shadow-rose-500/10',
    info: 'bg-orange-500/10 text-orange-400 border-orange-500/30 shadow-orange-500/10',
    neutral: 'bg-orange-500/10 text-orange-400 border-orange-500/30',
  };

  return (
    <span className={`inline-flex items-center gap-1.5 rounded-full border shadow-sm ${variantStyles[variant] || variantStyles.neutral} ${sizeStyles[size]}`}>
      {children}
    </span>
  );
};
