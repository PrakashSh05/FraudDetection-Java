package com.fraudapi.service;

import com.fraudapi.constants.FraudCaseAuditEventType;
import com.fraudapi.dto.cases.FraudCaseAuditResponse;
import com.fraudapi.model.FraudCase;
import com.fraudapi.model.FraudCaseAudit;
import com.fraudapi.repository.FraudCaseAuditRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Comprehensive unit tests for {@link FraudCaseAuditService}.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("FraudCaseAuditService Unit Tests")
class FraudCaseAuditServiceTest {

    @Mock
    private FraudCaseAuditRepository auditRepository;

    @InjectMocks
    private FraudCaseAuditService auditService;

    private FraudCase testCase;
    private FraudCaseAudit audit1;
    private FraudCaseAudit audit2;

    @BeforeEach
    void setUp() {
        testCase = FraudCase.builder().id(10L).build();

        audit1 = FraudCaseAudit.builder()
                .id(1L)
                .fraudCase(testCase)
                .eventType(FraudCaseAuditEventType.CASE_CREATED)
                .oldValue(null)
                .newValue("OPEN")
                .performedBy("SYSTEM")
                .timestamp(LocalDateTime.now().minusHours(2))
                .build();

        audit2 = FraudCaseAudit.builder()
                .id(2L)
                .fraudCase(testCase)
                .eventType(FraudCaseAuditEventType.CASE_ASSIGNED)
                .oldValue(null)
                .newValue("analyst1")
                .performedBy("analyst1")
                .timestamp(LocalDateTime.now().minusHours(1))
                .build();
    }

    @Test
    @DisplayName("recordAudit -> saves audit entity with performedBy actor")
    void testRecordAudit_Success() {
        auditService.recordAudit(testCase, FraudCaseAuditEventType.CASE_ASSIGNED, null, "analyst1", "analyst1");

        verify(auditRepository).save(argThat(audit ->
                audit.getFraudCase().getId().equals(10L) &&
                audit.getEventType() == FraudCaseAuditEventType.CASE_ASSIGNED &&
                "analyst1".equals(audit.getPerformedBy())
        ));
    }

    @Test
    @DisplayName("recordAudit with null performedBy -> defaults to SYSTEM")
    void testRecordAudit_NullActor_DefaultsToSystem() {
        auditService.recordAudit(testCase, FraudCaseAuditEventType.CASE_CREATED, null, "OPEN", null);

        verify(auditRepository).save(argThat(audit ->
                "SYSTEM".equals(audit.getPerformedBy())
        ));
    }

    @Test
    @DisplayName("getCaseTimeline -> returns ordered list of audit response DTOs")
    void testGetCaseTimeline_ReturnsOrderedTimeline() {
        when(auditRepository.findByFraudCaseIdOrderByTimestampAsc(10L)).thenReturn(List.of(audit1, audit2));

        List<FraudCaseAuditResponse> timeline = auditService.getCaseTimeline(10L);

        assertNotNull(timeline);
        assertEquals(2, timeline.size());
        assertEquals(FraudCaseAuditEventType.CASE_CREATED, timeline.get(0).eventType());
        assertEquals(FraudCaseAuditEventType.CASE_ASSIGNED, timeline.get(1).eventType());
    }
}
