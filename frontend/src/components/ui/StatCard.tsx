import { ReactNode, memo } from 'react';

interface StatCardProps {
  title: string;
  value: string | number;
  icon?: ReactNode;
  trend?: {
    value: string;
    isPositive: boolean;
  };
  subtitle?: string;
}

export const StatCard = memo(({
  title,
  value,
  icon,
  trend,
  subtitle,
}: StatCardProps) => {
  return (
    <div className="bg-neutral-950 border border-neutral-800 rounded-2xl p-5 shadow-lg hover:border-orange-500/50 transition-all duration-200 group">
      <div className="flex items-center justify-between">
        <span className="text-[11px] font-bold text-neutral-400 uppercase tracking-wider">{title}</span>
        {icon && (
          <div className="p-2 rounded-xl bg-neutral-900 border border-neutral-800 text-[#E94F37] group-hover:border-[#E94F37]/50 transition-colors">
            {icon}
          </div>
        )}
      </div>
      <div className="mt-3 flex items-baseline justify-between">
        <span className="text-2xl font-extrabold text-white tracking-tight">{value}</span>
        {trend && (
          <span className="text-xs font-bold px-2.5 py-0.5 rounded-full bg-orange-500/10 text-orange-400 border border-orange-500/30">
            {trend.isPositive ? '↑' : '↓'} {trend.value}
          </span>
        )}
      </div>
      {subtitle && <p className="mt-1.5 text-xs text-neutral-400">{subtitle}</p>}
    </div>
  );
});

StatCard.displayName = 'StatCard';
