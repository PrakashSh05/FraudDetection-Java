interface LoadingSpinnerProps {
  size?: 'sm' | 'md' | 'lg';
  label?: string;
}

export const LoadingSpinner = ({
  size = 'md',
  label = 'Loading...',
}: LoadingSpinnerProps) => {
  const sizeClasses = {
    sm: 'h-4 w-4 border-2',
    md: 'h-8 w-8 border-3',
    lg: 'h-12 w-12 border-4',
  };

  return (
    <div className="flex flex-col items-center justify-center p-8 text-slate-400">
      <div className={`animate-spin rounded-full border-[#E94F37] border-t-transparent ${sizeClasses[size]}`}></div>
      {label && <p className="mt-3 text-xs font-semibold text-slate-300">{label}</p>}
    </div>
  );
};
