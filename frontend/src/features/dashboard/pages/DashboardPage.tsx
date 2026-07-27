import { PageHeader } from '../../../components/ui/PageHeader';
import { KpiCardsSection } from '../components/KpiCardsSection';
import { RiskDistributionWidget } from '../components/RiskDistributionWidget';
import { DailyTrendWidget } from '../components/DailyTrendWidget';
import { TopRulesWidget } from '../components/TopRulesWidget';
import { QueueSummaryWidget } from '../components/QueueSummaryWidget';
import { Card } from '../../../components/ui/Card';
import { Badge } from '../../../components/ui/Badge';
import { Cpu, Database, ShieldAlert } from 'lucide-react';

export const DashboardPage = () => {
  return (
    <div className="space-y-6">
      <PageHeader
        title="Executive Risk Command Center"
        subtitle="Real-time transaction risk scoring telemetry, engine metrics, and analyst case operations"
        action={
          <div className="flex items-center gap-2">
            <Badge variant="success">Engine v2.4 Active</Badge>
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
      <Card title="Engine Architecture & System Telemetry">
        <div className="grid grid-cols-1 md:grid-cols-3 gap-4 text-xs">
          <div className="p-3 bg-slate-950/80 rounded-xl border border-slate-800 flex items-center gap-3">
            <Cpu className="w-5 h-5 text-indigo-400" />
            <div>
              <span className="text-[10px] text-slate-500 uppercase font-bold block">Engine Architecture</span>
              <span className="font-bold text-slate-200">Strategy Pattern (Open/Closed)</span>
            </div>
          </div>
          <div className="p-3 bg-slate-950/80 rounded-xl border border-slate-800 flex items-center gap-3">
            <ShieldAlert className="w-5 h-5 text-amber-400" />
            <div>
              <span className="text-[10px] text-slate-500 uppercase font-bold block">Risk Score Index</span>
              <span className="font-bold text-slate-200">0 to 100 Point Capping</span>
            </div>
          </div>
          <div className="p-3 bg-slate-950/80 rounded-xl border border-slate-800 flex items-center gap-3">
            <Database className="w-5 h-5 text-emerald-400" />
            <div>
              <span className="text-[10px] text-slate-500 uppercase font-bold block">Database Telemetry</span>
              <span className="font-bold text-slate-200">RDBMS Persistence & Auditing</span>
            </div>
          </div>
        </div>
      </Card>
    </div>
  );
};

export default DashboardPage;
