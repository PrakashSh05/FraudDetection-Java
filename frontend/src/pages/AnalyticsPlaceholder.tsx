import React from 'react';
import { PageHeader } from '../components/ui/PageHeader';
import { Card } from '../components/ui/Card';
import { BarChart3 } from 'lucide-react';

export const AnalyticsPlaceholder: React.FC = () => {
  return (
    <div>
      <PageHeader
        title="Fraud Risk Analytics"
        subtitle="Aggregated Insights, Top Fired Rules, and Daily Risk Level Trends"
      />

      <Card title="Analytics Reporting Module">
        <div className="p-12 text-center bg-gray-50 rounded-lg border border-dashed border-gray-200 flex flex-col items-center">
          <BarChart3 className="w-8 h-8 text-[#E94F37] mb-3" />
          <p className="text-sm font-medium text-[#393E41]">
            Analytics charts and reporting tables will be rendered in Sprint 4 analytics views.
          </p>
        </div>
      </Card>
    </div>
  );
};
