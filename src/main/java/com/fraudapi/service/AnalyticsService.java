package com.fraudapi.service;

import com.fraudapi.constants.Decision;
import com.fraudapi.constants.RiskLevel;
import com.fraudapi.dto.analytics.AnalyticsOverviewResponse;
import com.fraudapi.dto.analytics.DailyTrendResponse;
import com.fraudapi.dto.analytics.RiskDistributionResponse;
import com.fraudapi.dto.analytics.TopRuleResponse;
import com.fraudapi.repository.AnalyticsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Read-only service aggregating transaction risk metrics for analytics dashboards and reporting.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class AnalyticsService {

    private final AnalyticsRepository analyticsRepository;

    /**
     * Retrieves overall transaction volume, decision breakdown, and average risk score metrics.
     *
     * @return overview response DTO
     */
    public AnalyticsOverviewResponse getOverviewAnalytics() {
        long total = analyticsRepository.count();
        Double avgScore = analyticsRepository.getAverageRiskScore();

        Map<String, Long> decisionCounts = new HashMap<>();
        List<Object[]> groupedDecisions = analyticsRepository.countByDecisionGrouped();
        for (Object[] row : groupedDecisions) {
            if (row[0] != null) {
                decisionCounts.put(row[0].toString(), ((Number) row[1]).longValue());
            }
        }

        long approved = decisionCounts.getOrDefault(Decision.APPROVED.name(), 0L);
        long monitor = decisionCounts.getOrDefault(Decision.MONITOR.name(), 0L);
        long review = decisionCounts.getOrDefault(Decision.REVIEW.name(), 0L);
        long rejected = decisionCounts.getOrDefault(Decision.REJECTED.name(), 0L);

        double roundedAvgScore = avgScore != null ? Math.round(avgScore * 100.0) / 100.0 : 0.0;

        log.debug("Analytics overview: total={}, approved={}, monitor={}, review={}, rejected={}, avgScore={}",
                total, approved, monitor, review, rejected, roundedAvgScore);

        return new AnalyticsOverviewResponse(
                total,
                approved,
                monitor,
                review,
                rejected,
                roundedAvgScore
        );
    }

    /**
     * Retrieves transaction counts grouped by qualitative risk level tiers.
     *
     * @return list of risk distribution metrics
     */
    public List<RiskDistributionResponse> getRiskDistributionAnalytics() {
        Map<String, Long> distributionMap = new HashMap<>();
        List<Object[]> groupedLevels = analyticsRepository.countByRiskLevelGrouped();
        for (Object[] row : groupedLevels) {
            if (row[0] != null) {
                distributionMap.put(row[0].toString(), ((Number) row[1]).longValue());
            }
        }

        List<RiskDistributionResponse> responseList = new ArrayList<>();
        for (RiskLevel level : RiskLevel.values()) {
            long count = distributionMap.getOrDefault(level.name(), 0L);
            responseList.add(new RiskDistributionResponse(level.name(), count));
        }

        return responseList;
    }

    /**
     * Retrieves top triggered fraud rules ordered by frequency.
     *
     * @return list of top rules
     */
    public List<TopRuleResponse> getTopRulesAnalytics() {
        List<Object[]> topRulesData = analyticsRepository.findTopTriggeredRules();
        List<TopRuleResponse> topRules = new ArrayList<>();

        for (Object[] row : topRulesData) {
            String ruleId = (String) row[0];
            String ruleName = (String) row[1];
            long count = ((Number) row[2]).longValue();
            topRules.add(new TopRuleResponse(ruleId, ruleName, count));
        }

        return topRules;
    }

    /**
     * Retrieves daily aggregated transaction volume and average risk score metrics.
     *
     * @return list of daily trend metrics
     */
    public List<DailyTrendResponse> getDailyTrendAnalytics() {
        List<Object[]> trendData = analyticsRepository.getDailyTrendMetrics();
        List<DailyTrendResponse> dailyTrends = new ArrayList<>();

        for (Object[] row : trendData) {
            String dateStr = row[0] != null ? row[0].toString() : "";
            long count = row[1] != null ? ((Number) row[1]).longValue() : 0L;
            double avgScore = row[2] != null ? Math.round(((Number) row[2]).doubleValue() * 100.0) / 100.0 : 0.0;
            dailyTrends.add(new DailyTrendResponse(dateStr, count, avgScore));
        }

        return dailyTrends;
    }
}
