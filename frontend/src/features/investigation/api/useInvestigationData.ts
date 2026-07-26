import { useQuery } from '@tanstack/react-query';
import { apiClient } from '../../../lib/api';
import { ApiResponse } from '../../../types/api';
import { InvestigationResponse } from '../types';

export const useTransactionInvestigation = (transactionId: string | undefined) => {
  return useQuery({
    queryKey: ['investigation', 'transaction', transactionId],
    queryFn: async () => {
      if (!transactionId) throw new Error('Transaction ID is required');
      const res = await apiClient.get<unknown, ApiResponse<InvestigationResponse>>(
        `/investigation/transaction/${transactionId}`
      );
      return res.data;
    },
    enabled: !!transactionId,
  });
};
