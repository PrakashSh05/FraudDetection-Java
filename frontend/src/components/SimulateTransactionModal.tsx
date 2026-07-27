import React, { useState } from 'react';
import { X, Zap, ShieldAlert, CheckCircle, AlertTriangle, XCircle, ArrowRight } from 'lucide-react';
import { useQueryClient } from '@tanstack/react-query';
import { apiClient } from '../lib/api';

interface SimulateTransactionModalProps {
  isOpen: boolean;
  onClose: () => void;
}

interface TriggeredRule {
  ruleId: string;
  ruleName: string;
  category: string;
  severity: string;
  points: number;
  description: string;
}

interface SimulationResponse {
  transactionId: number;
  userId: number;
  amount: number;
  transactionType: string;
  status: string;
  riskScore: number;
  riskLevel: string;
  decision: string;
  processingTimeMs: number;
  triggeredRules: TriggeredRule[];
  createdAt: string;
}

export const SimulateTransactionModal: React.FC<SimulateTransactionModalProps> = ({ isOpen, onClose }) => {
  const queryClient = useQueryClient();

  const [userId, setUserId] = useState<number>(1);
  const [amount, setAmount] = useState<string>('75000.00');
  const [transactionType, setTransactionType] = useState<string>('DEBIT');

  const [isSubmitting, setIsSubmitting] = useState<boolean>(false);
  const [result, setResult] = useState<SimulationResponse | null>(null);
  const [errorMsg, setErrorMsg] = useState<string | null>(null);

  if (!isOpen) return null;

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setIsSubmitting(true);
    setErrorMsg(null);
    setResult(null);

    try {
      const payload = {
        userId: Number(userId),
        amount: parseFloat(amount),
        transactionType,
      };

      // 1. Submit transaction to backend
      const res: any = await apiClient.post('/transactions', payload);
      const txnData = res.data || res;
      const txnId = txnData?.id || txnData?.transactionId;

      if (!txnId) {
        throw new Error('Transaction created but ID missing from response');
      }

      // 2. Fetch full investigation telemetry for the created transaction
      const invRes: any = await apiClient.get(`/investigation/transaction/${txnId}`);
      const invData = invRes.data || invRes;

      setResult({
        transactionId: invData.transaction.transactionId,
        userId: invData.transaction.userId,
        amount: invData.transaction.amount,
        transactionType: invData.transaction.transactionType,
        status: invData.transaction.status,
        riskScore: invData.evaluation.riskScore,
        riskLevel: invData.evaluation.riskLevel,
        decision: invData.evaluation.decision,
        processingTimeMs: invData.evaluation.processingTimeMs,
        triggeredRules: invData.triggeredRules || [],
        createdAt: invData.transaction.createdAt,
      });

      // 3. Invalidate React Query caches so Cases Queue & Dashboard update in real-time
      queryClient.invalidateQueries({ queryKey: ['analytics'] });
      queryClient.invalidateQueries({ queryKey: ['cases'] });
    } catch (err: any) {
      const msg = err.response?.data?.message || err.message || 'Failed to process transaction';
      setErrorMsg(msg);
    } finally {
      setIsSubmitting(false);
    }
  };

  const resetForm = () => {
    setResult(null);
    setErrorMsg(null);
  };

  const getDecisionBadge = (decision: string) => {
    switch (decision) {
      case 'APPROVED':
        return (
          <span className="inline-flex items-center gap-1.5 px-3 py-1 rounded-full text-xs font-extrabold bg-emerald-500/20 text-emerald-400 border border-emerald-500/40 shadow-lg shadow-emerald-500/10">
            <CheckCircle className="w-3.5 h-3.5" /> APPROVED
          </span>
        );
      case 'MONITOR':
        return (
          <span className="inline-flex items-center gap-1.5 px-3 py-1 rounded-full text-xs font-extrabold bg-amber-500/20 text-amber-400 border border-amber-500/40">
            <Zap className="w-3.5 h-3.5" /> MONITOR (Case Created)
          </span>
        );
      case 'REVIEW':
        return (
          <span className="inline-flex items-center gap-1.5 px-3 py-1 rounded-full text-xs font-extrabold bg-[#E94F37]/20 text-[#E94F37] border border-[#E94F37]">
            <AlertTriangle className="w-3.5 h-3.5" /> REVIEW (Case Created)
          </span>
        );
      case 'REJECTED':
      default:
        return (
          <span className="inline-flex items-center gap-1.5 px-3 py-1 rounded-full text-xs font-extrabold bg-rose-500/20 text-rose-400 border border-rose-500">
            <XCircle className="w-3.5 h-3.5" /> REJECTED (Case Created)
          </span>
        );
    }
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/80 backdrop-blur-md">
      <div className="w-full max-w-xl bg-black border border-neutral-800 rounded-2xl shadow-2xl overflow-hidden">
        {/* Modal Header */}
        <div className="px-6 py-4 bg-neutral-950 border-b border-neutral-800 flex items-center justify-between">
          <div className="flex items-center gap-2.5">
            <div className="w-8 h-8 rounded-lg bg-[#E94F37]/20 border border-[#E94F37] flex items-center justify-center text-[#E94F37]">
              <Zap className="w-4 h-4" />
            </div>
            <div>
              <h3 className="text-sm font-bold text-white">Live Transaction Risk Simulator</h3>
              <p className="text-[11px] text-neutral-400">Trigger real-time Spring Boot fraud rule evaluation</p>
            </div>
          </div>
          <button
            onClick={onClose}
            className="p-1 rounded-lg text-neutral-400 hover:text-white hover:bg-neutral-900 transition-colors"
          >
            <X className="w-5 h-5" />
          </button>
        </div>

        {/* Modal Body */}
        <div className="p-6 space-y-5">
          {!result ? (
            <form onSubmit={handleSubmit} className="space-y-4">
              <div className="grid grid-cols-2 gap-4">
                <div>
                  <label className="block text-xs font-bold text-slate-300 mb-1.5">User Account</label>
                  <select
                    value={userId}
                    onChange={(e) => setUserId(Number(e.target.value))}
                    className="w-full px-3.5 py-2 bg-neutral-950 border border-neutral-800 rounded-xl text-sm text-white focus:outline-none focus:border-[#E94F37]"
                  >
                    <option value={1}>User #1 — Rahul Sharma (Balance: $250k)</option>
                    <option value={2}>User #2 — Priya Patel (Balance: $180k)</option>
                    <option value={3}>User #3 — Amit Kumar (Balance: $95k)</option>
                    <option value={4}>User #4 — Sneha Reddy (Balance: $320k)</option>
                    <option value={5}>User #5 — Vikram Singh (Balance: $140k)</option>
                  </select>
                </div>

                <div>
                  <label className="block text-xs font-bold text-slate-300 mb-1.5">Transaction Type</label>
                  <select
                    value={transactionType}
                    onChange={(e) => setTransactionType(e.target.value)}
                    className="w-full px-3.5 py-2 bg-neutral-950 border border-neutral-800 rounded-xl text-sm text-white focus:outline-none focus:border-[#E94F37]"
                  >
                    <option value="DEBIT">DEBIT</option>
                    <option value="CREDIT">CREDIT</option>
                  </select>
                </div>
              </div>

              <div>
                <label className="block text-xs font-bold text-slate-300 mb-1.5">Transaction Amount ($)</label>
                <input
                  type="number"
                  step="0.01"
                  value={amount}
                  onChange={(e) => setAmount(e.target.value)}
                  required
                  placeholder="e.g. 75000.00"
                  className="w-full px-3.5 py-2 bg-neutral-950 border border-neutral-800 rounded-xl text-sm text-white focus:outline-none focus:border-[#E94F37]"
                />
                <div className="mt-2 flex gap-2">
                  <button
                    type="button"
                    onClick={() => setAmount('1500.00')}
                    className="px-2.5 py-1 bg-neutral-900 hover:bg-neutral-800 text-slate-300 text-xs rounded-lg border border-neutral-800"
                  >
                    Clean ($1,500)
                  </button>
                  <button
                    type="button"
                    onClick={() => setAmount('75000.00')}
                    className="px-2.5 py-1 bg-orange-500/10 hover:bg-orange-500/20 text-orange-400 text-xs rounded-lg border border-orange-500/30"
                  >
                    High Amount ($75k)
                  </button>
                  <button
                    type="button"
                    onClick={() => setAmount('150000.00')}
                    className="px-2.5 py-1 bg-orange-500/20 hover:bg-orange-500/30 text-orange-300 text-xs rounded-lg border border-orange-500"
                  >
                    Critical ($150k)
                  </button>
                </div>
              </div>

              {errorMsg && (
                <div className="p-3 bg-neutral-950 border border-[#E94F37] rounded-xl text-xs text-orange-400 flex items-center gap-2">
                  <ShieldAlert className="w-4 h-4 text-[#E94F37] shrink-0" />
                  <span>{errorMsg}</span>
                </div>
              )}

              <div className="pt-2 flex justify-end gap-3">
                <button
                  type="button"
                  onClick={onClose}
                  className="px-4 py-2 bg-neutral-900 hover:bg-neutral-800 text-slate-300 text-xs font-bold rounded-xl transition-colors border border-neutral-800"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  disabled={isSubmitting}
                  className="px-5 py-2 bg-[#E94F37] hover:bg-[#D03E27] text-white text-xs font-bold rounded-xl shadow-lg shadow-[#E94F37]/30 transition-all flex items-center gap-2 disabled:opacity-50"
                >
                  {isSubmitting ? 'Evaluating Rules...' : 'Execute Evaluation'}
                  <ArrowRight className="w-3.5 h-3.5" />
                </button>
              </div>
            </form>
          ) : (
            /* Simulation Output Results */
            <div className="space-y-4">
              <div className="p-4 rounded-xl bg-neutral-950 border border-neutral-800 space-y-3">
                <div className="flex items-center justify-between">
                  <div className="flex items-center gap-2">
                    <span className="text-xs text-neutral-400 font-mono">Txn #{result.transactionId}</span>
                    <span className="px-2 py-0.5 rounded text-[10px] font-extrabold bg-orange-500/20 text-orange-400 border border-orange-500/40">
                      SIMULATION
                    </span>
                  </div>
                  {getDecisionBadge(result.decision)}
                </div>

                <div className="grid grid-cols-3 gap-3 pt-1 border-t border-neutral-800 text-center">
                  <div>
                    <p className="text-[10px] text-neutral-400 uppercase font-bold">Amount</p>
                    <p className="text-sm font-bold text-white">${result.amount.toLocaleString()}</p>
                  </div>
                  <div>
                    <p className="text-[10px] text-neutral-400 uppercase font-bold">Risk Score</p>
                    <p className={result.decision === 'APPROVED' ? 'text-sm font-bold text-emerald-400' : 'text-sm font-bold text-orange-400'}>
                      {result.riskScore} / 100
                    </p>
                  </div>
                  <div>
                    <p className="text-[10px] text-neutral-400 uppercase font-bold">Latency</p>
                    <p className="text-sm font-bold text-emerald-400">{result.processingTimeMs} ms</p>
                  </div>
                </div>
              </div>

              {/* Triggered Rules Section */}
              <div className="space-y-2">
                <p className="text-xs font-bold text-slate-300">
                  Triggered Fraud Rules ({result.triggeredRules?.length || 0})
                </p>

                {!result.triggeredRules || result.triggeredRules.length === 0 ? (
                  <div className="p-3 bg-emerald-500/10 border border-emerald-500/30 rounded-xl text-xs text-emerald-400 font-bold flex items-center gap-2">
                    <CheckCircle className="w-4 h-4 text-emerald-400" />
                    <span>No fraud indicators detected. Clean transaction automatically approved.</span>
                  </div>
                ) : (
                  <div className="space-y-2 max-h-40 overflow-y-auto">
                    {result.triggeredRules.map((rule, idx) => (
                      <div key={idx} className="p-3 bg-neutral-950 border border-orange-500/40 rounded-xl text-xs space-y-1">
                        <div className="flex items-center justify-between">
                          <span className="font-mono font-bold text-orange-400">{rule.ruleId} — {rule.ruleName}</span>
                          <span className="px-2 py-0.5 rounded bg-orange-500/20 text-orange-300 font-extrabold text-[10px]">
                            +{rule.points} pts
                          </span>
                        </div>
                        <p className="text-slate-300 text-[11px]">{rule.description}</p>
                      </div>
                    ))}
                  </div>
                )}
              </div>

              <div className="pt-2 flex justify-between items-center">
                <button
                  onClick={resetForm}
                  className="px-4 py-2 bg-neutral-900 hover:bg-neutral-800 text-slate-300 text-xs font-bold rounded-xl transition-colors border border-neutral-800"
                >
                  Test Another
                </button>
                <button
                  onClick={onClose}
                  className="px-5 py-2 bg-[#E94F37] hover:bg-[#D03E27] text-white text-xs font-bold rounded-xl shadow-lg transition-colors"
                >
                  Done & View Dashboard
                </button>
              </div>
            </div>
          )}
        </div>
      </div>
    </div>
  );
};
