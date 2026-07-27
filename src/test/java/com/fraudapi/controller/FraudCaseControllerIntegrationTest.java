package com.fraudapi.controller;

import com.fraudapi.constants.FraudCasePriority;
import com.fraudapi.constants.FraudCaseStatus;
import com.fraudapi.dto.cases.*;
import com.fraudapi.exception.GlobalExceptionHandler;
import com.fraudapi.service.FraudCaseAuditService;
import com.fraudapi.service.FraudCaseService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for {@link FraudCaseController} verifying REST workflow operations and validation error states.
 */
@WebMvcTest(FraudCaseController.class)
@Import(GlobalExceptionHandler.class)
@DisplayName("FraudCaseController Integration Tests")
class FraudCaseControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private FraudCaseService fraudCaseService;

    @MockBean
    private FraudCaseAuditService fraudCaseAuditService;

    @Test
    @DisplayName("GET /api/cases -> returns HTTP 200 with paginated queue")
    void testGetCaseQueue_Returns200() throws Exception {
        FraudCaseSummaryResponse summary = new FraudCaseSummaryResponse(
                1L, 101L, 1L, new BigDecimal("65000.00"), "DEBIT",
                65, "HIGH", FraudCaseStatus.OPEN, FraudCasePriority.HIGH,
                null, LocalDateTime.now(), LocalDateTime.now()
        );

        when(fraudCaseService.getCaseQueue(any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(summary), PageRequest.of(0, 20), 1));

        mockMvc.perform(get("/api/cases")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.data.content[0].caseId").value(1));
    }

    @Test
    @DisplayName("GET /api/cases/summary -> returns HTTP 200 with queue breakdown")
    void testGetQueueSummary_Returns200() throws Exception {
        FraudCaseQueueSummaryResponse summary = new FraudCaseQueueSummaryResponse(
                48, 11, 8, 6, 12, 5, 2, 4, 3, 9, 15, 21
        );
        when(fraudCaseService.getQueueSummary()).thenReturn(summary);

        mockMvc.perform(get("/api/cases/summary")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.data.totalCases").value(48));
    }

    @Test
    @DisplayName("PATCH /api/cases/1/assign -> returns HTTP 200 on successful assignment")
    void testAssignCase_Returns200() throws Exception {
        AssignCaseRequest request = new AssignCaseRequest("john.doe");
        FraudCaseDetailResponse response = new FraudCaseDetailResponse(
                1L, FraudCaseStatus.ASSIGNED, FraudCasePriority.HIGH, "john.doe",
                LocalDateTime.now(), null, null, null, LocalDateTime.now(), LocalDateTime.now(),
                null, null, List.of()
        );

        when(fraudCaseService.assignCase(eq(1L), eq("john.doe"))).thenReturn(response);

        mockMvc.perform(patch("/api/cases/1/assign")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.data.assignedTo").value("john.doe"));
    }

    @Test
    @DisplayName("PATCH /api/cases/1/status with invalid transition -> returns HTTP 400 Bad Request")
    void testUpdateStatus_InvalidTransition_Returns400() throws Exception {
        UpdateCaseStatusRequest request = new UpdateCaseStatusRequest(FraudCaseStatus.OPEN);

        when(fraudCaseService.updateCaseStatus(eq(1L), eq(FraudCaseStatus.OPEN)))
                .thenThrow(new IllegalArgumentException("Cannot transition out of CLOSED status"));

        mockMvc.perform(patch("/api/cases/1/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("ERROR"))
                .andExpect(jsonPath("$.message").value("Cannot transition out of CLOSED status"));
    }
}
