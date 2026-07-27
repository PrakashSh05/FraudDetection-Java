import { useTopRules } from '../api/useDashboardData';
import { DashboardWidget } from '../../../components/ui/DashboardWidget';
import { SkeletonChart } from './SkeletonLoaders';
import { BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer, Cell } from 'recharts';
import { CHART_COLORS } from '../../../lib/chartColors';

interface TooltipPayload {
  payload: {
    percentage?: number;
    ruleId?: string;
  };
}

export const TopRulesWidget = () => {
  const { data, isLoading, isError, refetch } = useTopRules();

  return (
    <DashboardWidget
      title="Top Triggered Fraud Rules"
      subtitle="Most frequently fired rules across transactions"
      isLoading={isLoading}
      isError={isError}
      isEmpty={!data || data.length === 0}
      errorMessage="Failed to load top triggered rules"
      emptyMessage="No rules triggered in evaluation period"
      onRetry={() => refetch()}
      skeleton={<SkeletonChart />}
    >
      <div className="h-64">
        <ResponsiveContainer width="100%" height="100%">
          <BarChart
            layout="vertical"
            data={data || []}
            margin={{ top: 10, right: 20, left: 40, bottom: 0 }}
          >
            <CartesianGrid strokeDasharray="3 3" horizontal={false} stroke="#F3F4F6" />
            <XAxis type="number" tick={{ fontSize: 11, fill: '#6C757D' }} axisLine={false} />
            <YAxis
              dataKey="ruleName"
              type="category"
              tick={{ fontSize: 11, fill: '#393E41', fontWeight: 500 }}
              axisLine={false}
              width={120}
            />
            <Tooltip
              formatter={(value: number, _name: string, props: unknown) => {
                const item = props as TooltipPayload;
                return [
                  `${value} triggers (${item.payload?.percentage ?? 0}%)`,
                  `Rule: ${item.payload?.ruleId ?? 'N/A'}`,
                ];
              }}
              contentStyle={{ borderRadius: '8px', border: '1px solid #E5E7EB', fontSize: '12px' }}
            />
            <Bar dataKey="triggerCount" radius={[0, 4, 4, 0]}>
              {(data || []).map((_, index) => (
                <Cell key={`cell-${index}`} fill={index === 0 ? CHART_COLORS.primary : CHART_COLORS.dark} />
              ))}
            </Bar>
          </BarChart>
        </ResponsiveContainer>
      </div>
    </DashboardWidget>
  );
};
