import { useAnalyticsOverview, useQueueSummary } from '../api/useDashboardData';
import { StatCard } from '../../../components/ui/StatCard';
import { SkeletonCard } from './SkeletonLoaders';
import { Activity, CheckCircle, AlertTriangle, XCircle, ShieldAlert, Flame, Gauge } from 'lucide-react';

export const KpiCardsSection = () => {
  const { data: overview, isLoading: isOverviewLoading, isError: isOverviewError } = useAnalyticsOverview();
  const { data: queueSummary, isLoading: isQueueLoading } = useQueueSummary();

  if (isOverviewLoading || isQueueLoading) {
    return (
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4 mb-6">
        {Array.from({ length: 7 }).map((_, i) => (
          <SkeletonCard key={i} />
        ))}
      </div>
    );
  }

  if (isOverviewError) {
    return (
      <div className="bg-rose-500/10 border border-rose-500/30 rounded-2xl p-4 mb-6 text-rose-300 text-xs font-semibold">
        Failed to load KPI metrics. Please check network connectivity or backend availability.
      </div>
    );
  }

  return (
    <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4 mb-6">
      <StatCard
        title="Total Transactions"
        value={overview?.totalTransactions?.toLocaleString() ?? 0}
        icon={<Activity className="w-5 h-5 text-indigo-400" />}
        subtitle="All evaluated events"
      />
      <StatCard
        title="Approved Txns"
        value={overview?.approvedTransactions?.toLocaleString() ?? 0}
        icon={<CheckCircle className="w-5 h-5 text-emerald-400" />}
        subtitle="Auto-cleared transactions"
      />
      <StatCard
        title="Review Tier Txns"
        value={overview?.reviewTransactions?.toLocaleString() ?? 0}
        icon={<AlertTriangle className="w-5 h-5 text-amber-400" />}
        subtitle="Flagged for manual review"
      />
      <StatCard
        title="Rejected Txns"
        value={overview?.rejectedTransactions?.toLocaleString() ?? 0}
        icon={<XCircle className="w-5 h-5 text-rose-400" />}
        subtitle="Blocked high-risk txns"
      />
      <StatCard
        title="Open Fraud Cases"
        value={queueSummary?.openCases?.toLocaleString() ?? 0}
        icon={<ShieldAlert className="w-5 h-5 text-[#E94F37]" />}
        subtitle="Awaiting analyst review"
      />
      <StatCard
        title="Critical Priority Cases"
        value={queueSummary?.criticalCases?.toLocaleString() ?? 0}
        icon={<Flame className="w-5 h-5 text-rose-500" />}
        subtitle="Immediate SLA required"
      />
      <StatCard
        title="Avg Risk Score"
        value={overview?.averageRiskScore ? `${overview.averageRiskScore.toFixed(1)} / 100` : '0 / 100'}
        icon={<Gauge className="w-5 h-5 text-purple-400" />}
        subtitle="System risk score index"
      />
    </div>
  );
};
