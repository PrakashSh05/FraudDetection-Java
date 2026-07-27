import { useCaseTimeline } from '../api/useCaseDetailData';
import { DetailSection } from '../../../components/ui/DetailSection';
import { formatDate, formatRelativeTime } from '../../../utils/date';
import { History, UserCheck, RefreshCw, FileText, CheckCircle2, Shield } from 'lucide-react';

interface AuditTimelinePanelProps {
  caseId: string;
}

export const AuditTimelinePanel = ({ caseId }: AuditTimelinePanelProps) => {
  const { data: timeline, isLoading, isError } = useCaseTimeline(caseId);

  const getEventIcon = (eventType: string) => {
    switch (eventType) {
      case 'CASE_CREATED':
        return <Shield className="w-4 h-4 text-[#E94F37]" />;
      case 'CASE_ASSIGNED':
        return <UserCheck className="w-4 h-4 text-[#3B82F6]" />;
      case 'STATUS_CHANGED':
        return <RefreshCw className="w-4 h-4 text-[#F59E0B]" />;
      case 'NOTES_UPDATED':
        return <FileText className="w-4 h-4 text-purple-600" />;
      case 'CASE_RESOLVED':
      case 'CASE_CLOSED':
        return <CheckCircle2 className="w-4 h-4 text-[#10B981]" />;
      default:
        return <History className="w-4 h-4 text-gray-500" />;
    }
  };

  if (isLoading) {
    return (
      <DetailSection title="Compliance Audit Timeline">
        <div className="p-8 text-center animate-pulse text-xs text-gray-400">Loading timeline...</div>
      </DetailSection>
    );
  }

  if (isError || !timeline || timeline.length === 0) {
    return (
      <DetailSection title="Compliance Audit Timeline">
        <div className="p-8 text-center text-xs text-gray-400">No audit records found for this case.</div>
      </DetailSection>
    );
  }

  return (
    <DetailSection
      title="Compliance Audit Timeline"
      subtitle="Immutable audit trail of all actions performed"
    >
      <div className="relative pl-6 space-y-6 before:absolute before:left-2.5 before:top-2 before:bottom-2 before:w-0.5 before:bg-gray-100">
        {timeline.map((event) => (
          <div key={event.id} className="relative flex items-start gap-3 text-xs">
            <div className="absolute -left-[27px] top-0.5 p-1 bg-white border border-gray-200 rounded-full shadow-2xs">
              {getEventIcon(event.eventType)}
            </div>
            <div className="flex-1 bg-gray-50 p-3 rounded-lg border border-gray-100">
              <div className="flex items-center justify-between font-semibold text-[#393E41]">
                <span>{event.eventType.replace('_', ' ')}</span>
                <span className="text-[10px] text-gray-400 font-normal">{formatRelativeTime(event.timestamp)}</span>
              </div>
              {(event.oldValue || event.newValue) && (
                <p className="text-gray-600 mt-1">
                  {event.oldValue && <span className="line-through text-gray-400 mr-1.5">{event.oldValue}</span>}
                  {event.newValue && <span className="font-semibold text-[#393E41]">{event.newValue}</span>}
                </p>
              )}
              <div className="mt-1 text-[10px] text-gray-400 flex items-center justify-between">
                <span>By: {event.performedBy}</span>
                <span>{formatDate(event.timestamp)}</span>
              </div>
            </div>
          </div>
        ))}
      </div>
    </DetailSection>
  );
};
