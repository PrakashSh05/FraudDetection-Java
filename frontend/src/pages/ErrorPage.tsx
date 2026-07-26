import React from 'react';
import { Button } from '../components/ui/Button';
import { ShieldAlert } from 'lucide-react';

interface ErrorPageProps {
  error?: Error;
  resetErrorBoundary?: () => void;
}

export const ErrorPage: React.FC<ErrorPageProps> = ({ error, resetErrorBoundary }) => {
  return (
    <div className="min-h-screen bg-[#F6F7EB] flex flex-col items-center justify-center p-6 text-center">
      <div className="p-4 bg-rose-50 text-[#EF4444] rounded-full mb-4">
        <ShieldAlert className="w-12 h-12" />
      </div>
      <h1 className="text-2xl font-bold text-[#393E41]">Something went wrong</h1>
      <p className="mt-2 text-sm text-gray-500 max-w-md">
        {error?.message || 'An unexpected application error occurred.'}
      </p>
      {resetErrorBoundary && (
        <div className="mt-6">
          <Button variant="primary" onClick={resetErrorBoundary}>
            Try Again
          </Button>
        </div>
      )}
    </div>
  );
};
