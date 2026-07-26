import { useQuery } from '@tanstack/react-query';
import { apiClient } from '../../../lib/api';
import { ApiResponse } from '../../../types/api';
import {
  AnalyticsOverview,
  RiskDistributionItem,
  DailyTrendItem,
  TopRuleItem,
  FraudCaseQueueSummary,
} from '../types';

export const useAnalyticsOverview = () => {
  return useQuery({
    queryKey: ['analytics', 'overview'],
    queryFn: async () => {
      const res = await apiClient.get<unknown, ApiResponse<AnalyticsOverview>>('/analytics/overview');
      return res.data;
    },
  });
};

export const useRiskDistribution = () => {
  return useQuery({
    queryKey: ['analytics', 'risk-distribution'],
    queryFn: async () => {
      const res = await apiClient.get<unknown, ApiResponse<RiskDistributionItem[]>>('/analytics/risk-distribution');
      return res.data;
    },
  });
};

export const useDailyTrend = () => {
  return useQuery({
    queryKey: ['analytics', 'daily-trend'],
    queryFn: async () => {
      const res = await apiClient.get<unknown, ApiResponse<DailyTrendItem[]>>('/analytics/daily-trend');
      return res.data;
    },
  });
};

export const useTopRules = () => {
  return useQuery({
    queryKey: ['analytics', 'top-rules'],
    queryFn: async () => {
      const res = await apiClient.get<unknown, ApiResponse<TopRuleItem[]>>('/analytics/top-rules');
      return res.data;
    },
  });
};

export const useQueueSummary = () => {
  return useQuery({
    queryKey: ['cases', 'summary'],
    queryFn: async () => {
      const res = await apiClient.get<unknown, ApiResponse<FraudCaseQueueSummary>>('/cases/summary');
      return res.data;
    },
  });
};
