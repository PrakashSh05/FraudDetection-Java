import { TransactionDetails, RiskEvaluationDetails, TriggeredRuleDetails } from '../../investigation/types';

export interface FraudCaseDetailResponse {
  caseId: number;
  status: string;
  priority: string;
  assignedTo?: string;
  openedAt: string;
  closedAt?: string;
  resolution?: string;
  reviewNotes?: string;
  createdAt: string;
  updatedAt?: string;
  transaction: TransactionDetails;
  evaluation: RiskEvaluationDetails;
  triggeredRules: TriggeredRuleDetails[];
}

export interface FraudCaseAuditResponse {
  id: number;
  eventType: string;
  oldValue?: string;
  newValue?: string;
  performedBy: string;
  timestamp: string;
}

export interface AssignCasePayload {
  caseId: number;
  assignedTo: string;
}

export interface UpdateStatusPayload {
  caseId: number;
  status: string;
}

export interface UpdateNotesPayload {
  caseId: number;
  reviewNotes: string;
}

export interface ResolveCasePayload {
  caseId: number;
  resolution: string;
  status: string;
}
