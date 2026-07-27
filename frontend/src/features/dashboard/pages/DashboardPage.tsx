import { PageHeader } from '../../../components/ui/PageHeader';
import { KpiCardsSection } from '../components/KpiCardsSection';
import { RiskDistributionWidget } from '../components/RiskDistributionWidget';
import { DailyTrendWidget } from '../components/DailyTrendWidget';
import { TopRulesWidget } from '../components/TopRulesWidget';
import { QueueSummaryWidget } from '../components/QueueSummaryWidget';
import { Card } from '../../../components/ui/Card';
import { Badge } from '../../../components/ui/Badge';

export const DashboardPage = () => {
  return (
    <div className="space-y-6">
      <PageHeader
        title="Executive Risk Dashboard"
        subtitle="Real-time transaction risk scoring telemetry, engine metrics, and analyst case operations"
        action={
          <div className="flex items-center gap-2">
            <Badge variant="success">Engine v2.4 Online</Badge>
          </div>
        }
      />

      {/* Top KPI Cards Section */}
      <KpiCardsSection />

      {/* Charts Grid Row 1 */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <RiskDistributionWidget />
        <DailyTrendWidget />
      </div>

      {/* Charts & Queue Summary Grid Row 2 */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <TopRulesWidget />
        <QueueSummaryWidget />
      </div>

      {/* System Status Banner */}
      <Card title="System Telemetry Status">
        <div className="flex items-center justify-between text-xs text-gray-500">
          <span>Rule Engine Architecture: Strategy Pattern (Open/Closed)</span>
          <span>Risk Capping: 100 Max</span>
          <span>Database Telemetry: PostgreSQL / MySQL Relational Persistence</span>
        </div>
      </Card>
    </div>
  );
};

export default DashboardPage;
