package customer.incident_management.handlers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Collections;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import cds.gen.sap.capire.incidents.Incidents;
import com.sap.cds.Result;
import com.sap.cds.services.cds.CdsService;

@ExtendWith(MockitoExtension.class)
class IncidentHandlerTest {

    @Mock
    private CdsService mockService;

    @Mock
    private Result mockResult;

    private IncidentHandler handler;
    private Incidents oldIncident;
    private Incidents recentIncident;

    @BeforeEach
    void setUp() {
        handler = new IncidentHandler();
        
        // Create test data
        oldIncident = Incidents.create();
        oldIncident.setId(UUID.randomUUID());
        oldIncident.setStatusCode("C");
        oldIncident.setModifiedAt(Instant.now().minus(3, ChronoUnit.DAYS));

        recentIncident = Incidents.create();
        recentIncident.setId(UUID.randomUUID());
        recentIncident.setStatusCode("C");
        recentIncident.setModifiedAt(Instant.now());
    }

    @Test
    void testRemoveTickets_WithOldClosedIncidents() {
        // Arrange
        when(mockService.run(any())).thenReturn(mockResult);
        when(mockResult.listOf(Incidents.class))
            .thenReturn(Arrays.asList(oldIncident));

        // Act
        handler.removeTickets(mockService);

        // Assert
        verify(mockService, times(2)).run(any()); // One for select, one for delete
    }

    @Test
    void testRemoveTickets_WithRecentClosedIncidents() {
        // Arrange
        when(mockService.run(any())).thenReturn(mockResult);
        when(mockResult.listOf(Incidents.class))
            .thenReturn(Arrays.asList(recentIncident));

        // Act
        handler.removeTickets(mockService);

        // Assert
        verify(mockService, times(1)).run(any()); // Only the select, no delete
    }

    @Test
    void testRemoveTickets_WithNoIncidents() {
        // Arrange
        when(mockService.run(any())).thenReturn(mockResult);
        when(mockResult.listOf(Incidents.class))
            .thenReturn(Collections.emptyList());

        // Act
        handler.removeTickets(mockService);

        // Assert
        verify(mockService, times(1)).run(any()); // Only the select, no delete
    }

    @Test
    void testRemoveTickets_WithMixedIncidents() {
        // Arrange
        when(mockService.run(any())).thenReturn(mockResult);
        when(mockResult.listOf(Incidents.class))
            .thenReturn(Arrays.asList(oldIncident, recentIncident));

        // Act
        handler.removeTickets(mockService);

        // Assert
        verify(mockService, times(2)).run(any()); // One select, one delete
    }
}