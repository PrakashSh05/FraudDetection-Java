import { RiskEvaluationDetails } from '../types';
import { Card } from '../../../components/ui/Card';
import { Badge } from '../../../components/ui/Badge';
import { Gauge, Clock, ShieldCheck } from 'lucide-react';
import { PRIORITY_BADGE_VARIANTS } from '../../../lib/chartColors';

interface RiskAssessmentCardProps {
  evaluation: RiskEvaluationDetails;
}

export const RiskAssessmentCard = ({ evaluation }: RiskAssessmentCardProps) => {
  return (
    <Card title="Risk Scoring Assessment" subtitle="Automated Strategy Pattern rule engine telemetry">
      <div className="grid grid-cols-1 sm:grid-cols-2 gap-4 text-xs">
        <div className="p-3.5 bg-neutral-950 rounded-xl border border-neutral-800 flex items-center justify-between">
          <div>
            <span className="text-neutral-400 block text-[10px] uppercase font-bold">Total Risk Score</span>
            <span className="text-xl font-extrabold text-[#E94F37] flex items-center gap-1.5 mt-0.5">
              <Gauge className="w-5 h-5" /> {evaluation.riskScore} / 100
            </span>
          </div>
          <Badge variant={evaluation.riskScore > 50 ? 'danger' : evaluation.riskScore > 20 ? 'warning' : 'success'}>
            Capped Score
          </Badge>
        </div>

        <div className="p-3.5 bg-neutral-950 rounded-xl border border-neutral-800 flex items-center justify-between">
          <div>
            <span className="text-neutral-400 block text-[10px] uppercase font-bold">Risk Level Tier</span>
            <span className="text-sm font-bold text-white mt-0.5 block">{evaluation.riskLevel}</span>
          </div>
          <Badge variant={PRIORITY_BADGE_VARIANTS[evaluation.riskLevel] || 'neutral'}>
            {evaluation.riskLevel}
          </Badge>
        </div>

        <div className="p-3.5 bg-neutral-950 rounded-xl border border-neutral-800 flex items-center gap-2.5">
          <Clock className="w-4 h-4 text-orange-400" />
          <div>
            <span className="text-neutral-400 block text-[10px] uppercase font-bold">Evaluation Latency</span>
            <span className="font-bold text-emerald-400">{evaluation.processingTimeMs} ms</span>
          </div>
        </div>

        <div className="p-3.5 bg-neutral-950 rounded-xl border border-neutral-800 flex items-center gap-2.5">
          <ShieldCheck className="w-4 h-4 text-emerald-400" />
          <div>
            <span className="text-neutral-400 block text-[10px] uppercase font-bold">Timestamp</span>
            <span className="font-bold text-neutral-200">
              {new Date(evaluation.evaluationTimestamp).toLocaleTimeString()}
            </span>
          </div>
        </div>
      </div>
    </Card>
  );
};
