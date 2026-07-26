package com.fraudapi.service;

import com.fraudapi.dto.investigation.InvestigationResponse;
import com.fraudapi.exception.TransactionNotFoundException;
import com.fraudapi.model.Transaction;
import com.fraudapi.model.TransactionRiskEvent;
import com.fraudapi.model.User;
import com.fraudapi.repository.TransactionRepository;
import com.fraudapi.repository.TransactionRiskEventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link InvestigationService}.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("InvestigationService Unit Tests")
class InvestigationServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private TransactionRiskEventRepository transactionRiskEventRepository;

    @InjectMocks
    private InvestigationService investigationService;

    private Transaction testTransaction;
    private TransactionRiskEvent testRiskEvent;

    @BeforeEach
    void setUp() {
        User user = User.builder().id(1L).name("Rahul Sharma").build();

        testTransaction = Transaction.builder()
                .id(101L)
                .user(user)
                .amount(new BigDecimal("75000.00"))
                .transactionType("DEBIT")
                .status("FLAGGED")
                .riskScore(35)
                .riskLevel("MEDIUM")
                .decision("REJECTED")
                .processingTimeMs(5L)
                .evaluationTimestamp(LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .build();

        testRiskEvent = TransactionRiskEvent.builder()
                .id(1L)
                .transaction(testTransaction)
                .ruleId("RULE-001")
                .ruleName("HIGH_AMOUNT")
                .category("TRANSACTION")
                .severity("HIGH")
                .points(35)
                .description("Transaction amount exceeded threshold.")
                .build();
    }

    @Test
    @DisplayName("Valid transaction ID -> returns full investigation report with triggered rules")
    void testGetInvestigationReport_Success() {
        when(transactionRepository.findById(101L)).thenReturn(Optional.of(testTransaction));
        when(transactionRiskEventRepository.findByTransactionId(101L)).thenReturn(List.of(testRiskEvent));

        InvestigationResponse report = investigationService.getInvestigationReport(101L);

        assertNotNull(report);
        assertEquals(101L, report.transaction().transactionId());
        assertEquals(35, report.evaluation().riskScore());
        assertEquals("REJECTED", report.evaluation().decision());
        assertEquals(1, report.triggeredRules().size());
        assertEquals("RULE-001", report.triggeredRules().get(0).ruleId());
    }

    @Test
    @DisplayName("Non-existent transaction ID -> throws TransactionNotFoundException")
    void testGetInvestigationReport_NotFound() {
        when(transactionRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(TransactionNotFoundException.class,
                () -> investigationService.getInvestigationReport(999L));
    }
}
