import { useDailyTrend } from '../api/useDashboardData';
import { DashboardWidget } from '../../../components/ui/DashboardWidget';
import { SkeletonChart } from './SkeletonLoaders';
import { LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer, Legend } from 'recharts';
import { CHART_COLORS } from '../../../lib/chartColors';

export const DailyTrendWidget = () => {
  const { data, isLoading, isError, refetch } = useDailyTrend();

  return (
    <DashboardWidget
      title="Daily Transaction & Risk Trend"
      subtitle="Daily volume vs flagged high-risk transactions"
      isLoading={isLoading}
      isError={isError}
      isEmpty={!data || data.length === 0}
      errorMessage="Failed to load daily trend telemetry"
      emptyMessage="No daily trend telemetry available"
      onRetry={() => refetch()}
      skeleton={<SkeletonChart />}
    >
      <div className="h-64">
        <ResponsiveContainer width="100%" height="100%">
          <LineChart data={data || []} margin={{ top: 10, right: 10, left: -20, bottom: 0 }}>
            <CartesianGrid strokeDasharray="3 3" vertical={false} stroke="#F3F4F6" />
            <XAxis dataKey="date" tick={{ fontSize: 11, fill: '#6C757D' }} axisLine={false} />
            <YAxis tick={{ fontSize: 11, fill: '#6C757D' }} axisLine={false} />
            <Tooltip
              contentStyle={{ borderRadius: '8px', border: '1px solid #E5E7EB', fontSize: '12px' }}
            />
            <Legend verticalAlign="top" height={36} iconType="plainline" />
            <Line
              type="monotone"
              dataKey="totalTransactions"
              name="Total Txns"
              stroke={CHART_COLORS.dark}
              strokeWidth={2}
              dot={{ r: 3 }}
              activeDot={{ r: 5 }}
            />
            <Line
              type="monotone"
              dataKey="flaggedTransactions"
              name="Flagged / Risk Txns"
              stroke={CHART_COLORS.primary}
              strokeWidth={2}
              dot={{ r: 3 }}
              activeDot={{ r: 5 }}
            />
          </LineChart>
        </ResponsiveContainer>
      </div>
    </DashboardWidget>
  );
};
