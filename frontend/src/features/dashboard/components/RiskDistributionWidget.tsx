import { useRiskDistribution } from '../api/useDashboardData';
import { DashboardWidget } from '../../../components/ui/DashboardWidget';
import { SkeletonChart } from './SkeletonLoaders';
import { PieChart, Pie, Cell, Tooltip, ResponsiveContainer, Legend } from 'recharts';

const CUSTOM_RISK_COLORS: Record<string, string> = {
  LOW: '#10B981',
  MEDIUM: '#F59E0B',
  HIGH: '#E94F37',
  CRITICAL: '#F43F5E',
};

export const RiskDistributionWidget = () => {
  const { data, isLoading, isError, refetch } = useRiskDistribution();

  const chartData = data?.map((item) => ({
    name: item.riskLevel,
    value: item.count,
    color: CUSTOM_RISK_COLORS[item.riskLevel] || '#64748B',
  })) || [];

  return (
    <DashboardWidget
      title="Risk Tier Distribution"
      subtitle="Breakdown of evaluated transactions by risk level"
      isLoading={isLoading}
      isError={isError}
      isEmpty={!data || data.length === 0}
      errorMessage="Failed to load risk distribution telemetry"
      emptyMessage="No risk distribution records found"
      onRetry={() => refetch()}
      skeleton={<SkeletonChart />}
    >
      <div className="h-64">
        <ResponsiveContainer width="100%" height="100%">
          <PieChart>
            <Pie
              data={chartData}
              cx="50%"
              cy="50%"
              innerRadius={55}
              outerRadius={80}
              paddingAngle={4}
              dataKey="value"
            >
              {chartData.map((entry, index) => (
                <Cell key={`cell-${index}`} fill={entry.color} stroke="#000000" strokeWidth={2} />
              ))}
            </Pie>
            <Tooltip
              formatter={(value: number, name: string) => [
                `${value} transactions`,
                `Risk Tier: ${name}`,
              ]}
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
            <Legend
              verticalAlign="bottom"
              height={36}
              iconType="circle"
              formatter={(value: string) => (
                <span className="text-xs font-bold text-white mr-2">{value}</span>
              )}
            />
          </PieChart>
        </ResponsiveContainer>
      </div>
    </DashboardWidget>
  );
};
