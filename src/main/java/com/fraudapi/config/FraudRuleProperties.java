package com.fraudapi.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Centralized configuration properties for fraud rules, bound with prefix {@code fraud.rules}.
 */
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "fraud.rules")
public class FraudRuleProperties {

    /** Grouped configuration for high-amount rule checks. */
    private HighAmount highAmount = new HighAmount();

    /** Grouped configuration for velocity rule checks. */
    private Velocity velocity = new Velocity();

    /** Grouped configuration for round-amount rule checks. */
    private RoundAmount roundAmount = new RoundAmount();

    /** Grouped configuration for repeated-amount rule checks. */
    private RepeatedAmount repeatedAmount = new RepeatedAmount();

    /**
     * Configuration parameters for the High Amount fraud rule.
     * Only applies to DEBIT transactions.
     */
    @Getter
    @Setter
    public static class HighAmount {
        /** Threshold amount above which the rule triggers (default 50000.0). */
        private double threshold = 50000.0;

        /** Risk points contributed when high-amount rule triggers (default 45). */
        private int points = 45;
    }

    /**
     * Configuration parameters for the Velocity fraud rule.
     */
    @Getter
    @Setter
    public static class Velocity {
        /** Time window in minutes for frequency evaluation (default 5). */
        private int windowMinutes = 5;

        /** Maximum transactions permitted within the window before triggering (default 3). */
        private int maxTransactions = 3;

        /** Risk points contributed when velocity rule triggers (default 30). */
        private int points = 30;
    }

    /**
     * Configuration parameters for the Round Amount fraud rule.
     * Fraudsters often use perfectly round numbers to avoid detection.
     */
    @Getter
    @Setter
    public static class RoundAmount {
        /** Minimum amount above which the round-amount check applies (default 10000.0). */
        private double minimumAmount = 10000.0;

        /** Divisor used to determine if amount is "round" (default 1000). */
        private int roundingDivisor = 1000;

        /** Risk points contributed when round-amount rule triggers (default 20). */
        private int points = 20;
    }

    /**
     * Configuration parameters for the Repeated Amount fraud rule.
     * Repeated identical amounts in a short window indicates structuring / split-payment fraud.
     */
    @Getter
    @Setter
    public static class RepeatedAmount {
        /** Time window in minutes to look back for repeated amounts (default 5). */
        private int windowMinutes = 5;

        /** Minimum number of identical-amount transactions to trigger the rule (default 2). */
        private int minRepeatCount = 2;

        /** Risk points contributed when repeated-amount rule triggers (default 25). */
        private int points = 25;
    }
}
