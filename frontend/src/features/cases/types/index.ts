export interface FraudCaseSummary {
  caseId: number;
  transactionId: number;
  userId: number;
  amount: number;
  transactionType: string;
  riskScore: number;
  riskLevel: string;
  status: string;
  priority: string;
  assignedTo?: string;
  openedAt: string;
  createdAt: string;
}

export interface CasesFilterParams {
  status?: string;
  priority?: string;
  assignedTo?: string;
  riskLevel?: string;
  transactionId?: number;
  caseId?: number;
  page?: number;
  size?: number;
  sort?: string;
}
