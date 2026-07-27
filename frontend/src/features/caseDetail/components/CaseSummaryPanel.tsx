import { useNavigate } from 'react-router-dom';
import { FraudCaseDetailResponse } from '../types';
import { DetailSection } from '../../../components/ui/DetailSection';
import { KeyValueGrid } from '../../../components/ui/KeyValueGrid';
import { Badge } from '../../../components/ui/Badge';
import { STATUS_THEME_MAP, PRIORITY_THEME_MAP } from '../../../lib/statusTheme';
import { formatDate } from '../../../utils/date';
import { ExternalLink } from 'lucide-react';

interface CaseSummaryPanelProps {
  caseDetail: FraudCaseDetailResponse;
}

export const CaseSummaryPanel = ({ caseDetail }: CaseSummaryPanelProps) => {
  const navigate = useNavigate();
  const statusConfig = STATUS_THEME_MAP[caseDetail.status] || { variant: 'neutral', label: caseDetail.status };
  const priorityConfig = PRIORITY_THEME_MAP[caseDetail.priority] || { variant: 'neutral', label: caseDetail.priority };

  const summaryItems = [
    { label: 'Case ID', value: <span className="font-mono font-bold text-[#E94F37]">#{caseDetail.caseId}</span> },
    {
      label: 'Linked Transaction',
      value: (
        <button
          onClick={() => navigate(`/investigation/${caseDetail.transaction.transactionId}`)}
          className="font-mono text-[#E94F37] hover:underline flex items-center gap-1 font-bold"
        >
          #{caseDetail.transaction.transactionId} <ExternalLink className="w-3 h-3" />
        </button>
      ),
    },
    { label: 'Status', value: <Badge variant={statusConfig.variant}>{statusConfig.label}</Badge> },
    { label: 'Priority', value: <Badge variant={priorityConfig.variant}>{priorityConfig.label}</Badge> },
    { label: 'Assigned Analyst', value: caseDetail.assignedTo || <span className="text-neutral-500 italic">Unassigned</span> },
    { label: 'Opened At', value: formatDate(caseDetail.openedAt) },
    { label: 'Closed At', value: caseDetail.closedAt ? formatDate(caseDetail.closedAt) : <span className="text-neutral-500 italic">Active</span> },
    { label: 'User / Customer ID', value: `User #${caseDetail.transaction.userId}` },
    { label: 'Transaction Amount', value: `$${caseDetail.transaction.amount?.toLocaleString() ?? 0}` },
    { label: 'Risk Score / Tier', value: `${caseDetail.evaluation.riskScore} / 100 (${caseDetail.evaluation.riskLevel})` },
    { label: 'Resolution Summary', value: caseDetail.resolution || <span className="text-neutral-500 italic">Unresolved</span>, fullWidth: true },
    { label: 'Analyst Review Notes', value: caseDetail.reviewNotes || <span className="text-neutral-500 italic">No notes recorded</span>, fullWidth: true },
  ];

  return (
    <DetailSection
      title={`Case #${caseDetail.caseId} Operational Overview`}
      subtitle="Complete business summary and compliance telemetry"
      action={<Badge variant={statusConfig.variant}>{statusConfig.label}</Badge>}
    >
      <KeyValueGrid items={summaryItems} cols={2} />
    </DetailSection>
  );
};
