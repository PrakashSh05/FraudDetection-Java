import React from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { PageHeader } from '../../../components/ui/PageHeader';
import { useTransactionInvestigation } from '../api/useInvestigationData';
import { TransactionSummaryCard } from '../components/TransactionSummaryCard';
import { RiskAssessmentCard } from '../components/RiskAssessmentCard';
import { TriggeredRulesList } from '../components/TriggeredRulesList';
import { DecisionCard } from '../components/DecisionCard';
import { SkeletonInvestigation } from '../components/SkeletonInvestigation';
import { EmptyState } from '../../../components/ui/EmptyState';
import { Button } from '../../../components/ui/Button';
import { ArrowLeft, SearchCode } from 'lucide-react';

export const InvestigationPage: React.FC = () => {
  const { transactionId } = useParams<{ transactionId: string }>();
  const navigate = useNavigate();

  const { data, isLoading, isError, refetch } = useTransactionInvestigation(transactionId);

  if (isLoading) {
    return <SkeletonInvestigation />;
  }

  if (isError || !data) {
    return (
      <div className="space-y-6">
        <PageHeader title={`Transaction Investigation #${transactionId}`} />
        <div className="p-8 text-center bg-rose-50 border border-rose-200 rounded-xl">
          <SearchCode className="w-8 h-8 text-[#EF4444] mx-auto mb-2" />
          <p className="text-sm font-semibold text-[#EF4444]">
            Failed to load investigation telemetry for Transaction #{transactionId}
          </p>
          <div className="mt-4 flex justify-center gap-3">
            <Button size="sm" variant="outline" onClick={() => navigate(-1)}>
              Back
            </Button>
            <Button size="sm" variant="primary" onClick={() => refetch()}>
              Retry Connection
            </Button>
          </div>
        </div>
      </div>
    );
  }

  const { transaction, evaluation, triggeredRules } = data;

  return (
    <div className="space-y-6">
      <PageHeader
        title={`Transaction Investigation #${transaction.transactionId}`}
        subtitle="Deep risk evaluation audit, triggered rules breakdown, and score breakdown"
        action={
          <Button variant="outline" size="sm" onClick={() => navigate(-1)} className="gap-1">
            <ArrowLeft className="w-3.5 h-3.5" /> Back
          </Button>
        }
      />

      {/* Grid Row 1: Transaction Summary & Risk Assessment */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <TransactionSummaryCard transaction={transaction} />
        <RiskAssessmentCard evaluation={evaluation} />
      </div>

      {/* Triggered Rules List */}
      <TriggeredRulesList rules={triggeredRules} />

      {/* Decision Card */}
      <DecisionCard decision={evaluation.decision} transactionId={transaction.transactionId} />
    </div>
  );
};

export default InvestigationPage;
