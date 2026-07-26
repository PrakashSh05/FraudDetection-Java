import React from 'react';
import { PageHeader } from '../components/ui/PageHeader';
import { StatCard } from '../components/ui/StatCard';
import { Card } from '../components/ui/Card';
import { LayoutDashboard, AlertTriangle, CheckCircle, ShieldAlert } from 'lucide-react';

export const DashboardPlaceholder: React.FC = () => {
  return (
    <div>
      <PageHeader
        title="Transaction Risk Dashboard"
        subtitle="Real-time transaction risk scoring telemetry and engine overview"
      />

      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-8">
        <StatCard
          title="Evaluated Transactions"
          value="1,248"
          icon={<LayoutDashboard className="w-5 h-5" />}
          trend={{ value: "12% vs yesterday", isPositive: true }}
        />
        <StatCard
          title="Flagged / Review"
          value="48"
          icon={<AlertTriangle className="w-5 h-5" />}
          trend={{ value: "4 cases open", isPositive: false }}
        />
        <StatCard
          title="Auto Approved"
          value="1,150"
          icon={<CheckCircle className="w-5 h-5" />}
          trend={{ value: "92.2% pass rate", isPositive: true }}
        />
        <StatCard
          title="Critical Risk Cases"
          value="3"
          icon={<ShieldAlert className="w-5 h-5" />}
          trend={{ value: "Requires SLA action", isPositive: false }}
        />
      </div>

      <Card title="Executive Risk Summary" subtitle="Foundation architecture initialized">
        <div className="p-8 text-center bg-gray-50 rounded-lg border border-dashed border-gray-200">
          <p className="text-sm font-medium text-gray-600">
            Frontend foundation initialized. Dashboard analytics widgets and live metrics will be integrated in Sprint 4 – Task 2.
          </p>
        </div>
      </Card>
    </div>
  );
};
