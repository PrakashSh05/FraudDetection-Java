import { render, screen } from '@testing-library/react';
import { describe, it, expect } from 'vitest';
import { Badge } from '../Badge';

describe('Badge Component', () => {
  it('renders status text correctly', () => {
    render(<Badge variant="success">APPROVED</Badge>);
    expect(screen.getByText('APPROVED')).toBeInTheDocument();
  });

  it('applies proper variant styles for success and danger', () => {
    const { container: successBadge } = render(<Badge variant="success">SUCCESS</Badge>);
    expect(successBadge.firstChild).toHaveClass('bg-emerald-50');

    const { container: dangerBadge } = render(<Badge variant="danger">REJECTED</Badge>);
    expect(dangerBadge.firstChild).toHaveClass('bg-rose-50');
  });
});
