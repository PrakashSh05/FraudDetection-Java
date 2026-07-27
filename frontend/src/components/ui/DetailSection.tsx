import { ReactNode } from 'react';

interface DetailSectionProps {
  title: string;
  subtitle?: string;
  action?: ReactNode;
  children: ReactNode;
  className?: string;
}

export const DetailSection = ({
  title,
  subtitle,
  action,
  children,
  className = '',
}: DetailSectionProps) => {
  return (
    <div className={`bg-black border border-neutral-800 rounded-2xl p-6 shadow-2xl ${className}`}>
      <div className="flex items-center justify-between pb-4 mb-4 border-b border-neutral-800">
        <div>
          <h3 className="text-base font-bold text-white tracking-tight">{title}</h3>
          {subtitle && <p className="text-xs text-neutral-400 mt-0.5">{subtitle}</p>}
        </div>
        {action && <div>{action}</div>}
      </div>
      {children}
    </div>
  );
};
