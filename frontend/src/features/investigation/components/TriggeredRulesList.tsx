import React from 'react';
import { TriggeredRuleDetails } from '../types';
import { Card } from '../../../components/ui/Card';
import { Badge } from '../../../components/ui/Badge';
import { AlertOctagon, CheckCircle2 } from 'lucide-react';
import { PRIORITY_BADGE_VARIANTS } from '../../../lib/chartColors';

interface TriggeredRulesListProps {
  rules: TriggeredRuleDetails[];
}

export const TriggeredRulesList: React.FC<TriggeredRulesListProps> = ({ rules }) => {
  return (
    <Card
      title={`Triggered Fraud Rules (${rules.length})`}
      subtitle="Specific rule indicators fired during automated evaluation"
    >
      {rules.length === 0 ? (
        <div className="p-8 text-center bg-emerald-50/50 rounded-lg border border-emerald-100 flex flex-col items-center">
          <CheckCircle2 className="w-8 h-8 text-[#10B981] mb-2" />
          <p className="text-xs font-semibold text-[#10B981]">No Fraud Indicators Triggered</p>
          <p className="text-[11px] text-gray-500 mt-1">Transaction cleared all registered engine rules cleanly.</p>
        </div>
      ) : (
        <div className="space-y-3">
          {rules.map((rule, idx) => (
            <div
              key={idx}
              className="p-4 bg-gray-50 rounded-xl border border-gray-100 flex flex-col sm:flex-row sm:items-center justify-between gap-4"
            >
              <div className="flex items-start gap-3">
                <div className="p-2 bg-rose-100 text-[#E94F37] rounded-lg mt-0.5">
                  <AlertOctagon className="w-4 h-4" />
                </div>
                <div>
                  <div className="flex items-center gap-2">
                    <span className="font-bold text-xs text-[#393E41]">{rule.ruleName}</span>
                    <span className="font-mono text-[10px] text-gray-400">({rule.ruleId})</span>
                  </div>
                  <p className="text-xs text-gray-600 mt-1">{rule.description}</p>
                </div>
              </div>

              <div className="flex items-center gap-3 self-end sm:self-center">
                <span className="text-xs font-bold text-[#E94F37] bg-white px-2.5 py-1 rounded-md border border-gray-200 shadow-2xs">
                  +{rule.points} pts
                </span>
                <Badge variant={PRIORITY_BADGE_VARIANTS[rule.severity] || 'neutral'}>
                  {rule.severity}
                </Badge>
              </div>
            </div>
          ))}
        </div>
      )}
    </Card>
  );
};
