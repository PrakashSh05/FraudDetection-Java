package com.fraudapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fraudapi.dto.CreateUserRequest;
import com.fraudapi.dto.TransactionRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the full transaction flow.
 *
 * <p>Uses {@code @SpringBootTest} with H2 in-memory database — no external dependencies.
 * Tests run in a fixed order to simulate real user journeys.
 */
@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("Transaction API Integration Tests")
class TransactionControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // Shared state: userId created in test 1 and reused in subsequent tests
    private static Long createdUserId;

    @Test
    @Order(1)
    @DisplayName("1. Create user → 201 Created")
    void createUser_ShouldReturn201() throws Exception {
        CreateUserRequest req = new CreateUserRequest();
        req.setName("Rahul Sharma");
        req.setEmail("rahul.integration@example.com");
        req.setBalance(new BigDecimal("100000.00"));

        MvcResult result = mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data.email").value("rahul.integration@example.com"))
                .andExpect(jsonPath("$.data.balance").value(100000.00))
                .andReturn();

        // Extract userId for subsequent tests
        String body = result.getResponse().getContentAsString();
        createdUserId = objectMapper.readTree(body).path("data").path("id").asLong();
    }

    @Test
    @Order(2)
    @DisplayName("2. Normal ₹5,000 DEBIT → APPROVED, balance reduced")
    void normalDebit_ShouldBeApproved() throws Exception {
        TransactionRequest req = buildTxnRequest(createdUserId, "5000.00", "DEBIT");

        mockMvc.perform(post("/api/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.status").value("APPROVED"))
                .andExpect(jsonPath("$.data.newBalance").value(95000.00))
                .andExpect(jsonPath("$.data.fraudReason").doesNotExist());
    }

    @Test
    @Order(3)
    @DisplayName("3. ₹75,000 DEBIT → FLAGGED by HighAmount (45pts) + RoundAmount (20pts) = 65pts (HIGH risk)")
    void highAmountDebit_ShouldBeFlagged() throws Exception {
        TransactionRequest req = buildTxnRequest(createdUserId, "75000.00", "DEBIT");

        mockMvc.perform(post("/api/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("FLAGGED"))
                .andExpect(jsonPath("$.data.fraudReason").value(containsString("indicator")))
                .andExpect(jsonPath("$.data.newBalance").doesNotExist());
    }

    @Test
    @Order(4)
    @DisplayName("4. GET /api/fraud/flagged → lists the high-amount transaction")
    void getFlaggedTransactions_ShouldReturnFlaggedList() throws Exception {
        mockMvc.perform(get("/api/fraud/flagged"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$.data[0].status").value("FLAGGED"));
    }

    @Test
    @Order(5)
    @DisplayName("5. GET /api/transactions/user/{userId} → returns history for user")
    void getTransactionHistory_ShouldReturnAll() throws Exception {
        mockMvc.perform(get("/api/transactions/user/" + createdUserId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(greaterThanOrEqualTo(2))));
    }

    @Test
    @Order(6)
    @DisplayName("6. Invalid request body (missing userId) → 400 with validation errors")
    void invalidRequest_ShouldReturn400() throws Exception {
        TransactionRequest req = new TransactionRequest();
        // userId not set deliberately

        mockMvc.perform(post("/api/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.data.userId").exists());
    }

    @Test
    @Order(7)
    @DisplayName("7. GET unknown user → 404")
    void getUnknownUser_ShouldReturn404() throws Exception {
        mockMvc.perform(get("/api/users/9999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value("error"));
    }

    // ── Helper ────────────────────────────────────────────────────────────── //

    private TransactionRequest buildTxnRequest(Long userId, String amount, String type) {
        TransactionRequest req = new TransactionRequest();
        req.setUserId(userId);
        req.setAmount(new BigDecimal(amount));
        req.setTransactionType(type);
        return req;
    }
}
