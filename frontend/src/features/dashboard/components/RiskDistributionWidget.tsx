import { useRiskDistribution } from '../api/useDashboardData';
import { DashboardWidget } from '../../../components/ui/DashboardWidget';
import { SkeletonChart } from './SkeletonLoaders';
import { PieChart, Pie, Cell, Tooltip, ResponsiveContainer, Legend } from 'recharts';
import { RISK_LEVEL_COLORS } from '../../../lib/chartColors';

export const RiskDistributionWidget = () => {
  const { data, isLoading, isError, refetch } = useRiskDistribution();

  const chartData = data?.map((item) => ({
    name: item.riskLevel,
    value: item.count,
    percentage: item.percentage,
    color: RISK_LEVEL_COLORS[item.riskLevel] || '#6C757D',
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
                <Cell key={`cell-${index}`} fill={entry.color} />
              ))}
            </Pie>
            <Tooltip
              formatter={(value: number, name: string) => [
                `${value} transactions`,
                `Risk Tier: ${name}`,
              ]}
              contentStyle={{ borderRadius: '8px', border: '1px solid #E5E7EB', fontSize: '12px' }}
            />
            <Legend
              verticalAlign="bottom"
              height={36}
              iconType="circle"
              formatter={(value: string) => (
                <span className="text-xs font-medium text-[#393E41]">{value}</span>
              )}
            />
          </PieChart>
        </ResponsiveContainer>
      </div>
    </DashboardWidget>
  );
};
