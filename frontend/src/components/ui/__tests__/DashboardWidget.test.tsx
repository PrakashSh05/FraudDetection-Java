import { render, screen, fireEvent } from '@testing-library/react';
import { describe, it, expect, vi } from 'vitest';
import { DashboardWidget } from '../DashboardWidget';

describe('DashboardWidget Component', () => {
  it('renders children when state is success (isLoading=false, isError=false)', () => {
    render(
      <DashboardWidget title="Metrics Widget" isLoading={false} isError={false}>
        <div>Widget Content Payload</div>
      </DashboardWidget>
    );

    expect(screen.getByText('Metrics Widget')).toBeInTheDocument();
    expect(screen.getByText('Widget Content Payload')).toBeInTheDocument();
  });

  it('renders error state and handles retry click', () => {
    const handleRetry = vi.fn();
    render(
      <DashboardWidget
        title="Metrics Widget"
        isLoading={false}
        isError={true}
        errorMessage="Network error loading metrics"
        onRetry={handleRetry}
      >
        <div>Payload</div>
      </DashboardWidget>
    );

    expect(screen.getByText('Network error loading metrics')).toBeInTheDocument();

    const retryBtn = screen.getByRole('button', { name: /retry/i });
    fireEvent.click(retryBtn);
    expect(handleRetry).toHaveBeenCalledTimes(1);
  });

  it('renders empty state message when isEmpty is true', () => {
    render(
      <DashboardWidget
        title="Metrics Widget"
        isLoading={false}
        isError={false}
        isEmpty={true}
        emptyMessage="No metrics recorded"
      >
        <div>Payload</div>
      </DashboardWidget>
    );

    expect(screen.getByText('No metrics recorded')).toBeInTheDocument();
  });
});
