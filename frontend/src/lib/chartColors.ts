export const CHART_COLORS = {
  primary: '#E94F37',
  dark: '#393E41',
  success: '#10B981',
  warning: '#F59E0B',
  danger: '#EF4444',
  info: '#3B82F6',
  critical: '#991B1B',
  neutral: '#6C757D',
};

export const RISK_LEVEL_COLORS: Record<string, string> = {
  LOW: CHART_COLORS.success,
  MEDIUM: CHART_COLORS.warning,
  HIGH: CHART_COLORS.primary,
  CRITICAL: CHART_COLORS.critical,
};

export const CASE_STATUS_COLORS: Record<string, 'success' | 'warning' | 'danger' | 'info' | 'neutral'> = {
  OPEN: 'warning',
  ASSIGNED: 'info',
  UNDER_REVIEW: 'info',
  APPROVED: 'success',
  DECLINED: 'danger',
  ESCALATED: 'danger',
  CLOSED: 'neutral',
};

export const PRIORITY_BADGE_VARIANTS: Record<string, 'success' | 'warning' | 'danger' | 'info' | 'neutral'> = {
  LOW: 'success',
  MEDIUM: 'warning',
  HIGH: 'danger',
  CRITICAL: 'danger',
};
