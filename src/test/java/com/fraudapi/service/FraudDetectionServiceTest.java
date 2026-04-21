package com.fraudapi.service;

import com.fraudapi.model.Transaction;
import com.fraudapi.model.User;
import com.fraudapi.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link FraudDetectionService}.
 * Tests each rule independently with mocked repository.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("FraudDetectionService Unit Tests")
class FraudDetectionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private FraudDetectionService fraudDetectionService;

    @BeforeEach
    void setUp() {
        // Inject @Value fields manually (they're not auto-injected in unit tests)
        ReflectionTestUtils.setField(fraudDetectionService, "highAmountThreshold", 50000.0);
        ReflectionTestUtils.setField(fraudDetectionService, "maxTransactionsPerWindow", 3);
        ReflectionTestUtils.setField(fraudDetectionService, "timeWindowMinutes", 5);
    }

    @Test
    @DisplayName("Transaction under ₹50,000 with no recent history → NO FRAUD")
    void testCleanTransaction_ShouldReturnNull() {
        when(transactionRepository.countRecentTransactions(anyLong(), any())).thenReturn(0L);

        String result = fraudDetectionService.checkFraud(1L, new BigDecimal("5000.00"));

        assertNull(result);
    }

    @Test
    @DisplayName("Transaction exactly at ₹50,000 → NOT flagged (boundary value)")
    void testAmountAtExactThreshold_ShouldNotFlag() {
        when(transactionRepository.countRecentTransactions(anyLong(), any())).thenReturn(0L);

        String result = fraudDetectionService.checkFraud(1L, new BigDecimal("50000.00"));

        assertNull(result, "Exactly at threshold should be allowed");
    }

    @Test
    @DisplayName("Transaction above ₹50,000 → FLAGGED by high-amount rule")
    void testHighAmount_ShouldReturnFraudReason() {
        String result = fraudDetectionService.checkFraud(1L, new BigDecimal("50000.01"));

        assertNotNull(result);
        assertTrue(result.contains("exceeds"));
        // Velocity check should NOT be called when high-amount triggers first
        verify(transactionRepository, never()).countRecentTransactions(anyLong(), any());
    }

    @Test
    @DisplayName("3 recent transactions (at limit) → NOT flagged")
    void testVelocityAtLimit_ShouldNotFlag() {
        when(transactionRepository.countRecentTransactions(anyLong(), any())).thenReturn(2L);

        String result = fraudDetectionService.checkFraud(1L, new BigDecimal("1000.00"));

        assertNull(result);
    }

    @Test
    @DisplayName("4th transaction in 5 minutes → FLAGGED by velocity rule")
    void testVelocityExceeded_ShouldReturnFraudReason() {
        when(transactionRepository.countRecentTransactions(anyLong(), any())).thenReturn(3L);

        String result = fraudDetectionService.checkFraud(1L, new BigDecimal("1000.00"));

        assertNotNull(result);
        assertTrue(result.contains("Too many transactions"));
    }
}
