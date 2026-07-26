import React, { Component, ErrorInfo, ReactNode } from 'react';
import { Button } from './Button';
import { AlertOctagon } from 'lucide-react';

interface Props {
  children: ReactNode;
  fallback?: ReactNode;
}

interface State {
  hasError: boolean;
  error: Error | null;
}

export class ErrorBoundary extends Component<Props, State> {
  public state: State = {
    hasError: false,
    error: null,
  };

  public static getDerivedStateFromError(error: Error): State {
    return { hasError: true, error };
  }

  public componentDidCatch(error: Error, errorInfo: ErrorInfo) {
    console.error('Uncaught React rendering error:', error, errorInfo);
  }

  private handleReset = () => {
    this.setState({ hasError: false, error: null });
  };

  public render() {
    if (this.state.hasError) {
      if (this.props.fallback) {
        return this.props.fallback;
      }

      return (
        <div className="p-8 my-6 text-center bg-rose-50 border border-rose-200 rounded-xl max-w-lg mx-auto">
          <AlertOctagon className="w-10 h-10 text-[#EF4444] mx-auto mb-3" />
          <h2 className="text-base font-bold text-[#393E41]">Unexpected Component Error</h2>
          <p className="mt-1 text-xs text-gray-600">
            {this.state.error?.message || 'A rendering error occurred in this view.'}
          </p>
          <div className="mt-4">
            <Button size="sm" variant="primary" onClick={handleReset}>
              Reload Component
            </Button>
          </div>
        </div>
      );
    }

    return this.props.children;
  }
}
