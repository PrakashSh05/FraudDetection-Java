import { useNavigate } from 'react-router-dom';
import { FraudCaseSummary } from '../types';
import { DataTable, Column } from '../../../components/ui/DataTable';
import { Badge } from '../../../components/ui/Badge';
import { Button } from '../../../components/ui/Button';
import { CASE_STATUS_COLORS, PRIORITY_BADGE_VARIANTS } from '../../../lib/chartColors';
import { Eye } from 'lucide-react';

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
      render: (row) => <span className="font-mono font-bold text-[#E94F37]">#{row.caseId}</span>,
    },
    {
      header: 'Txn ID',
      render: (row) => <span className="font-mono text-gray-600">#{row.transactionId}</span>,
    },
    {
      header: 'Amount',
      render: (row) => <span className="font-medium">₹{row.amount?.toLocaleString() ?? 0}</span>,
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
      render: (row) => <span className="font-semibold">{row.riskScore} / 100</span>,
    },
    {
      header: 'Assigned Analyst',
      render: (row) => (
        <span className="text-gray-500 font-medium">
          {row.assignedTo ? row.assignedTo : <span className="text-gray-300 italic">Unassigned</span>}
        </span>
      ),
    },
    {
      header: 'Opened At',
      sortableKey: 'openedAt',
      render: (row) => (
        <span className="text-gray-500">
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
    <div className="bg-white p-4 rounded-xl border border-gray-100 shadow-sm space-y-3">
      <div className="flex items-center justify-between border-b border-gray-100 pb-2">
        <span className="font-mono font-bold text-[#E94F37] text-sm">Case #{item.caseId}</span>
        <Badge variant={CASE_STATUS_COLORS[item.status] || 'neutral'}>{item.status}</Badge>
      </div>
      <div className="grid grid-cols-2 gap-2 text-xs">
        <div>
          <span className="text-gray-400 block">Transaction</span>
          <span className="font-mono font-semibold">#{item.transactionId}</span>
        </div>
        <div>
          <span className="text-gray-400 block">Amount</span>
          <span className="font-bold">₹{item.amount?.toLocaleString() ?? 0}</span>
        </div>
        <div>
          <span className="text-gray-400 block">Priority</span>
          <Badge variant={PRIORITY_BADGE_VARIANTS[item.priority] || 'neutral'}>{item.priority}</Badge>
        </div>
        <div>
          <span className="text-gray-400 block">Assigned Analyst</span>
          <span className="font-medium text-gray-700">{item.assignedTo || 'Unassigned'}</span>
        </div>
      </div>
      <div className="pt-2 border-t border-gray-100 flex items-center justify-between">
        <span className="text-[10px] text-gray-400">{new Date(item.openedAt).toLocaleDateString()}</span>
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
