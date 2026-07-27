package com.fraudapi.config;

import com.fraudapi.constants.FraudCaseAuditEventType;
import com.fraudapi.constants.FraudCasePriority;
import com.fraudapi.constants.FraudCaseStatus;
import com.fraudapi.constants.TransactionStatus;
import com.fraudapi.constants.TransactionType;
import com.fraudapi.model.*;
import com.fraudapi.repository.*;
import com.fraudapi.service.FraudCaseAuditService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Bootstraps realistic demonstration data for users, transactions, risk events,
 * fraud cases, and audit logs if the database is currently empty.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;
    private final TransactionRiskEventRepository transactionRiskEventRepository;
    private final FraudCaseRepository fraudCaseRepository;
    private final FraudCaseAuditService fraudCaseAuditService;

    @Override
    @Transactional
    public void run(String... args) {
        if (userRepository.count() > 0) {
            log.info("Database already contains seed data. Skipping DataInitializer.");
            return;
        }

        log.info("Database is empty. Populating realistic demonstration seed data...");

        // 1. Create Users
        User u1 = User.builder().name("Rahul Sharma").email("rahul.sharma@example.com").balance(new BigDecimal("250000.00")).build();
        User u2 = User.builder().name("Priya Patel").email("priya.patel@example.com").balance(new BigDecimal("180000.00")).build();
        User u3 = User.builder().name("Amit Kumar").email("amit.kumar@example.com").balance(new BigDecimal("95000.00")).build();
        User u4 = User.builder().name("Sneha Reddy").email("sneha.reddy@example.com").balance(new BigDecimal("320000.00")).build();
        User u5 = User.builder().name("Vikram Singh").email("vikram.singh@example.com").balance(new BigDecimal("500000.00")).build();

        List<User> users = userRepository.saveAll(List.of(u1, u2, u3, u4, u5));
        log.info("Created {} demo users.", users.size());

        // 2. Generate Transactions & Risk Telemetry
        LocalDateTime now = LocalDateTime.now();
        Random random = new Random(42); // deterministic seed

        List<Transaction> transactionsToSave = new ArrayList<>();
        List<TransactionRiskEvent> riskEventsToSave = new ArrayList<>();
        List<FraudCase> casesToSave = new ArrayList<>();

        for (int i = 1; i <= 50; i++) {
            User user = users.get(random.nextInt(users.size()));
            LocalDateTime timestamp = now.minusDays(random.nextInt(14)).minusHours(random.nextInt(24)).minusMinutes(random.nextInt(60));

            boolean isHighAmount = (i % 7 == 0);
            boolean isVelocity = (i % 5 == 0);
            boolean isCritical = (i % 11 == 0);

            BigDecimal amount;
            int riskScore = 0;
            String riskLevel = "LOW";
            String decision = "APPROVED";
            String status = TransactionStatus.APPROVED;
            String fraudReason = null;

            List<TriggeredRuleData> triggeredRules = new ArrayList<>();

            if (isCritical) {
                amount = new BigDecimal("125000.00");
                riskScore = 85;
                riskLevel = "CRITICAL";
                decision = "REJECTED";
                status = TransactionStatus.FLAGGED;
                fraudReason = "Multiple high-risk fraud rules triggered. Amount: 125000.00";
                triggeredRules.add(new TriggeredRuleData("RULE-001", "HIGH_AMOUNT", "TRANSACTION", "HIGH", 35, "Transaction amount 125000.00 exceeded threshold 50000.00"));
                triggeredRules.add(new TriggeredRuleData("RULE-002", "VELOCITY_EXCEEDED", "VELOCITY", "MEDIUM", 25, "Velocity limit exceeded: 4 txns in 5 mins"));
                triggeredRules.add(new TriggeredRuleData("RULE-003", "SUSPICIOUS_PATTERN", "PATTERN", "CRITICAL", 25, "Suspicious rapid debit pattern detected"));
            } else if (isHighAmount) {
                amount = new BigDecimal("68000.00");
                riskScore = 65;
                riskLevel = "HIGH";
                decision = "REVIEW";
                status = TransactionStatus.FLAGGED;
                fraudReason = "High amount transaction requiring manual review. Amount: 68000.00";
                triggeredRules.add(new TriggeredRuleData("RULE-001", "HIGH_AMOUNT", "TRANSACTION", "HIGH", 35, "Transaction amount 68000.00 exceeded threshold 50000.00"));
            } else if (isVelocity) {
                amount = new BigDecimal("15000.00");
                riskScore = 25;
                riskLevel = "MEDIUM";
                decision = "MONITOR";
                status = TransactionStatus.APPROVED;
                triggeredRules.add(new TriggeredRuleData("RULE-002", "VELOCITY_EXCEEDED", "VELOCITY", "MEDIUM", 25, "Velocity limit exceeded: 3 txns in 5 mins"));
            } else {
                amount = new BigDecimal(500 + random.nextInt(4500));
                riskScore = random.nextInt(15);
                riskLevel = "LOW";
                decision = "APPROVED";
                status = TransactionStatus.APPROVED;
            }

            Transaction txn = Transaction.builder()
                    .user(user)
                    .amount(amount)
                    .transactionType(i % 3 == 0 ? TransactionType.CREDIT : TransactionType.DEBIT)
                    .status(status)
                    .fraudReason(fraudReason)
                    .riskScore(riskScore)
                    .riskLevel(riskLevel)
                    .decision(decision)
                    .processingTimeMs(2L + random.nextInt(8))
                    .evaluationTimestamp(timestamp)
                    .createdAt(timestamp)
                    .build();

            Transaction savedTxn = transactionRepository.save(txn);
            transactionsToSave.add(savedTxn);

            for (TriggeredRuleData tr : triggeredRules) {
                TransactionRiskEvent event = TransactionRiskEvent.builder()
                        .transaction(savedTxn)
                        .ruleId(tr.ruleId)
                        .ruleName(tr.ruleName)
                        .category(tr.category)
                        .severity(tr.severity)
                        .points(tr.points)
                        .description(tr.description)
                        .createdAt(timestamp)
                        .build();
                riskEventsToSave.add(event);
            }

            // Create Fraud Case for REVIEW or REJECTED / FLAGGED items
            if ("REVIEW".equals(decision) || "REJECTED".equals(decision)) {
                FraudCaseStatus caseStatus = (i % 2 == 0) ? FraudCaseStatus.OPEN : FraudCaseStatus.ASSIGNED;
                String assignedTo = (caseStatus == FraudCaseStatus.ASSIGNED) ? "analyst1" : null;

                FraudCase fc = FraudCase.builder()
                        .transaction(savedTxn)
                        .status(caseStatus)
                        .priority(riskScore >= 80 ? FraudCasePriority.CRITICAL : FraudCasePriority.HIGH)
                        .assignedTo(assignedTo)
                        .openedAt(timestamp)
                        .createdAt(timestamp)
                        .build();

                FraudCase savedCase = fraudCaseRepository.save(fc);
                fraudCaseAuditService.recordAudit(savedCase, FraudCaseAuditEventType.CASE_CREATED, null, "OPEN", "SYSTEM");

                if (caseStatus == FraudCaseStatus.ASSIGNED) {
                    fraudCaseAuditService.recordAudit(savedCase, FraudCaseAuditEventType.CASE_ASSIGNED, null, "analyst1", "SYSTEM");
                }
            }
        }

        transactionRiskEventRepository.saveAll(riskEventsToSave);
        log.info("Seeded {} transactions, {} risk events, and {} fraud cases.",
                transactionsToSave.size(), riskEventsToSave.size(), fraudCaseRepository.count());
    }

    private static class TriggeredRuleData {
        String ruleId;
        String ruleName;
        String category;
        String severity;
        int points;
        String description;

        TriggeredRuleData(String ruleId, String ruleName, String category, String severity, int points, String description) {
            this.ruleId = ruleId;
            this.ruleName = ruleName;
            this.category = category;
            this.severity = severity;
            this.points = points;
            this.description = description;
        }
    }
}
