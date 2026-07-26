import React, { useState } from 'react';
import { FraudCaseDetailResponse } from '../types';
import {
  useAssignCaseMutation,
  useUpdateCaseStatusMutation,
  useUpdateCaseNotesMutation,
  useResolveCaseMutation,
} from '../api/useCaseDetailData';
import { DetailSection } from '../../../components/ui/DetailSection';
import { Button } from '../../../components/ui/Button';
import { ConfirmationDialog } from '../../../components/ui/ConfirmationDialog';
import { useToast } from '../../../components/ui/Toast';
import { UserCheck, RefreshCw, FileText, CheckCircle2, Lock } from 'lucide-react';

interface WorkflowActionsPanelProps {
  caseDetail: FraudCaseDetailResponse;
}

export const WorkflowActionsPanel: React.FC<WorkflowActionsPanelProps> = ({ caseDetail }) => {
  const isClosed = caseDetail.status === 'CLOSED';
  const { showToast } = useToast();

  // Form states
  const [assignedToInput, setAssignedToInput] = useState(caseDetail.assignedTo || '');
  const [statusSelect, setStatusSelect] = useState(caseDetail.status);
  const [notesInput, setNotesInput] = useState(caseDetail.reviewNotes || '');
  const [resolutionInput, setResolutionInput] = useState('');
  const [resolveStatusSelect, setResolveStatusSelect] = useState('APPROVED');

  // Confirmation dialog state
  const [isResolveModalOpen, setIsResolveModalOpen] = useState(false);

  // React Query Mutations
  const assignMutation = useAssignCaseMutation();
  const statusMutation = useUpdateCaseStatusMutation();
  const notesMutation = useUpdateCaseNotesMutation();
  const resolveMutation = useResolveCaseMutation();

  const handleAssign = () => {
    if (!assignedToInput.trim()) return;
    assignMutation.mutate(
      { caseId: caseDetail.caseId, assignedTo: assignedToInput.trim() },
      {
        onSuccess: () => showToast('Analyst assigned successfully', 'success'),
        onError: (err: any) => showToast(err?.response?.data?.message || 'Assignment failed', 'danger'),
      }
    );
  };

  const handleStatusUpdate = () => {
    statusMutation.mutate(
      { caseId: caseDetail.caseId, status: statusSelect },
      {
        onSuccess: () => showToast(`Status updated to ${statusSelect}`, 'success'),
        onError: (err: any) => showToast(err?.response?.data?.message || 'Status transition failed', 'danger'),
      }
    );
  };

  const handleNotesUpdate = () => {
    if (!notesInput.trim()) return;
    notesMutation.mutate(
      { caseId: caseDetail.caseId, reviewNotes: notesInput.trim() },
      {
        onSuccess: () => showToast('Review notes updated successfully', 'success'),
        onError: (err: any) => showToast(err?.response?.data?.message || 'Failed to update notes', 'danger'),
      }
    );
  };

  const handleResolveConfirm = () => {
    if (!resolutionInput.trim()) return;
    resolveMutation.mutate(
      { caseId: caseDetail.caseId, resolution: resolutionInput.trim(), status: resolveStatusSelect },
      {
        onSuccess: () => {
          setIsResolveModalOpen(false);
          showToast(`Case finalized with ${resolveStatusSelect}`, 'success');
        },
        onError: (err: any) => {
          setIsResolveModalOpen(false);
          showToast(err?.response?.data?.message || 'Resolution failed', 'danger');
        },
      }
    );
  };

  return (
    <DetailSection
      title="Analyst Review Workflow"
      subtitle={isClosed ? 'Case is CLOSED — actions disabled' : 'Execute manual review workflow actions'}
      action={
        isClosed ? (
          <span className="inline-flex items-center gap-1 text-xs text-gray-400 font-semibold bg-gray-100 px-2.5 py-1 rounded-md">
            <Lock className="w-3 h-3" /> Immutable
          </span>
        ) : undefined
      }
    >
      <div className="space-y-6">
        {/* Action 1: Assign Analyst */}
        <div className="p-4 bg-gray-50 rounded-xl border border-gray-100 space-y-3">
          <label htmlFor="assign-analyst-input" className="flex items-center gap-2 text-xs font-bold text-[#393E41]">
            <UserCheck className="w-4 h-4 text-[#3B82F6]" />
            <span>Assign Analyst</span>
          </label>
          <div className="flex gap-2">
            <input
              id="assign-analyst-input"
              type="text"
              placeholder="Analyst username..."
              value={assignedToInput}
              onChange={(e) => setAssignedToInput(e.target.value)}
              disabled={isClosed}
              aria-label="Assign Analyst Username"
              className="flex-1 px-3 py-1.5 text-xs bg-white border border-gray-200 rounded-lg focus:outline-none focus:ring-1 focus:ring-[#E94F37] disabled:bg-gray-100"
            />
            <Button
              size="sm"
              variant="outline"
              disabled={isClosed || !assignedToInput.trim()}
              isLoading={assignMutation.isPending}
              onClick={handleAssign}
            >
              Assign
            </Button>
          </div>
        </div>

        {/* Action 2: Update Workflow Status */}
        <div className="p-4 bg-gray-50 rounded-xl border border-gray-100 space-y-3">
          <label htmlFor="status-select-input" className="flex items-center gap-2 text-xs font-bold text-[#393E41]">
            <RefreshCw className="w-4 h-4 text-[#F59E0B]" />
            <span>Update Workflow Status</span>
          </label>
          <div className="flex gap-2">
            <select
              id="status-select-input"
              value={statusSelect}
              onChange={(e) => setStatusSelect(e.target.value)}
              disabled={isClosed}
              aria-label="Select Workflow Status"
              className="flex-1 px-3 py-1.5 text-xs bg-white border border-gray-200 rounded-lg focus:outline-none focus:ring-1 focus:ring-[#E94F37] disabled:bg-gray-100"
            >
              <option value="OPEN">OPEN</option>
              <option value="ASSIGNED">ASSIGNED</option>
              <option value="UNDER_REVIEW">UNDER_REVIEW</option>
              <option value="APPROVED">APPROVED</option>
              <option value="DECLINED">DECLINED</option>
              <option value="ESCALATED">ESCALATED</option>
            </select>
            <Button
              size="sm"
              variant="outline"
              disabled={isClosed || statusSelect === caseDetail.status}
              isLoading={statusMutation.isPending}
              onClick={handleStatusUpdate}
            >
              Update Status
            </Button>
          </div>
        </div>

        {/* Action 3: Analyst Review Notes */}
        <div className="p-4 bg-gray-50 rounded-xl border border-gray-100 space-y-3">
          <label htmlFor="notes-textarea" className="flex items-center gap-2 text-xs font-bold text-[#393E41]">
            <FileText className="w-4 h-4 text-purple-600" />
            <span>Analyst Review Notes</span>
          </label>
          <textarea
            id="notes-textarea"
            rows={3}
            placeholder="Record internal investigation findings..."
            value={notesInput}
            onChange={(e) => setNotesInput(e.target.value)}
            disabled={isClosed}
            aria-label="Analyst Review Notes Input"
            className="w-full p-3 text-xs bg-white border border-gray-200 rounded-lg focus:outline-none focus:ring-1 focus:ring-[#E94F37] disabled:bg-gray-100"
          />
          <div className="flex justify-end">
            <Button
              size="sm"
              variant="outline"
              disabled={isClosed || !notesInput.trim()}
              isLoading={notesMutation.isPending}
              onClick={handleNotesUpdate}
            >
              Save Notes
            </Button>
          </div>
        </div>

        {/* Action 4: Resolve & Finalize Case */}
        <div className="p-4 bg-rose-50/50 rounded-xl border border-rose-100 space-y-3">
          <div className="flex items-center gap-2 text-xs font-bold text-[#E94F37]">
            <CheckCircle2 className="w-4 h-4" />
            <span>Resolve & Finalize Case</span>
          </div>
          <div className="space-y-2">
            <select
              value={resolveStatusSelect}
              onChange={(e) => setResolveStatusSelect(e.target.value)}
              disabled={isClosed}
              aria-label="Select Resolution Decision"
              className="w-full px-3 py-1.5 text-xs bg-white border border-gray-200 rounded-lg focus:outline-none focus:ring-1 focus:ring-[#E94F37] disabled:bg-gray-100"
            >
              <option value="APPROVED">APPROVED (Clear Transaction)</option>
              <option value="DECLINED">DECLINED (Confirm Fraud)</option>
              <option value="ESCALATED">ESCALATED (Senior Review)</option>
            </select>
            <input
              type="text"
              placeholder="Final resolution outcome summary..."
              value={resolutionInput}
              onChange={(e) => setResolutionInput(e.target.value)}
              disabled={isClosed}
              aria-label="Resolution Outcome Summary"
              className="w-full px-3 py-1.5 text-xs bg-white border border-gray-200 rounded-lg focus:outline-none focus:ring-1 focus:ring-[#E94F37] disabled:bg-gray-100"
            />
          </div>
          <Button
            size="sm"
            variant="primary"
            disabled={isClosed || !resolutionInput.trim()}
            onClick={() => setIsResolveModalOpen(true)}
            className="w-full"
          >
            Resolve & Finalize Case
          </Button>
        </div>
      </div>

      {/* Reusable Generic ConfirmationDialog */}
      <ConfirmationDialog
        isOpen={isResolveModalOpen}
        onClose={() => setIsResolveModalOpen(false)}
        onConfirm={handleResolveConfirm}
        title="Confirm Case Resolution"
        description={`Are you sure you want to resolve and finalize this case with status ${resolveStatusSelect}? Once finalized, the case will be set to CLOSED and cannot be edited.`}
        confirmText="Confirm Resolution"
        variant="primary"
        isLoading={resolveMutation.isPending}
      >
        <div className="p-3 bg-gray-50 rounded-lg text-xs font-mono text-gray-700 border border-gray-100">
          <span className="text-[10px] text-gray-400 block uppercase font-semibold">Resolution Summary</span>
          "{resolutionInput}"
        </div>
      </ConfirmationDialog>
    </DetailSection>
  );
};
