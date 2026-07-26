import React from 'react';
import { useNavigate } from 'react-router-dom';
import { Card } from '../../../components/ui/Card';
import { Badge } from '../../../components/ui/Badge';
import { Button } from '../../../components/ui/Button';
import { ShieldCheck, ShieldAlert, AlertTriangle, ExternalLink } from 'lucide-react';

interface DecisionCardProps {
  decision: string;
  transactionId: number;
}

export const DecisionCard: React.FC<DecisionCardProps> = ({ decision, transactionId }) => {
  const navigate = useNavigate();

  const decisionConfigs: Record<
    string,
    { variant: 'success' | 'warning' | 'danger' | 'info'; icon: React.ReactNode; explanation: string }
  > = {
    APPROVED: {
      variant: 'success',
      icon: <ShieldCheck className="w-6 h-6 text-[#10B981]" />,
      explanation: 'Transaction passed all engine risk evaluations cleanly and was automatically cleared.',
    },
    MONITOR: {
      variant: 'warning',
      icon: <AlertTriangle className="w-6 h-6 text-[#F59E0B]" />,
      explanation: 'Transaction cleared balance deduction but flagged for passive telemetry monitoring.',
    },
    REVIEW: {
      variant: 'warning',
      icon: <ShieldAlert className="w-6 h-6 text-[#F59E0B]" />,
      explanation: 'Transaction flagged for manual compliance analyst review. A Fraud Case has been opened.',
    },
    REJECTED: {
      variant: 'danger',
      icon: <ShieldAlert className="w-6 h-6 text-[#EF4444]" />,
      explanation: 'Transaction blocked immediately due to high-risk rule violations. Balance preserved.',
    },
  };

  const config = decisionConfigs[decision] || {
    variant: 'info' as const,
    icon: <ShieldCheck className="w-6 h-6 text-[#3B82F6]" />,
    explanation: 'Transaction evaluated by decision engine.',
  };

  const isReview = decision === 'REVIEW';

  return (
    <Card title="Final Fraud Engine Decision">
      <div className="p-5 bg-gray-50 rounded-xl border border-gray-100 flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div className="flex items-center gap-4">
          <div className="p-3 bg-white rounded-xl shadow-2xs border border-gray-100">{config.icon}</div>
          <div>
            <div className="flex items-center gap-2">
              <span className="text-sm font-bold text-[#393E41]">Engine Decision:</span>
              <Badge variant={config.variant}>{decision}</Badge>
            </div>
            <p className="text-xs text-gray-600 mt-1 max-w-lg">{config.explanation}</p>
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
