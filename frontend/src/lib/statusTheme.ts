export type BadgeVariant = 'success' | 'warning' | 'danger' | 'info' | 'neutral';

export const STATUS_THEME_MAP: Record<string, { variant: BadgeVariant; label: string }> = {
  OPEN: { variant: 'warning', label: 'OPEN' },
  ASSIGNED: { variant: 'info', label: 'ASSIGNED' },
  UNDER_REVIEW: { variant: 'info', label: 'UNDER REVIEW' },
  APPROVED: { variant: 'success', label: 'APPROVED' },
  DECLINED: { variant: 'danger', label: 'DECLINED' },
  ESCALATED: { variant: 'danger', label: 'ESCALATED' },
  CLOSED: { variant: 'neutral', label: 'CLOSED' },
};

export const PRIORITY_THEME_MAP: Record<string, { variant: BadgeVariant; label: string }> = {
  LOW: { variant: 'success', label: 'LOW' },
  MEDIUM: { variant: 'warning', label: 'MEDIUM' },
  HIGH: { variant: 'danger', label: 'HIGH' },
  CRITICAL: { variant: 'danger', label: 'CRITICAL' },
};

export const DECISION_THEME_MAP: Record<string, { variant: BadgeVariant; label: string }> = {
  APPROVED: { variant: 'success', label: 'APPROVED' },
  MONITOR: { variant: 'warning', label: 'MONITOR' },
  REVIEW: { variant: 'warning', label: 'REVIEW' },
  REJECTED: { variant: 'danger', label: 'REJECTED' },
};
