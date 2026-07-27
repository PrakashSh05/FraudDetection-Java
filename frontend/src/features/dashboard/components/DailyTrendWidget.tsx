import { useDailyTrend } from '../api/useDashboardData';
import { DashboardWidget } from '../../../components/ui/DashboardWidget';
import { SkeletonChart } from './SkeletonLoaders';
import { LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer, Legend } from 'recharts';

export const DailyTrendWidget = () => {
  const { data, isLoading, isError, refetch } = useDailyTrend();

  return (
    <DashboardWidget
      title="Daily Transaction & Risk Trend"
      subtitle="Daily volume vs average risk score metrics"
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
            <CartesianGrid strokeDasharray="3 3" vertical={false} stroke="#262626" />
            <XAxis dataKey="date" tick={{ fontSize: 11, fill: '#A1A1AA' }} axisLine={false} />
            <YAxis tick={{ fontSize: 11, fill: '#A1A1AA' }} axisLine={false} />
            <Tooltip
              contentStyle={{
                backgroundColor: '#0A0A0A',
                borderRadius: '12px',
                borderColor: '#E94F37',
                color: '#FFFFFF',
                fontSize: '12px',
                boxShadow: '0 10px 25px -5px rgba(233, 79, 55, 0.3)',
              }}
              itemStyle={{ color: '#FFFFFF', fontWeight: 'bold' }}
              labelStyle={{ color: '#FFFFFF', fontWeight: 'bold' }}
            />
            <Legend verticalAlign="top" height={36} iconType="plainline" />
            <Line
              type="monotone"
              dataKey="transactions"
              name="Txn Volume"
              stroke="#3B82F6"
              strokeWidth={2.5}
              dot={{ r: 3.5, fill: '#3B82F6' }}
              activeDot={{ r: 6 }}
            />
            <Line
              type="monotone"
              dataKey="averageRiskScore"
              name="Avg Risk Score"
              stroke="#E94F37"
              strokeWidth={2.5}
              dot={{ r: 3.5, fill: '#E94F37' }}
              activeDot={{ r: 6 }}
            />
          </LineChart>
        </ResponsiveContainer>
      </div>
    </DashboardWidget>
  );
};
