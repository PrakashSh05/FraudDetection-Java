package com.fraudapi.controller;

import com.fraudapi.dto.investigation.InvestigationResponse;
import com.fraudapi.dto.investigation.RiskEvaluationDetails;
import com.fraudapi.dto.investigation.TransactionDetails;
import com.fraudapi.exception.GlobalExceptionHandler;
import com.fraudapi.exception.TransactionNotFoundException;
import com.fraudapi.service.InvestigationService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for {@link InvestigationController} verifying 200 OK and 404 responses.
 */
@WebMvcTest(InvestigationController.class)
@Import(GlobalExceptionHandler.class)
@DisplayName("InvestigationController Integration Tests")
class InvestigationControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private InvestigationService investigationService;

    @Test
    @DisplayName("GET /api/investigation/transaction/101 -> returns HTTP 200 with investigation report")
    void testGetInvestigationReport_Success_Returns200() throws Exception {
        TransactionDetails txn = new TransactionDetails(101L, 1L, new BigDecimal("75000.00"), "DEBIT", "FLAGGED", LocalDateTime.now());
        RiskEvaluationDetails eval = new RiskEvaluationDetails(35, "MEDIUM", "REJECTED", 5L, LocalDateTime.now());
        InvestigationResponse response = new InvestigationResponse(txn, eval, List.of());

        when(investigationService.getInvestigationReport(101L)).thenReturn(response);

        mockMvc.perform(get("/api/investigation/transaction/101")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data.transaction.transactionId").value(101));
    }

    @Test
    @DisplayName("GET /api/investigation/transaction/999 (missing) -> returns HTTP 404 Not Found")
    void testGetInvestigationReport_NotFound_Returns404() throws Exception {
        when(investigationService.getInvestigationReport(999L))
                .thenThrow(new TransactionNotFoundException("Transaction not found with ID: 999"));

        mockMvc.perform(get("/api/investigation/transaction/999")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.message").value("Transaction not found with ID: 999"));
    }
}
