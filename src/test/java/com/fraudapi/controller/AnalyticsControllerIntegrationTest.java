package com.fraudapi.controller;

import com.fraudapi.dto.analytics.*;
import com.fraudapi.service.AnalyticsService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for {@link AnalyticsController} verifying REST contracts and payload envelopes.
 */
@WebMvcTest(AnalyticsController.class)
@DisplayName("AnalyticsController Integration Tests")
class AnalyticsControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AnalyticsService analyticsService;

    @Test
    @DisplayName("GET /api/analytics/overview -> returns HTTP 200 with summary metrics")
    void testGetOverview_Returns200() throws Exception {
        // AnalyticsOverviewResponse record: (totalTransactions, approvedTransactions, monitorTransactions,
        //                                    reviewTransactions, rejectedTransactions, averageRiskScore)
        AnalyticsOverviewResponse overview = new AnalyticsOverviewResponse(100L, 85L, 5L, 6L, 4L, 14.5);
        when(analyticsService.getOverviewAnalytics()).thenReturn(overview);

        mockMvc.perform(get("/api/analytics/overview")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data.totalTransactions").value(100))
                .andExpect(jsonPath("$.data.approvedTransactions").value(85));
    }

    @Test
    @DisplayName("GET /api/analytics/risk-distribution -> returns HTTP 200 with distribution list")
    void testGetRiskDistribution_Returns200() throws Exception {
        // RiskDistributionResponse record: (riskLevel, count)
        RiskDistributionResponse dist = new RiskDistributionResponse("LOW", 85L);
        when(analyticsService.getRiskDistributionAnalytics()).thenReturn(List.of(dist));

        mockMvc.perform(get("/api/analytics/risk-distribution")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data[0].riskLevel").value("LOW"));
    }

    @Test
    @DisplayName("GET /api/analytics/top-rules -> returns HTTP 200 with top fired rules")
    void testGetTopRules_Returns200() throws Exception {
        // TopRuleResponse record: (ruleId, ruleName, triggerCount)
        TopRuleResponse topRule = new TopRuleResponse("RULE-001", "HIGH_AMOUNT", 15L);
        when(analyticsService.getTopRulesAnalytics()).thenReturn(List.of(topRule));

        mockMvc.perform(get("/api/analytics/top-rules")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data[0].ruleId").value("RULE-001"));
    }
}
