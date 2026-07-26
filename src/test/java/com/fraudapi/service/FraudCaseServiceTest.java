package com.fraudapi.service;

import com.fraudapi.constants.FraudCasePriority;
import com.fraudapi.constants.FraudCaseStatus;
import com.fraudapi.dto.cases.FraudCaseDetailResponse;
import com.fraudapi.model.FraudCase;
import com.fraudapi.model.Transaction;
import com.fraudapi.model.User;
import com.fraudapi.repository.FraudCaseRepository;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link FraudCaseService}.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("FraudCaseService Unit Tests")
class FraudCaseServiceTest {

    @Mock
    private FraudCaseRepository fraudCaseRepository;

    @Mock
    private TransactionRiskEventRepository transactionRiskEventRepository;

    @Mock
    private FraudCaseAuditService fraudCaseAuditService;

    @InjectMocks
    private FraudCaseService fraudCaseService;

    private FraudCase testCase;

    @BeforeEach
    void setUp() {
        User user = User.builder().id(1L).name("Rahul Sharma").build();
        Transaction txn = Transaction.builder()
                .id(101L)
                .user(user)
                .amount(new BigDecimal("65000.00"))
                .transactionType("DEBIT")
                .status("FLAGGED")
                .riskScore(65)
                .riskLevel("HIGH")
                .decision("REVIEW")
                .build();

        testCase = FraudCase.builder()
                .id(1L)
                .transaction(txn)
                .status(FraudCaseStatus.OPEN)
                .priority(FraudCasePriority.HIGH)
                .openedAt(LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("Assigning open case -> updates assignedTo and sets status to ASSIGNED")
    void testAssignCase_Success() {
        when(fraudCaseRepository.findById(1L)).thenReturn(Optional.of(testCase));
        when(fraudCaseRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        FraudCaseDetailResponse response = fraudCaseService.assignCase(1L, "analyst1");

        assertNotNull(response);
        assertEquals("analyst1", response.assignedTo());
        assertEquals(FraudCaseStatus.ASSIGNED, response.status());
    }

    @Test
    @DisplayName("Assigning CLOSED case -> throws IllegalArgumentException")
    void testAssignClosedCase_ShouldThrowException() {
        testCase.setStatus(FraudCaseStatus.CLOSED);
        when(fraudCaseRepository.findById(1L)).thenReturn(Optional.of(testCase));

        assertThrows(IllegalArgumentException.class,
                () -> fraudCaseService.assignCase(1L, "analyst1"));
    }

    @Test
    @DisplayName("Resolving case with APPROVED status -> sets status APPROVED, closedAt timestamp, and resolution")
    void testResolveCase_Success() {
        when(fraudCaseRepository.findById(1L)).thenReturn(Optional.of(testCase));
        when(fraudCaseRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        FraudCaseDetailResponse response = fraudCaseService.resolveCase(1L, "Customer verified transaction", FraudCaseStatus.APPROVED);

        assertNotNull(response);
        assertEquals(FraudCaseStatus.APPROVED, response.status());
        assertEquals("Customer verified transaction", response.resolution());
        assertNotNull(response.closedAt());
    }
}
