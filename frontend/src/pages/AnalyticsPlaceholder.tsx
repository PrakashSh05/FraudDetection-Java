import { PageHeader } from '../components/ui/PageHeader';
import { StatCard } from '../components/ui/StatCard';
import { useAnalyticsOverview } from '../features/dashboard/api/useDashboardData';
import { RiskDistributionWidget } from '../features/dashboard/components/RiskDistributionWidget';
import { DailyTrendWidget } from '../features/dashboard/components/DailyTrendWidget';
import { TopRulesWidget } from '../features/dashboard/components/TopRulesWidget';
import { Activity, ShieldCheck, AlertTriangle, ShieldX, Gauge } from 'lucide-react';

export const AnalyticsPlaceholder = () => {
  const { data: overview, isLoading: overviewLoading } = useAnalyticsOverview();

  const total = overview?.totalTransactions || 0;
  const approvedPct = total > 0 ? Math.round(((overview?.approvedTransactions || 0) / total) * 100) : 0;
  const reviewPct = total > 0 ? Math.round(((overview?.reviewTransactions || 0) / total) * 100) : 0;
  const rejectedPct = total > 0 ? Math.round(((overview?.rejectedTransactions || 0) / total) * 100) : 0;

  return (
    <div className="space-y-6">
      <PageHeader
        title="Fraud Risk Analytics"
        subtitle="Aggregated risk telemetry, top rule execution frequencies, and daily trend analysis"
      />

      {/* KPI Overview Grid */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-5 gap-4">
        <StatCard
          title="TOTAL EVALUATED"
          value={overviewLoading ? '...' : total.toLocaleString()}
          subtitle="Total transaction volume"
          icon={<Activity className="w-5 h-5 text-indigo-500" />}
        />
        <StatCard
          title="AUTO-APPROVED"
          value={overviewLoading ? '...' : `${approvedPct}%`}
          subtitle={`${overview?.approvedTransactions || 0} clear transactions`}
          icon={<ShieldCheck className="w-5 h-5 text-emerald-500" />}
        />
        <StatCard
          title="REVIEW TIER"
          value={overviewLoading ? '...' : `${reviewPct}%`}
          subtitle={`${overview?.reviewTransactions || 0} flagged for review`}
          icon={<AlertTriangle className="w-5 h-5 text-amber-500" />}
        />
        <StatCard
          title="REJECTED TIER"
          value={overviewLoading ? '...' : `${rejectedPct}%`}
          subtitle={`${overview?.rejectedTransactions || 0} blocked transactions`}
          icon={<ShieldX className="w-5 h-5 text-rose-500" />}
        />
        <StatCard
          title="AVG RISK INDEX"
          value={overviewLoading ? '...' : `${overview?.averageRiskScore || 0} / 100`}
          subtitle="System-wide risk score average"
          icon={<Gauge className="w-5 h-5 text-purple-500" />}
        />
      </div>

      {/* Analytics Charts Grid */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <RiskDistributionWidget />
        <DailyTrendWidget />
      </div>

      {/* Top Rules Breakdown */}
      <div className="grid grid-cols-1 gap-6">
        <TopRulesWidget />
      </div>
    </div>
  );
};
