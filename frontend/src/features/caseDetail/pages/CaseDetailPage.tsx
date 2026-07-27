import { useParams, useNavigate } from 'react-router-dom';
import { PageHeader } from '../../../components/ui/PageHeader';
import { useFraudCaseDetails } from '../api/useCaseDetailData';
import { CaseSummaryPanel } from '../components/CaseSummaryPanel';
import { WorkflowActionsPanel } from '../components/WorkflowActionsPanel';
import { AuditTimelinePanel } from '../components/AuditTimelinePanel';
import { SkeletonCaseDetail } from '../components/SkeletonCaseDetail';
import { Button } from '../../../components/ui/Button';
import { ArrowLeft, ShieldAlert } from 'lucide-react';

export const CaseDetailPage = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();

  const { data: caseDetail, isLoading, isError, refetch } = useFraudCaseDetails(id);

  if (isLoading) {
    return <SkeletonCaseDetail />;
  }

  if (isError || !caseDetail) {
    return (
      <div className="space-y-6">
        <PageHeader title={`Fraud Case #${id}`} />
        <div className="p-8 text-center bg-rose-500/10 border border-rose-500/20 rounded-2xl">
          <ShieldAlert className="w-8 h-8 text-rose-400 mx-auto mb-2" />
          <p className="text-sm font-semibold text-rose-300">
            Failed to load details for Fraud Case #{id}
          </p>
          <div className="mt-4 flex justify-center gap-3">
            <Button size="sm" variant="outline" onClick={() => navigate('/cases')}>
              Back to Queue
            </Button>
            <Button size="sm" variant="primary" onClick={() => refetch()}>
              Retry Connection
            </Button>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="space-y-6">
      <PageHeader
        title={`Fraud Case #${caseDetail.caseId}`}
        subtitle="Analyst workspace, workflow state transitions, and audit timeline"
        action={
          <Button variant="outline" size="sm" onClick={() => navigate('/cases')} className="gap-1">
            <ArrowLeft className="w-3.5 h-3.5" /> Back to Queue
          </Button>
        }
      />

      {/* Grid Layout: Left Column Details & Timeline, Right Column Workflow Actions */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        <div className="lg:col-span-2 space-y-6">
          <CaseSummaryPanel caseDetail={caseDetail} />
          {id && <AuditTimelinePanel caseId={id} />}
        </div>
        <div>
          <WorkflowActionsPanel caseDetail={caseDetail} />
        </div>
      </div>
    </div>
  );
};

export default CaseDetailPage;
