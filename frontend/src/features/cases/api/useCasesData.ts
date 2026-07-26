import { useQuery } from '@tanstack/react-query';
import { apiClient } from '../../../lib/api';
import { ApiResponse, PageResponse } from '../../../types/api';
import { FraudCaseSummary, CasesFilterParams } from '../types';

export const useFraudCasesQueue = (params: CasesFilterParams) => {
  return useQuery({
    queryKey: ['cases', 'queue', params],
    queryFn: async () => {
      const cleanParams: Record<string, any> = {};
      
      if (params.status) cleanParams.status = params.status;
      if (params.priority) cleanParams.priority = params.priority;
      if (params.assignedTo) cleanParams.assignedTo = params.assignedTo;
      if (params.riskLevel) cleanParams.riskLevel = params.riskLevel;
      if (params.transactionId) cleanParams.transactionId = params.transactionId;
      if (params.caseId) cleanParams.caseId = params.caseId;
      cleanParams.page = params.page ?? 0;
      cleanParams.size = params.size ?? 20;
      cleanParams.sort = params.sort ?? 'openedAt,desc';

      const res = await apiClient.get<unknown, ApiResponse<PageResponse<FraudCaseSummary>>>('/cases', {
        params: cleanParams,
      });

      return res.data;
    },
  });
};
