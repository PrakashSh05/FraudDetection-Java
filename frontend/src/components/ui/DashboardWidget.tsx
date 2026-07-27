import { ReactNode } from 'react';
import { Card } from './Card';
import { Button } from './Button';
import { AlertCircle, Inbox } from 'lucide-react';

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

export const DashboardWidget = ({
  title,
  subtitle,
  action,
  isLoading,
  isError,
  isEmpty = false,
  errorMessage = 'Failed to load widget data',
  emptyMessage = 'No telemetry data available',
  onRetry,
  skeleton,
  children,
}: DashboardWidgetProps) => {
  if (isLoading) {
    if (skeleton) return <>{skeleton}</>;
    return (
      <Card title={title} subtitle={subtitle}>
        <div className="h-64 flex items-center justify-center animate-pulse bg-slate-950/60 rounded-xl border border-slate-800">
          <div className="h-6 w-32 bg-slate-800 rounded"></div>
        </div>
      </Card>
    );
  }

  if (isError) {
    return (
      <Card title={title} subtitle={subtitle}>
        <div className="h-64 flex flex-col items-center justify-center p-6 text-center bg-rose-500/10 rounded-xl border border-rose-500/20">
          <AlertCircle className="w-8 h-8 text-rose-400 mb-2" />
          <p className="text-xs font-semibold text-rose-300">{errorMessage}</p>
          {onRetry && (
            <div className="mt-3">
              <Button size="sm" variant="outline" onClick={onRetry}>
                Retry Connection
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
        <div className="h-64 flex flex-col items-center justify-center text-xs text-slate-500 bg-slate-950/40 rounded-xl border border-dashed border-slate-800/80 gap-2">
          <Inbox className="w-6 h-6 text-slate-600" />
          <span>{emptyMessage}</span>
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
