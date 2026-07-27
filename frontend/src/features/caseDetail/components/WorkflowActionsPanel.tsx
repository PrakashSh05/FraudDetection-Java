import { useState } from 'react';
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

export const WorkflowActionsPanel = ({ caseDetail }: WorkflowActionsPanelProps) => {
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
        onError: (err: any) => showToast(err.response?.data?.message || 'Assignment failed', 'danger'),
      }
    );
  };

  const handleStatusUpdate = () => {
    statusMutation.mutate(
      { caseId: caseDetail.caseId, status: statusSelect },
      {
        onSuccess: () => showToast(`Status updated to ${statusSelect}`, 'success'),
        onError: (err: any) => showToast(err.response?.data?.message || 'Status transition failed', 'danger'),
      }
    );
  };

  const handleNotesUpdate = () => {
    if (!notesInput.trim()) return;
    notesMutation.mutate(
      { caseId: caseDetail.caseId, reviewNotes: notesInput.trim() },
      {
        onSuccess: () => showToast('Review notes updated successfully', 'success'),
        onError: (err: any) => showToast(err.response?.data?.message || 'Failed to update notes', 'danger'),
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
        onError: (err: any) => showToast(err.response?.data?.message || 'Case resolution failed', 'danger'),
      }
    );
  };

  return (
    <DetailSection
      title="Analyst Action Panel"
      subtitle="Lifecycle status transitions, analyst assignment, notes, and case resolution"
    >
      {isClosed && (
        <div className="mb-4 p-3 bg-amber-500/10 border border-amber-500/30 rounded-xl text-xs text-amber-300 flex items-center gap-2">
          <Lock className="w-4 h-4 text-amber-400 shrink-0" />
          <span>Case is CLOSED. Workflow actions are locked for editing.</span>
        </div>
      )}

      <div className="space-y-4">
        {/* Action 1: Assign Analyst */}
        <div className="p-4 bg-neutral-950 rounded-xl border border-neutral-800 space-y-3">
          <div className="flex items-center gap-2 text-xs font-bold text-white">
            <UserCheck className="w-4 h-4 text-[#E94F37]" />
            <span>Assign Case to Analyst</span>
          </div>
          <div className="flex gap-2">
            <input
              type="text"
              placeholder="Analyst username..."
              value={assignedToInput}
              onChange={(e) => setAssignedToInput(e.target.value)}
              disabled={isClosed}
              aria-label="Analyst username"
              className="flex-1 px-3 py-1.5 text-xs bg-black border border-neutral-800 rounded-lg text-white focus:outline-none focus:border-[#E94F37] disabled:bg-neutral-950"
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

        {/* Action 2: Update Case Lifecycle Status */}
        <div className="p-4 bg-neutral-950 rounded-xl border border-neutral-800 space-y-3">
          <div className="flex items-center gap-2 text-xs font-bold text-white">
            <RefreshCw className="w-4 h-4 text-orange-400" />
            <span>Transition Status</span>
          </div>
          <div className="flex gap-2">
            <select
              value={statusSelect}
              onChange={(e) => setStatusSelect(e.target.value)}
              disabled={isClosed}
              aria-label="Transition Status"
              className="flex-1 px-3 py-1.5 text-xs bg-black border border-neutral-800 rounded-lg text-white focus:outline-none focus:border-[#E94F37] disabled:bg-neutral-950"
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

        {/* Action 3: Review Notes */}
        <div className="p-4 bg-neutral-950 rounded-xl border border-neutral-800 space-y-3">
          <div className="flex items-center gap-2 text-xs font-bold text-white">
            <FileText className="w-4 h-4 text-orange-400" />
            <span>Record Investigation Notes</span>
          </div>
          <textarea
            rows={3}
            placeholder="Record internal investigation findings..."
            value={notesInput}
            onChange={(e) => setNotesInput(e.target.value)}
            disabled={isClosed}
            aria-label="Record internal investigation findings"
            className="w-full px-3 py-2 text-xs bg-black border border-neutral-800 rounded-lg text-white focus:outline-none focus:border-[#E94F37] disabled:bg-neutral-950"
          ></textarea>
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
        <div className="p-4 bg-neutral-950 rounded-xl border border-orange-500/30 space-y-3">
          <div className="flex items-center gap-2 text-xs font-bold text-orange-400">
            <CheckCircle2 className="w-4 h-4" />
            <span>Resolve & Finalize Case</span>
          </div>
          <div className="space-y-2">
            <select
              value={resolveStatusSelect}
              onChange={(e) => setResolveStatusSelect(e.target.value)}
              disabled={isClosed}
              aria-label="Select Resolution Decision"
              className="w-full px-3 py-1.5 text-xs bg-black border border-neutral-800 rounded-lg text-white focus:outline-none focus:border-[#E94F37] disabled:bg-neutral-950"
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
              className="w-full px-3 py-1.5 text-xs bg-black border border-neutral-800 rounded-lg text-white focus:outline-none focus:border-[#E94F37] disabled:bg-neutral-950"
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

      {/* ConfirmationDialog */}
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
        <div className="p-3 bg-neutral-950 rounded-lg text-xs font-mono text-neutral-300 border border-neutral-800">
          <span className="text-[10px] text-neutral-500 block uppercase font-bold">Resolution Summary</span>
          "{resolutionInput}"
        </div>
      </ConfirmationDialog>
    </DetailSection>
  );
};
