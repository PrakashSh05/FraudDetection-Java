import { useNavigate } from 'react-router-dom';
import { FraudCaseSummary } from '../types';
import { DataTable, Column } from '../../../components/ui/DataTable';
import { Badge } from '../../../components/ui/Badge';
import { Button } from '../../../components/ui/Button';
import { CASE_STATUS_COLORS, PRIORITY_BADGE_VARIANTS } from '../../../lib/chartColors';
import { Eye, Zap } from 'lucide-react';

interface CasesTableProps {
  cases: FraudCaseSummary[];
  sortParam?: string;
  onSortChange: (sortField: string) => void;
}

export const CasesTable = ({ cases, sortParam, onSortChange }: CasesTableProps) => {
  const navigate = useNavigate();

  const handleViewCase = (caseId: number) => {
    navigate(`/cases/${caseId}`);
  };

  const columns: Column<FraudCaseSummary>[] = [
    {
      header: 'Case ID',
      render: (row) => (
        <div className="flex items-center gap-1.5">
          <span className="font-mono font-bold text-[#E94F37]">#{row.caseId}</span>
          {(row.caseId > 11 || row.transactionId > 50) && (
            <span className="px-1.5 py-0.5 rounded text-[9px] font-extrabold bg-orange-500/20 text-orange-400 border border-orange-500/40 flex items-center gap-1">
              <Zap className="w-2.5 h-2.5 text-orange-400" /> SIMULATION
            </span>
          )}
        </div>
      ),
    },
    {
      header: 'Txn ID',
      render: (row) => <span className="font-mono text-slate-300">#{row.transactionId}</span>,
    },
    {
      header: 'Amount',
      render: (row) => <span className="font-bold text-white">${row.amount?.toLocaleString() ?? 0}</span>,
    },
    {
      header: 'Priority',
      sortableKey: 'priority',
      render: (row) => (
        <Badge variant={PRIORITY_BADGE_VARIANTS[row.priority] || 'neutral'}>
          {row.priority}
        </Badge>
      ),
    },
    {
      header: 'Status',
      sortableKey: 'status',
      render: (row) => (
        <Badge variant={CASE_STATUS_COLORS[row.status] || 'neutral'}>
          {row.status}
        </Badge>
      ),
    },
    {
      header: 'Risk Score',
      render: (row) => <span className="font-extrabold text-orange-400">{row.riskScore} / 100</span>,
    },
    {
      header: 'Assigned Analyst',
      render: (row) => (
        <span className="text-slate-300 font-medium">
          {row.assignedTo ? row.assignedTo : <span className="text-slate-500 italic">Unassigned</span>}
        </span>
      ),
    },
    {
      header: 'Opened At',
      sortableKey: 'openedAt',
      render: (row) => (
        <span className="text-slate-400 text-xs">
          {new Date(row.openedAt).toLocaleString(undefined, {
            month: 'short',
            day: 'numeric',
            hour: '2-digit',
            minute: '2-digit',
          })}
        </span>
      ),
    },
    {
      header: 'Actions',
      align: 'right',
      render: (row) => (
        <Button
          size="sm"
          variant="outline"
          onClick={(e) => {
            e.stopPropagation();
            handleViewCase(row.caseId);
          }}
          className="gap-1.5"
        >
          <Eye className="w-3.5 h-3.5" /> View
        </Button>
      ),
    },
  ];

  const mobileRender = (item: FraudCaseSummary) => (
    <div className="bg-neutral-950 p-4 rounded-xl border border-neutral-800 shadow-md space-y-3">
      <div className="flex items-center justify-between border-b border-neutral-800 pb-2">
        <div className="flex items-center gap-2">
          <span className="font-mono font-bold text-[#E94F37] text-sm">Case #{item.caseId}</span>
          {(item.caseId > 11 || item.transactionId > 50) && (
            <span className="px-1.5 py-0.5 rounded text-[9px] font-bold bg-orange-500/20 text-orange-400 border border-orange-500">
              SIMULATION
            </span>
          )}
        </div>
        <Badge variant={CASE_STATUS_COLORS[item.status] || 'neutral'}>{item.status}</Badge>
      </div>
      <div className="grid grid-cols-2 gap-2 text-xs">
        <div>
          <span className="text-slate-500 block">Transaction</span>
          <span className="font-mono font-bold text-white">#{item.transactionId}</span>
        </div>
        <div>
          <span className="text-slate-500 block">Amount</span>
          <span className="font-bold text-white">${item.amount?.toLocaleString() ?? 0}</span>
        </div>
        <div>
          <span className="text-slate-500 block">Priority</span>
          <Badge variant={PRIORITY_BADGE_VARIANTS[item.priority] || 'neutral'}>{item.priority}</Badge>
        </div>
        <div>
          <span className="text-slate-500 block">Assigned Analyst</span>
          <span className="font-medium text-slate-300">{item.assignedTo || 'Unassigned'}</span>
        </div>
      </div>
      <div className="pt-2 border-t border-neutral-800 flex items-center justify-between">
        <span className="text-[10px] text-slate-500">{new Date(item.openedAt).toLocaleDateString()}</span>
        <Button size="sm" variant="outline" onClick={() => handleViewCase(item.caseId)}>
          View Case
        </Button>
      </div>
    </div>
  );

  return (
    <DataTable
      columns={columns}
      data={cases}
      keyExtractor={(row) => row.caseId}
      sortParam={sortParam}
      onSortChange={onSortChange}
      mobileRender={mobileRender}
    />
  );
};
