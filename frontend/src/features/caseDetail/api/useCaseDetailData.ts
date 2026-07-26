import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { apiClient } from '../../../lib/api';
import { ApiResponse } from '../../../types/api';
import {
  FraudCaseDetailResponse,
  FraudCaseAuditResponse,
  AssignCasePayload,
  UpdateStatusPayload,
  UpdateNotesPayload,
  ResolveCasePayload,
} from '../types';

export const useFraudCaseDetails = (caseId: string | undefined) => {
  return useQuery({
    queryKey: ['cases', 'detail', caseId],
    queryFn: async () => {
      if (!caseId) throw new Error('Case ID is required');
      const res = await apiClient.get<unknown, ApiResponse<FraudCaseDetailResponse>>(`/cases/${caseId}`);
      return res.data;
    },
    enabled: !!caseId,
  });
};

export const useCaseTimeline = (caseId: string | undefined) => {
  return useQuery({
    queryKey: ['cases', 'timeline', caseId],
    queryFn: async () => {
      if (!caseId) throw new Error('Case ID is required');
      const res = await apiClient.get<unknown, ApiResponse<FraudCaseAuditResponse[]>>(`/cases/${caseId}/timeline`);
      return res.data;
    },
    enabled: !!caseId,
  });
};

export const useAssignCaseMutation = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: async ({ caseId, assignedTo }: AssignCasePayload) => {
      const res = await apiClient.patch<unknown, ApiResponse<FraudCaseDetailResponse>>(
        `/cases/${caseId}/assign`,
        { assignedTo }
      );
      return res.data;
    },
    onMutate: async ({ caseId, assignedTo }) => {
      await queryClient.cancelQueries({ queryKey: ['cases', 'detail', String(caseId)] });
      const previousDetail = queryClient.getQueryData<FraudCaseDetailResponse>(['cases', 'detail', String(caseId)]);

      if (previousDetail) {
        queryClient.setQueryData(['cases', 'detail', String(caseId)], {
          ...previousDetail,
          assignedTo,
          status: previousDetail.status === 'OPEN' ? 'ASSIGNED' : previousDetail.status,
        });
      }

      return { previousDetail };
    },
    onError: (_err, { caseId }, context) => {
      if (context?.previousDetail) {
        queryClient.setQueryData(['cases', 'detail', String(caseId)], context.previousDetail);
      }
    },
    onSettled: (_data, _error, { caseId }) => {
      queryClient.invalidateQueries({ queryKey: ['cases', 'detail', String(caseId)] });
      queryClient.invalidateQueries({ queryKey: ['cases', 'timeline', String(caseId)] });
      queryClient.invalidateQueries({ queryKey: ['cases', 'queue'] });
      queryClient.invalidateQueries({ queryKey: ['cases', 'summary'] });
    },
  });
};

export const useUpdateCaseStatusMutation = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: async ({ caseId, status }: UpdateStatusPayload) => {
      const res = await apiClient.patch<unknown, ApiResponse<FraudCaseDetailResponse>>(
        `/cases/${caseId}/status`,
        { status }
      );
      return res.data;
    },
    onMutate: async ({ caseId, status }) => {
      await queryClient.cancelQueries({ queryKey: ['cases', 'detail', String(caseId)] });
      const previousDetail = queryClient.getQueryData<FraudCaseDetailResponse>(['cases', 'detail', String(caseId)]);

      if (previousDetail) {
        queryClient.setQueryData(['cases', 'detail', String(caseId)], {
          ...previousDetail,
          status,
        });
      }

      return { previousDetail };
    },
    onError: (_err, { caseId }, context) => {
      if (context?.previousDetail) {
        queryClient.setQueryData(['cases', 'detail', String(caseId)], context.previousDetail);
      }
    },
    onSettled: (_data, _error, { caseId }) => {
      queryClient.invalidateQueries({ queryKey: ['cases', 'detail', String(caseId)] });
      queryClient.invalidateQueries({ queryKey: ['cases', 'timeline', String(caseId)] });
      queryClient.invalidateQueries({ queryKey: ['cases', 'queue'] });
      queryClient.invalidateQueries({ queryKey: ['cases', 'summary'] });
    },
  });
};

export const useUpdateCaseNotesMutation = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: async ({ caseId, reviewNotes }: UpdateNotesPayload) => {
      const res = await apiClient.patch<unknown, ApiResponse<FraudCaseDetailResponse>>(
        `/cases/${caseId}/notes`,
        { reviewNotes }
      );
      return res.data;
    },
    onMutate: async ({ caseId, reviewNotes }) => {
      await queryClient.cancelQueries({ queryKey: ['cases', 'detail', String(caseId)] });
      const previousDetail = queryClient.getQueryData<FraudCaseDetailResponse>(['cases', 'detail', String(caseId)]);

      if (previousDetail) {
        queryClient.setQueryData(['cases', 'detail', String(caseId)], {
          ...previousDetail,
          reviewNotes,
        });
      }

      return { previousDetail };
    },
    onError: (_err, { caseId }, context) => {
      if (context?.previousDetail) {
        queryClient.setQueryData(['cases', 'detail', String(caseId)], context.previousDetail);
      }
    },
    onSettled: (_data, _error, { caseId }) => {
      queryClient.invalidateQueries({ queryKey: ['cases', 'detail', String(caseId)] });
      queryClient.invalidateQueries({ queryKey: ['cases', 'timeline', String(caseId)] });
    },
  });
};

export const useResolveCaseMutation = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: async ({ caseId, resolution, status }: ResolveCasePayload) => {
      const res = await apiClient.patch<unknown, ApiResponse<FraudCaseDetailResponse>>(
        `/cases/${caseId}/resolve`,
        { resolution, status }
      );
      return res.data;
    },
    onMutate: async ({ caseId, resolution, status }) => {
      await queryClient.cancelQueries({ queryKey: ['cases', 'detail', String(caseId)] });
      const previousDetail = queryClient.getQueryData<FraudCaseDetailResponse>(['cases', 'detail', String(caseId)]);

      if (previousDetail) {
        queryClient.setQueryData(['cases', 'detail', String(caseId)], {
          ...previousDetail,
          resolution,
          status,
          closedAt: new Date().toISOString(),
        });
      }

      return { previousDetail };
    },
    onError: (_err, { caseId }, context) => {
      if (context?.previousDetail) {
        queryClient.setQueryData(['cases', 'detail', String(caseId)], context.previousDetail);
      }
    },
    onSettled: (_data, _error, { caseId }) => {
      queryClient.invalidateQueries({ queryKey: ['cases', 'detail', String(caseId)] });
      queryClient.invalidateQueries({ queryKey: ['cases', 'timeline', String(caseId)] });
      queryClient.invalidateQueries({ queryKey: ['cases', 'queue'] });
      queryClient.invalidateQueries({ queryKey: ['cases', 'summary'] });
    },
  });
};
