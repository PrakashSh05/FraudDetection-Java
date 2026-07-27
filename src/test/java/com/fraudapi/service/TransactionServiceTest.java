package com.fraudapi.service;

import com.fraudapi.constants.Decision;
import com.fraudapi.constants.FraudCaseAuditEventType;
import com.fraudapi.constants.FraudCasePriority;
import com.fraudapi.constants.FraudCaseStatus;
import com.fraudapi.constants.RiskLevel;
import com.fraudapi.constants.RuleSeverity;
import com.fraudapi.constants.TransactionStatus;
import com.fraudapi.constants.TransactionType;
import com.fraudapi.dto.FraudDecision;
import com.fraudapi.dto.TransactionRequest;
import com.fraudapi.dto.TransactionResponse;
import com.fraudapi.dto.TriggeredRule;
import com.fraudapi.exception.InsufficientBalanceException;
import com.fraudapi.exception.UserNotFoundException;
import com.fraudapi.model.Transaction;
import com.fraudapi.model.User;
import com.fraudapi.repository.FraudCaseRepository;
import com.fraudapi.repository.TransactionRepository;
import com.fraudapi.repository.TransactionRiskEventRepository;
import com.fraudapi.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link TransactionService} integrated with {@link TransactionRiskService},
 * {@link TransactionRiskEventRepository}, {@link FraudCaseRepository}, and {@link FraudCaseAuditService}.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("TransactionService Unit Tests")
class TransactionServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private TransactionRiskEventRepository transactionRiskEventRepository;

    @Mock
    private FraudCaseRepository fraudCaseRepository;

    @Mock
    private FraudCaseAuditService fraudCaseAuditService;

    @Mock
    private TransactionRiskService transactionRiskService;

    @InjectMocks
    private TransactionService transactionService;

    private User testUser;
    private FraudDecision approvedDecision;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .name("Rahul Sharma")
                .email("rahul@example.com")
                .balance(new BigDecimal("100000.00"))
                .build();

        approvedDecision = FraudDecision.builder()
                .riskScore(0)
                .riskLevel(RiskLevel.LOW)
                .decision(Decision.APPROVED)
                .summary("No fraud indicators detected.")
                .processingTimeMs(2)
                .triggeredRules(Collections.emptyList())
                .build();
    }

    @Test
    @DisplayName("1. Normal DEBIT transaction should be APPROVED and balance deducted")
    void testNormalDebitTransaction_ShouldBeApproved() {
        TransactionRequest request = buildRequest(1L, "5000.00", TransactionType.DEBIT);

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(transactionRiskService.evaluateTransactionRisk(any())).thenReturn(approvedDecision);
        when(transactionRepository.save(any())).thenAnswer(inv -> {
            Transaction t = inv.getArgument(0);
            t.setId(101L);
            t.setCreatedAt(LocalDateTime.now());
            return t;
        });

        TransactionResponse response = transactionService.createTransaction(request);

        assertEquals(TransactionStatus.APPROVED, response.getStatus());
        assertNull(response.getFraudReason());
        assertEquals(new BigDecimal("95000.00"), response.getNewBalance());

        verify(userRepository).save(argThat(u -> u.getBalance().compareTo(new BigDecimal("95000.00")) == 0));
        verify(transactionRiskEventRepository, never()).saveAll(any());
        verify(fraudCaseRepository, never()).save(any());
    }

    @Test
    @DisplayName("2. Transaction exceeding threshold should be FLAGGED and risk events saved")
    void testHighAmountTransaction_ShouldBeFlagged() {
        TransactionRequest request = buildRequest(1L, "75000.00", TransactionType.DEBIT);

        TriggeredRule rule = TriggeredRule.builder()
                .ruleId("RULE-001")
                .ruleName("HIGH_AMOUNT")
                .category("TRANSACTION")
                .severity(RuleSeverity.HIGH)
                .points(35)
                .description("Transaction amount exceeded configured threshold. Actual: 75000.00, Threshold: 50000.00")
                .build();

        FraudDecision rejectedDecision = FraudDecision.builder()
                .riskScore(35)
                .riskLevel(RiskLevel.MEDIUM)
                .decision(Decision.REJECTED)
                .summary("1 fraud indicator detected.")
                .processingTimeMs(5)
                .triggeredRules(List.of(rule))
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(transactionRiskService.evaluateTransactionRisk(any())).thenReturn(rejectedDecision);
        when(transactionRepository.save(any())).thenAnswer(inv -> {
            Transaction t = inv.getArgument(0);
            t.setId(102L);
            t.setCreatedAt(LocalDateTime.now());
            return t;
        });

        TransactionResponse response = transactionService.createTransaction(request);

        assertEquals(TransactionStatus.FLAGGED, response.getStatus());
        assertTrue(response.getFraudReason().contains("exceeded"));
        assertNull(response.getNewBalance());
        verify(transactionRiskEventRepository).saveAll(argThat(list -> ((List<?>) list).size() == 1));
        verify(fraudCaseRepository, never()).save(any());
    }

    @Test
    @DisplayName("3. Decision.REVIEW -> automatically creates a FraudCase and logs CASE_CREATED audit event")
    void testDecisionReview_ShouldCreateFraudCaseAndAuditLog() {
        TransactionRequest request = buildRequest(1L, "65000.00", TransactionType.DEBIT);

        TriggeredRule rule = TriggeredRule.builder()
                .ruleId("RULE-001")
                .ruleName("HIGH_AMOUNT")
                .category("TRANSACTION")
                .severity(RuleSeverity.HIGH)
                .points(65)
                .description("High amount review required")
                .build();

        FraudDecision reviewDecision = FraudDecision.builder()
                .riskScore(65)
                .riskLevel(RiskLevel.HIGH)
                .decision(Decision.REVIEW)
                .summary("1 fraud indicator detected.")
                .processingTimeMs(6)
                .triggeredRules(List.of(rule))
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(transactionRiskService.evaluateTransactionRisk(any())).thenReturn(reviewDecision);
        when(transactionRepository.save(any())).thenAnswer(inv -> {
            Transaction t = inv.getArgument(0);
            t.setId(103L);
            t.setCreatedAt(LocalDateTime.now());
            return t;
        });
        when(fraudCaseRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        TransactionResponse response = transactionService.createTransaction(request);

        assertEquals(TransactionStatus.FLAGGED, response.getStatus());
        verify(fraudCaseRepository).save(argThat(fc ->
                fc.getStatus() == FraudCaseStatus.OPEN &&
                fc.getPriority() == FraudCasePriority.HIGH
        ));
        verify(fraudCaseAuditService).recordAudit(any(), eq(FraudCaseAuditEventType.CASE_CREATED), isNull(), eq("OPEN"), eq("SYSTEM"));
    }

    @Test
    @DisplayName("4. DEBIT exceeding user balance should throw InsufficientBalanceException")
    void testInsufficientBalance_ShouldThrowException() {
        TransactionRequest request = buildRequest(1L, "150000.00", TransactionType.DEBIT);

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(transactionRiskService.evaluateTransactionRisk(any())).thenReturn(approvedDecision);

        InsufficientBalanceException ex = assertThrows(
                InsufficientBalanceException.class,
                () -> transactionService.createTransaction(request));

        assertTrue(ex.getMessage().contains("Insufficient balance"));
        verify(userRepository, never()).save(any());
        verify(transactionRiskEventRepository, never()).saveAll(any());
        verify(fraudCaseRepository, never()).save(any());
    }

    private TransactionRequest buildRequest(Long userId, String amount, String type) {
        TransactionRequest req = new TransactionRequest();
        req.setUserId(userId);
        req.setAmount(new BigDecimal(amount));
        req.setTransactionType(type);
        return req;
    }
}
