import React from 'react';
import { Button } from '../../../components/ui/Button';
import { AlertTriangle } from 'lucide-react';

interface ResolveConfirmationModalProps {
  isOpen: boolean;
  onClose: () => void;
  onConfirm: () => void;
  status: string;
  resolution: string;
  isLoading: boolean;
}

export const ResolveConfirmationModal: React.FC<ResolveConfirmationModalProps> = ({
  isOpen,
  onClose,
  onConfirm,
  status,
  resolution,
  isLoading,
}) => {
  if (!isOpen) return null;

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/40 backdrop-blur-xs">
      <div className="bg-white rounded-xl shadow-lg border border-gray-100 max-w-md w-full p-6 space-y-4 animate-in fade-in zoom-in-95">
        <div className="flex items-center gap-3 text-[#E94F37]">
          <div className="p-2 bg-rose-50 rounded-full">
            <AlertTriangle className="w-6 h-6" />
          </div>
          <h3 className="text-base font-bold text-[#393E41]">Confirm Case Resolution</h3>
        </div>

        <p className="text-xs text-gray-600">
          Are you sure you want to resolve and finalize this case with status{' '}
          <span className="font-bold text-[#393E41]">{status}</span>? Once finalized, the case will be set to{' '}
          <span className="font-bold text-[#393E41]">CLOSED</span> and cannot be edited.
        </p>

        <div className="p-3 bg-gray-50 rounded-lg text-xs font-mono text-gray-700 border border-gray-100">
          <span className="text-[10px] text-gray-400 block uppercase font-semibold">Resolution Summary</span>
          "{resolution}"
        </div>

        <div className="flex items-center justify-end gap-3 pt-2">
          <Button variant="outline" size="sm" onClick={onClose} disabled={isLoading}>
            Cancel
          </Button>
          <Button variant="primary" size="sm" onClick={onConfirm} isLoading={isLoading}>
            Confirm Resolution
          </Button>
        </div>
      </div>
    </div>
  );
};
