package com.fraudapi.service;

import com.fraudapi.dto.analytics.AnalyticsOverviewResponse;
import com.fraudapi.dto.analytics.DailyTrendResponse;
import com.fraudapi.dto.analytics.RiskDistributionResponse;
import com.fraudapi.dto.analytics.TopRuleResponse;
import com.fraudapi.repository.AnalyticsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link AnalyticsService}.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("AnalyticsService Unit Tests")
class AnalyticsServiceTest {

    @Mock
    private AnalyticsRepository analyticsRepository;

    @InjectMocks
    private AnalyticsService analyticsService;

    @BeforeEach
    void setUp() {
    }

    @Test
    @DisplayName("Overview analytics should aggregate total volume, decision counts, and average risk score")
    void testGetOverviewAnalytics() {
        when(analyticsRepository.count()).thenReturn(100L);
        when(analyticsRepository.getAverageRiskScore()).thenReturn(28.45);
        when(analyticsRepository.countByDecisionGrouped()).thenReturn(List.<Object[]>of(
                new Object[]{"APPROVED", 70L},
                new Object[]{"MONITOR", 15L},
                new Object[]{"REVIEW", 10L},
                new Object[]{"REJECTED", 5L}
        ));

        AnalyticsOverviewResponse overview = analyticsService.getOverviewAnalytics();

        assertNotNull(overview);
        assertEquals(100L, overview.totalTransactions());
        assertEquals(70L, overview.approvedTransactions());
        assertEquals(15L, overview.monitorTransactions());
        assertEquals(10L, overview.reviewTransactions());
        assertEquals(5L, overview.rejectedTransactions());
        assertEquals(28.45, overview.averageRiskScore());
    }

    @Test
    @DisplayName("Risk distribution should contain counts for all 4 risk tiers")
    void testGetRiskDistributionAnalytics() {
        when(analyticsRepository.countByRiskLevelGrouped()).thenReturn(List.<Object[]>of(
                new Object[]{"LOW", 60L},
                new Object[]{"MEDIUM", 25L},
                new Object[]{"HIGH", 10L},
                new Object[]{"CRITICAL", 5L}
        ));

        List<RiskDistributionResponse> distribution = analyticsService.getRiskDistributionAnalytics();

        assertNotNull(distribution);
        assertEquals(4, distribution.size());
        assertEquals("LOW", distribution.get(0).riskLevel());
        assertEquals(60L, distribution.get(0).count());
    }

    @Test
    @DisplayName("Top rules should map rule trigger frequencies")
    void testGetTopRulesAnalytics() {
        when(analyticsRepository.findTopTriggeredRules()).thenReturn(List.<Object[]>of(
                new Object[]{"RULE-001", "HIGH_AMOUNT", 12L},
                new Object[]{"RULE-002", "VELOCITY_EXCEEDED", 8L}
        ));

        List<TopRuleResponse> topRules = analyticsService.getTopRulesAnalytics();

        assertNotNull(topRules);
        assertEquals(2, topRules.size());
        assertEquals("RULE-001", topRules.get(0).ruleId());
        assertEquals(12L, topRules.get(0).triggerCount());
    }

    @Test
    @DisplayName("Daily trends should map date, count, and average score")
    void testGetDailyTrendAnalytics() {
        when(analyticsRepository.getDailyTrendMetrics()).thenReturn(List.<Object[]>of(
                new Object[]{"2026-07-26", 15L, 32.5}
        ));

        List<DailyTrendResponse> trends = analyticsService.getDailyTrendAnalytics();

        assertNotNull(trends);
        assertEquals(1, trends.size());
        assertEquals("2026-07-26", trends.get(0).date());
        assertEquals(15L, trends.get(0).transactions());
        assertEquals(32.5, trends.get(0).averageRiskScore());
    }
}
