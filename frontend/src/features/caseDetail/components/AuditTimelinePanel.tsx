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
        return <UserCheck className="w-4 h-4 text-orange-400" />;
      case 'STATUS_CHANGED':
        return <RefreshCw className="w-4 h-4 text-amber-400" />;
      case 'NOTES_UPDATED':
        return <FileText className="w-4 h-4 text-orange-400" />;
      case 'CASE_RESOLVED':
      case 'CASE_CLOSED':
        return <CheckCircle2 className="w-4 h-4 text-emerald-400" />;
      default:
        return <History className="w-4 h-4 text-neutral-400" />;
    }
  };

  if (isLoading) {
    return (
      <DetailSection title="Compliance Audit Timeline">
        <div className="p-8 text-center animate-pulse text-xs text-neutral-400">Loading timeline...</div>
      </DetailSection>
    );
  }

  if (isError || !timeline || timeline.length === 0) {
    return (
      <DetailSection title="Compliance Audit Timeline">
        <div className="p-8 text-center text-xs text-neutral-500">No audit records found for this case.</div>
      </DetailSection>
    );
  }

  return (
    <DetailSection
      title="Compliance Audit Timeline"
      subtitle="Immutable audit trail of all compliance operations"
    >
      <div className="relative pl-6 space-y-6 before:absolute before:left-2.5 before:top-2 before:bottom-2 before:w-0.5 before:bg-neutral-800">
        {timeline.map((event) => (
          <div key={event.id} className="relative flex items-start gap-3 text-xs">
            <div className="absolute -left-[27px] top-0.5 p-1 bg-neutral-950 border border-neutral-800 rounded-full shadow-lg">
              {getEventIcon(event.eventType)}
            </div>
            <div className="flex-1 bg-neutral-950 p-3.5 rounded-xl border border-neutral-800">
              <div className="flex items-center justify-between font-bold text-white">
                <span>{event.eventType.replace('_', ' ')}</span>
                <span className="text-[10px] text-neutral-500 font-normal">{formatRelativeTime(event.timestamp)}</span>
              </div>
              {(event.oldValue || event.newValue) && (
                <p className="text-neutral-300 mt-1.5">
                  {event.oldValue && <span className="line-through text-neutral-500 mr-1.5">{event.oldValue}</span>}
                  {event.newValue && <span className="font-bold text-[#E94F37]">{event.newValue}</span>}
                </p>
              )}
              <div className="mt-2 pt-2 border-t border-neutral-800 text-[10px] text-neutral-400 flex items-center justify-between">
                <span>By: <strong className="text-[#E94F37]">{event.performedBy}</strong></span>
                <span>{formatDate(event.timestamp)}</span>
              </div>
            </div>
          </div>
        ))}
      </div>
    </DetailSection>
  );
};
