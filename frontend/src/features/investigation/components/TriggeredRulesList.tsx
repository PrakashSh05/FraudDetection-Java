import { TriggeredRuleDetails } from '../types';
import { Card } from '../../../components/ui/Card';
import { Badge } from '../../../components/ui/Badge';
import { AlertOctagon, CheckCircle2 } from 'lucide-react';
import { PRIORITY_BADGE_VARIANTS } from '../../../lib/chartColors';

interface TriggeredRulesListProps {
  rules: TriggeredRuleDetails[];
}

export const TriggeredRulesList = ({ rules }: TriggeredRulesListProps) => {
  return (
    <Card
      title={`Triggered Fraud Rules (${rules.length})`}
      subtitle="Specific rule indicators fired during automated evaluation"
    >
      {rules.length === 0 ? (
        <div className="p-8 text-center bg-emerald-500/10 rounded-xl border border-emerald-500/20 flex flex-col items-center">
          <CheckCircle2 className="w-8 h-8 text-emerald-400 mb-2" />
          <p className="text-xs font-bold text-emerald-300">No Fraud Indicators Triggered</p>
          <p className="text-[11px] text-slate-400 mt-1">Transaction cleared all registered engine rules cleanly.</p>
        </div>
      ) : (
        <div className="space-y-3">
          {rules.map((rule, idx) => (
            <div
              key={idx}
              className="p-4 bg-slate-950/80 rounded-xl border border-amber-500/30 flex flex-col sm:flex-row sm:items-center justify-between gap-4"
            >
              <div className="flex items-start gap-3">
                <div className="p-2 bg-amber-500/20 text-amber-400 rounded-lg mt-0.5 border border-amber-500/30">
                  <AlertOctagon className="w-4 h-4" />
                </div>
                <div>
                  <div className="flex items-center gap-2">
                    <span className="font-bold text-xs text-white">{rule.ruleName}</span>
                    <span className="font-mono text-[10px] text-amber-400">({rule.ruleId})</span>
                  </div>
                  <p className="text-xs text-slate-300 mt-1">{rule.description}</p>
                </div>
              </div>

              <div className="flex items-center gap-3 self-end sm:self-center">
                <span className="text-xs font-extrabold text-amber-400 bg-slate-900 px-3 py-1 rounded-lg border border-amber-500/40">
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
