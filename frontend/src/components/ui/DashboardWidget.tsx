import React, { ReactNode } from 'react';
import { Card } from './Card';
import { Button } from './Button';
import { AlertCircle } from 'lucide-react';

interface DashboardWidgetProps {
  title: string;
  subtitle?: string;
  action?: ReactNode;
  isLoading: boolean;
  isError: boolean;
  isEmpty?: boolean;
  errorMessage?: string;
  emptyMessage?: string;
  onRetry?: () => void;
  skeleton?: ReactNode;
  children: ReactNode;
}

export const DashboardWidget: React.FC<DashboardWidgetProps> = ({
  title,
  subtitle,
  action,
  isLoading,
  isError,
  isEmpty = false,
  errorMessage = 'Failed to load widget data',
  emptyMessage = 'No data available',
  onRetry,
  skeleton,
  children,
}) => {
  if (isLoading) {
    if (skeleton) return <>{skeleton}</>;
    return (
      <Card title={title} subtitle={subtitle}>
        <div className="h-64 flex items-center justify-center animate-pulse bg-gray-50 rounded-lg">
          <div className="h-6 w-32 bg-gray-200 rounded"></div>
        </div>
      </Card>
    );
  }

  if (isError) {
    return (
      <Card title={title} subtitle={subtitle}>
        <div className="h-64 flex flex-col items-center justify-center p-6 text-center bg-rose-50/50 rounded-lg border border-rose-100">
          <AlertCircle className="w-8 h-8 text-[#EF4444] mb-2" />
          <p className="text-xs font-semibold text-[#EF4444]">{errorMessage}</p>
          {onRetry && (
            <div className="mt-3">
              <Button size="sm" variant="outline" onClick={onRetry}>
                Retry
              </Button>
            </div>
          )}
        </div>
      </Card>
    );
  }

  if (isEmpty) {
    return (
      <Card title={title} subtitle={subtitle}>
        <div className="h-64 flex items-center justify-center text-xs text-gray-400 bg-gray-50 rounded-lg border border-dashed border-gray-200">
          {emptyMessage}
        </div>
      </Card>
    );
  }

  return (
    <Card title={title} subtitle={subtitle} action={action}>
      {children}
    </Card>
  );
};
