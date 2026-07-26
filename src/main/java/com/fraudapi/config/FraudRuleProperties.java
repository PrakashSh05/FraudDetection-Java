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

    /**
     * Configuration parameters for the High Amount fraud rule.
     */
    @Getter
    @Setter
    public static class HighAmount {
        /** Threshold amount above which the rule triggers (default 50000.0). */
        private double threshold = 50000.0;

        /** Risk points contributed when high-amount rule triggers (default 35). */
        private int points = 35;
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

        /** Risk points contributed when velocity rule triggers (default 25). */
        private int points = 25;
    }
}
