package customer.incident_management.handler;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import cds.gen.processorservice.Incidents;
import cds.gen.processorservice.Incidents_;
import com.sap.cds.ql.cqn.CqnSelect;
import com.sap.cds.services.cds.CdsUpdateEventContext;
import com.sap.cds.services.persistence.PersistenceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import com.sap.cds.ResultList;

class ClosedIncidentUpdateHandlerTest {

    @Mock
    private PersistenceService mockDb;

    @Mock
    private CdsUpdateEventContext mockContext;

    private ClosedIncidentUpdateHandler handler;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        handler = new ClosedIncidentUpdateHandler(mockDb);
    }

    @Test
    void preventClosedTicketUpdate_ClosedTicket_ShouldBlockUpdate() {
        // Arrange
        String incidentId = "test-incident-id";
        Incidents mockIncident = mock(Incidents.class);
        ResultList<Incidents> mockResult = mock(ResultList.class);
        
        // Mock context behavior
        when(mockContext.getCqn()).thenReturn(mock(CqnSelect.class));
        when(mockContext.getCqn().ref()).thenReturn(mock(CqnSelect.class));
        when(mockContext.getCqn().ref().segments()).thenReturn(java.util.Arrays.asList("", incidentId));
        
        // Mock database behavior
        when(mockDb.run(any(CqnSelect.class))).thenReturn(mockResult);
        when(mockResult.single(Incidents.class)).thenReturn(mockIncident);
        when(mockIncident.getStatusCode()).thenReturn("C");

        // Act
        handler.preventClosedTicketUpdate(mockContext);

        // Assert
        verify(mockContext).setResult(any());
        verify(mockContext).setCompleted();
    }

    @Test
    void preventClosedTicketUpdate_OpenTicket_ShouldAllowUpdate() {
        // Arrange
        String incidentId = "test-incident-id";
        Incidents mockIncident = mock(Incidents.class);
        ResultList<Incidents> mockResult = mock(ResultList.class);
        
        // Mock context behavior
        when(mockContext.getCqn()).thenReturn(mock(CqnSelect.class));
        when(mockContext.getCqn().ref()).thenReturn(mock(CqnSelect.class));
        when(mockContext.getCqn().ref().segments()).thenReturn(java.util.Arrays.asList("", incidentId));
        
        // Mock database behavior
        when(mockDb.run(any(CqnSelect.class))).thenReturn(mockResult);
        when(mockResult.single(Incidents.class)).thenReturn(mockIncident);
        when(mockIncident.getStatusCode()).thenReturn("O");

        // Act
        handler.preventClosedTicketUpdate(mockContext);

        // Assert - should not block the update
        verify(mockContext).getCqn();
        verify(mockIncident).getStatusCode();
    }
}