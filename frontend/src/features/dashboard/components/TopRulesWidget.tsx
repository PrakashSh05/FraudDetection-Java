import { useTopRules } from '../api/useDashboardData';
import { DashboardWidget } from '../../../components/ui/DashboardWidget';
import { SkeletonChart } from './SkeletonLoaders';
import { BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer, Cell } from 'recharts';

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
            <CartesianGrid strokeDasharray="3 3" horizontal={false} stroke="#262626" />
            <XAxis type="number" tick={{ fontSize: 11, fill: '#A1A1AA' }} axisLine={false} />
            <YAxis
              dataKey="ruleName"
              type="category"
              tick={{ fontSize: 11, fill: '#FFFFFF', fontWeight: 600 }}
              axisLine={false}
              width={130}
            />
            <Tooltip
              formatter={(value: number, _name: string, props: unknown) => {
                const item = props as TooltipPayload;
                return [
                  `${value} triggers`,
                  `Rule: ${item.payload?.ruleId ?? 'N/A'}`,
                ];
              }}
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
            <Bar dataKey="triggerCount" radius={[0, 8, 8, 0]}>
              {(data || []).map((_, index) => (
                <Cell key={`cell-${index}`} fill={index === 0 ? '#E94F37' : '#F97316'} />
              ))}
            </Bar>
          </BarChart>
        </ResponsiveContainer>
      </div>
    </DashboardWidget>
  );
};
