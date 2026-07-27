import { ReactNode } from 'react';
import { useNavigate } from 'react-router-dom';
import { Card } from '../../../components/ui/Card';
import { Badge } from '../../../components/ui/Badge';
import { Button } from '../../../components/ui/Button';
import { ShieldCheck, ShieldAlert, AlertTriangle, ExternalLink } from 'lucide-react';

interface DecisionCardProps {
  decision: string;
  transactionId: number;
}

export const DecisionCard = ({ decision, transactionId }: DecisionCardProps) => {
  const navigate = useNavigate();

  const decisionConfigs: Record<
    string,
    { variant: 'success' | 'warning' | 'danger' | 'info'; icon: ReactNode; explanation: string }
  > = {
    APPROVED: {
      variant: 'success',
      icon: <ShieldCheck className="w-6 h-6 text-emerald-400" />,
      explanation: 'Transaction passed all engine risk evaluations cleanly and was automatically cleared.',
    },
    MONITOR: {
      variant: 'warning',
      icon: <AlertTriangle className="w-6 h-6 text-amber-400" />,
      explanation: 'Transaction cleared balance deduction but flagged for passive telemetry monitoring.',
    },
    REVIEW: {
      variant: 'warning',
      icon: <ShieldAlert className="w-6 h-6 text-amber-400" />,
      explanation: 'Transaction flagged for manual compliance analyst review. A Fraud Case has been opened.',
    },
    REJECTED: {
      variant: 'danger',
      icon: <ShieldAlert className="w-6 h-6 text-rose-400" />,
      explanation: 'Transaction blocked immediately due to high-risk rule violations. Balance preserved.',
    },
  };

  const config = decisionConfigs[decision] || {
    variant: 'info' as const,
    icon: <ShieldCheck className="w-6 h-6 text-blue-400" />,
    explanation: 'Transaction evaluated by decision engine.',
  };

  const isReview = decision === 'REVIEW';

  return (
    <Card title="Final Fraud Engine Decision">
      <div className="p-5 bg-slate-950/80 rounded-xl border border-slate-800 flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div className="flex items-center gap-4">
          <div className="p-3 bg-slate-900 rounded-xl border border-slate-700/80">{config.icon}</div>
          <div>
            <div className="flex items-center gap-2">
              <span className="text-sm font-bold text-slate-200">Engine Decision:</span>
              <Badge variant={config.variant}>{decision}</Badge>
            </div>
            <p className="text-xs text-slate-400 mt-1 max-w-lg">{config.explanation}</p>
          </div>
        </div>

        {isReview && (
          <Button
            variant="primary"
            size="sm"
            onClick={() => navigate(`/cases/${transactionId}`)}
            className="gap-1.5 self-start sm:self-center"
          >
            Open Fraud Case <ExternalLink className="w-3.5 h-3.5" />
          </Button>
        )}
      </div>
    </Card>
  );
};
