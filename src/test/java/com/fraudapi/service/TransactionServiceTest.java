package com.fraudapi.service;

import com.fraudapi.constants.TransactionStatus;
import com.fraudapi.constants.TransactionType;
import com.fraudapi.dto.TransactionRequest;
import com.fraudapi.dto.TransactionResponse;
import com.fraudapi.exception.InsufficientBalanceException;
import com.fraudapi.exception.UserNotFoundException;
import com.fraudapi.model.Transaction;
import com.fraudapi.model.User;
import com.fraudapi.repository.TransactionRepository;
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
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link TransactionService}.
 *
 * <p>All dependencies (repositories, fraud service) are mocked so these tests
 * run entirely in-memory with zero I/O — fast and deterministic.
 *
 * <p>Coverage targets:
 * <ul>
 *   <li>Happy path — approved debit</li>
 *   <li>High-amount fraud rule</li>
 *   <li>Velocity fraud rule</li>
 *   <li>Insufficient balance exception</li>
 *   <li>Balance NOT deducted on flagged transaction</li>
 *   <li>Credit transaction increases balance</li>
 *   <li>Unknown user throws UserNotFoundException</li>
 * </ul>
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("TransactionService Unit Tests")
class TransactionServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private FraudDetectionService fraudDetectionService;

    @InjectMocks
    private TransactionService transactionService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .name("Rahul Sharma")
                .email("rahul@example.com")
                .balance(new BigDecimal("100000.00"))
                .build();
    }

    // ── Test 1 ────────────────────────────────────────────────────────────── //

    @Test
    @DisplayName("1. Normal DEBIT transaction should be APPROVED and balance deducted")
    void testNormalDebitTransaction_ShouldBeApproved() {
        // Arrange
        TransactionRequest request = buildRequest(1L, "5000.00", TransactionType.DEBIT);

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(fraudDetectionService.checkFraud(eq(1L), any())).thenReturn(null); // no fraud
        when(transactionRepository.save(any())).thenAnswer(inv -> {
            Transaction t = inv.getArgument(0);
            t.setId(101L);
            t.setCreatedAt(LocalDateTime.now());
            return t;
        });

        // Act
        TransactionResponse response = transactionService.createTransaction(request);

        // Assert
        assertEquals(TransactionStatus.APPROVED, response.getStatus());
        assertNull(response.getFraudReason());
        assertEquals(new BigDecimal("95000.00"), response.getNewBalance());

        // Verify balance was saved
        verify(userRepository).save(argThat(u -> u.getBalance().compareTo(new BigDecimal("95000.00")) == 0));
    }

    // ── Test 2 ────────────────────────────────────────────────────────────── //

    @Test
    @DisplayName("2. Transaction exceeding ₹50,000 should be FLAGGED")
    void testHighAmountTransaction_ShouldBeFlagged() {
        // Arrange
        TransactionRequest request = buildRequest(1L, "75000.00", TransactionType.DEBIT);
        String fraudReason = "Amount ₹75000.00 exceeds the allowed limit of ₹50000.00";

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(fraudDetectionService.checkFraud(eq(1L), any())).thenReturn(fraudReason);
        when(transactionRepository.save(any())).thenAnswer(inv -> {
            Transaction t = inv.getArgument(0);
            t.setId(102L);
            t.setCreatedAt(LocalDateTime.now());
            return t;
        });

        // Act
        TransactionResponse response = transactionService.createTransaction(request);

        // Assert
        assertEquals(TransactionStatus.FLAGGED, response.getStatus());
        assertEquals(fraudReason, response.getFraudReason());
        assertNull(response.getNewBalance(), "Balance should NOT be returned for flagged transaction");
    }

    // ── Test 3 ────────────────────────────────────────────────────────────── //

    @Test
    @DisplayName("3. 4th transaction in 5 minutes should be FLAGGED (velocity rule)")
    void testVelocityCheck_ShouldFlagAfterMaxTransactions() {
        // Arrange
        TransactionRequest request = buildRequest(1L, "2000.00", TransactionType.DEBIT);
        String velocityReason = "Too many transactions: 3 in the last 5 minutes (limit: 3)";

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(fraudDetectionService.checkFraud(eq(1L), any())).thenReturn(velocityReason);
        when(transactionRepository.save(any())).thenAnswer(inv -> {
            Transaction t = inv.getArgument(0);
            t.setId(103L);
            t.setCreatedAt(LocalDateTime.now());
            return t;
        });

        // Act
        TransactionResponse response = transactionService.createTransaction(request);

        // Assert
        assertEquals(TransactionStatus.FLAGGED, response.getStatus());
        assertTrue(response.getFraudReason().contains("Too many transactions"));
    }

    // ── Test 4 ────────────────────────────────────────────────────────────── //

    @Test
    @DisplayName("4. DEBIT exceeding user balance should throw InsufficientBalanceException")
    void testInsufficientBalance_ShouldThrowException() {
        // User only has ₹100,000 — trying to withdraw ₹150,000
        TransactionRequest request = buildRequest(1L, "150000.00", TransactionType.DEBIT);

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(fraudDetectionService.checkFraud(eq(1L), any())).thenReturn(null); // passes fraud check

        // Act & Assert
        InsufficientBalanceException ex = assertThrows(
                InsufficientBalanceException.class,
                () -> transactionService.createTransaction(request));

        assertTrue(ex.getMessage().contains("Insufficient balance"));

        // Balance must NOT have been touched
        verify(userRepository, never()).save(any());
    }

    // ── Test 5 ────────────────────────────────────────────────────────────── //

    @Test
    @DisplayName("5. FLAGGED transaction should NOT deduct balance")
    void testFlaggedTransaction_ShouldNotDeductBalance() {
        // Arrange
        BigDecimal originalBalance = testUser.getBalance();
        TransactionRequest request = buildRequest(1L, "60000.00", TransactionType.DEBIT);

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(fraudDetectionService.checkFraud(eq(1L), any()))
                .thenReturn("Amount exceeds limit");
        when(transactionRepository.save(any())).thenAnswer(inv -> {
            Transaction t = inv.getArgument(0);
            t.setId(104L);
            t.setCreatedAt(LocalDateTime.now());
            return t;
        });

        // Act
        TransactionResponse response = transactionService.createTransaction(request);

        // Assert — balance on user object unchanged
        assertEquals(originalBalance, testUser.getBalance());
        // userRepository.save(user) should NOT be called
        verify(userRepository, never()).save(any(User.class));
        assertEquals(TransactionStatus.FLAGGED, response.getStatus());
    }

    // ── Bonus Test 6 ──────────────────────────────────────────────────────── //

    @Test
    @DisplayName("6. CREDIT transaction should INCREASE balance")
    void testCreditTransaction_ShouldIncreaseBalance() {
        // Arrange
        TransactionRequest request = buildRequest(1L, "10000.00", TransactionType.CREDIT);

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(fraudDetectionService.checkFraud(eq(1L), any())).thenReturn(null);
        when(transactionRepository.save(any())).thenAnswer(inv -> {
            Transaction t = inv.getArgument(0);
            t.setId(105L);
            t.setCreatedAt(LocalDateTime.now());
            return t;
        });

        // Act
        TransactionResponse response = transactionService.createTransaction(request);

        // Assert
        assertEquals(TransactionStatus.APPROVED, response.getStatus());
        assertEquals(new BigDecimal("110000.00"), response.getNewBalance());
        verify(userRepository).save(argThat(u -> u.getBalance().compareTo(new BigDecimal("110000.00")) == 0));
    }

    // ── Bonus Test 7 ──────────────────────────────────────────────────────── //

    @Test
    @DisplayName("7. Transaction for non-existent user should throw UserNotFoundException")
    void testUnknownUser_ShouldThrowUserNotFoundException() {
        // Arrange
        TransactionRequest request = buildRequest(999L, "5000.00", TransactionType.DEBIT);
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UserNotFoundException.class,
                () -> transactionService.createTransaction(request));

        verify(fraudDetectionService, never()).checkFraud(any(), any());
        verify(transactionRepository, never()).save(any());
    }

    // ── Helpers ─────────────────────────────────────────────────────────── //

    private TransactionRequest buildRequest(Long userId, String amount, String type) {
        TransactionRequest req = new TransactionRequest();
        req.setUserId(userId);
        req.setAmount(new BigDecimal(amount));
        req.setTransactionType(type);
        return req;
    }
}
