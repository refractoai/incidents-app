package customer.incident_management.handler;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.sap.cds.Result;
import com.sap.cds.ql.cqn.CqnSelect;
import com.sap.cds.services.cds.CdsCreateEventContext;
import com.sap.cds.services.cds.CdsUpdateEventContext;
import com.sap.cds.services.persistence.PersistenceService;

import cds.gen.processorservice.Incidents;
import cds.gen.processorservice.StatusHistory;

class IncidentStatusHandlerTest {

    @Mock
    private PersistenceService mockDb;
    
    @Mock
    private CdsCreateEventContext mockCreateContext;
    
    @Mock
    private CdsUpdateEventContext mockUpdateContext;
    
    @Mock
    private Result mockResult;
    
    private IncidentStatusHandler handler;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        handler = new IncidentStatusHandler(mockDb);
    }

    @Test
    void beforeCreate_SetsInitialStatusToNew() {
        // Arrange
        Incidents incident = Incidents.create();
        List<Map<String, Object>> entries = List.of(Map.of("incident", incident));
        when(mockCreateContext.getCqn().entries()).thenReturn(entries);
        
        // Act
        handler.beforeCreate(mockCreateContext);
        
        // Assert
        assertEquals("N", incident.getStatusCode());
    }

    @Test
    void beforeUpdate_WhenStatusChanges_CreatesHistoryEntry() {
        // Arrange
        String incidentId = UUID.randomUUID().toString();
        Incidents updatedIncident = Incidents.create();
        updatedIncident.setStatusCode("C"); // Setting to Closed
        
        Incidents currentIncident = Incidents.create();
        currentIncident.setStatusCode("A"); // Currently Assigned
        
        // Mock the update context
        List<Map<String, Object>> entries = List.of(Map.of("incident", updatedIncident));
        when(mockUpdateContext.getCqn().entries()).thenReturn(entries);
        when(mockUpdateContext.getCqn().ref().segments().get(1)).thenReturn(incidentId);
        
        // Mock the database query
        when(mockDb.run(any(CqnSelect.class))).thenReturn(mockResult);
        when(mockResult.single(Incidents.class)).thenReturn(currentIncident);
        
        // Act
        handler.beforeUpdate(mockUpdateContext);
        
        // Assert
        verify(mockDb).persist(any(StatusHistory.class));
    }

    @Test
    void beforeUpdate_WhenStatusUnchanged_DoesNotCreateHistory() {
        // Arrange
        String incidentId = UUID.randomUUID().toString();
        Incidents updatedIncident = Incidents.create();
        updatedIncident.setStatusCode("A"); // Setting to Assigned
        
        Incidents currentIncident = Incidents.create();
        currentIncident.setStatusCode("A"); // Already Assigned
        
        // Mock the update context
        List<Map<String, Object>> entries = List.of(Map.of("incident", updatedIncident));
        when(mockUpdateContext.getCqn().entries()).thenReturn(entries);
        when(mockUpdateContext.getCqn().ref().segments().get(1)).thenReturn(incidentId);
        
        // Mock the database query
        when(mockDb.run(any(CqnSelect.class))).thenReturn(mockResult);
        when(mockResult.single(Incidents.class)).thenReturn(currentIncident);
        
        // Act
        handler.beforeUpdate(mockUpdateContext);
        
        // Assert
        verify(mockDb, never()).persist(any(StatusHistory.class));
    }
}