export interface TransactionDetails {
  transactionId: number;
  userId: number;
  amount: number;
  transactionType: string;
  status: string;
  createdAt: string;
}

export interface RiskEvaluationDetails {
  riskScore: number;
  riskLevel: string;
  decision: string;
  processingTimeMs: number;
  evaluationTimestamp: string;
}

export interface TriggeredRuleDetails {
  ruleId: string;
  ruleName: string;
  category: string;
  severity: string;
  points: number;
  description: string;
}

export interface InvestigationResponse {
  transaction: TransactionDetails;
  evaluation: RiskEvaluationDetails;
  triggeredRules: TriggeredRuleDetails[];
}
