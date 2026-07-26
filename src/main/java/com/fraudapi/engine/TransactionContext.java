package com.fraudapi.engine;

import com.fraudapi.model.Transaction;
import com.fraudapi.model.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * Context container passed to every {@code FraudRule} during evaluation.
 * Encapsulates domain objects and extensible attributes without changing rule signatures.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionContext {

    /** The user associated with the transaction attempt. */
    private User user;

    /** The transaction object or request representation under evaluation. */
    private Transaction transaction;

    /** Transaction user ID shorthand. */
    private Long userId;

    /** Transaction amount shorthand. */
    private BigDecimal amount;

    /** Transaction type shorthand (e.g. DEBIT, CREDIT). */
    private String transactionType;

    /**
     * Extensible key-value map supporting future fraud signals
     * (e.g. IP Address, Device Fingerprint, Merchant ID, Geolocation).
     */
    @Builder.Default
    private Map<String, Object> attributes = new HashMap<>();

    /**
     * Helper method to retrieve an attribute by key.
     *
     * @param key the attribute key
     * @param <T> the expected type
     * @return the attribute value cast to type T, or null if absent
     */
    @SuppressWarnings("unchecked")
    public <T> T getAttribute(String key) {
        return attributes != null ? (T) attributes.get(key) : null;
    }

    /**
     * Helper method to set an attribute.
     *
     * @param key   the attribute key
     * @param value the attribute value
     */
    public void setAttribute(String key, Object value) {
        if (this.attributes == null) {
            this.attributes = new HashMap<>();
        }
        this.attributes.put(key, value);
    }
}
