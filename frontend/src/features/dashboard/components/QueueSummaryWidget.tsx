import { useQueueSummary } from '../api/useDashboardData';
import { DashboardWidget } from '../../../components/ui/DashboardWidget';
import { Badge } from '../../../components/ui/Badge';
import { SkeletonSummary } from './SkeletonLoaders';

export const QueueSummaryWidget = () => {
  const { data, isLoading, isError, refetch } = useQueueSummary();

  const items = data ? [
    { label: 'Open Cases', count: data.openCases, variant: 'warning' as const },
    { label: 'Assigned Cases', count: data.assignedCases, variant: 'info' as const },
    { label: 'Under Review', count: data.underReviewCases, variant: 'info' as const },
    { label: 'Approved Cases', count: data.approvedCases, variant: 'success' as const },
    { label: 'Declined Cases', count: data.declinedCases, variant: 'danger' as const },
    { label: 'Escalated Cases', count: data.escalatedCases, variant: 'danger' as const },
    { label: 'Closed Cases', count: data.closedCases, variant: 'neutral' as const },
  ] : [];

  return (
    <DashboardWidget
      title="Case Queue Status Breakdown"
      subtitle="Current count of cases by workflow lifecycle stage"
      isLoading={isLoading}
      isError={isError}
      isEmpty={!data}
      errorMessage="Failed to load queue summary statistics"
      emptyMessage="No queue statistics available"
      onRetry={() => refetch()}
      skeleton={<SkeletonSummary />}
    >
      <div className="grid grid-cols-1 sm:grid-cols-2 gap-3 mb-4">
        {items.map((item) => (
          <div
            key={item.label}
            className="flex items-center justify-between p-3 bg-gray-50 rounded-lg border border-gray-100"
          >
            <span className="text-xs font-medium text-[#393E41]">{item.label}</span>
            <Badge variant={item.variant}>{item.count.toLocaleString()}</Badge>
          </div>
        ))}
      </div>

      <div className="pt-3 border-t border-gray-100 flex items-center justify-between text-xs text-gray-500">
        <span>Total Tracked Cases:</span>
        <span className="font-bold text-[#393E41]">{data?.totalCases?.toLocaleString() ?? 0}</span>
      </div>
    </DashboardWidget>
  );
};
