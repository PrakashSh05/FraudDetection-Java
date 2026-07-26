export interface AnalyticsOverview {
  totalTransactions: number;
  approvedTransactions: number;
  reviewTransactions: number;
  rejectedTransactions: number;
  averageRiskScore: number;
}

export interface RiskDistributionItem {
  riskLevel: string;
  count: number;
  percentage: number;
}

export interface DailyTrendItem {
  date: string;
  totalTransactions: number;
  flaggedTransactions: number;
  averageRiskScore: number;
}

export interface TopRuleItem {
  ruleId: string;
  ruleName: string;
  triggerCount: number;
  percentage: number;
}

export interface FraudCaseQueueSummary {
  totalCases: number;
  openCases: number;
  assignedCases: number;
  underReviewCases: number;
  approvedCases: number;
  declinedCases: number;
  escalatedCases: number;
  closedCases: number;
  criticalCases: number;
  highCases: number;
  mediumCases: number;
  lowCases: number;
}
