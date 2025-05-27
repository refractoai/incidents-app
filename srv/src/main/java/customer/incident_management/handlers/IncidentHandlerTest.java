package customer.incident_management.handlers;

import cds.gen.incidentservice.Incidents;
import com.sap.cds.services.cds.CdsReadEventContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import com.sap.cds.Row;
import com.sap.cds.Result;
import com.sap.cds.ql.cqn.CqnSelect;

import static org.mockito.Mockito.*;

public class IncidentHandlerTest {

    @Mock
    private CdsReadEventContext context;

    @Mock
    private Result result;

    @Mock
    private CqnSelect select;

    @Mock
    private Incidents incident;

    private IncidentHandler handler;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        handler = new IncidentHandler();
    }

    @Test
    void testPreventEditClosedTicket_WhenTicketIsClosed() {
        // Arrange
        when(context.getCqn()).thenReturn(select);
        when(select.ref()).thenReturn(java.util.Collections.singletonList(() -> "Incidents"));
        when(context.getResult()).thenReturn(result);
        when(result.as(Incidents.class)).thenReturn(incident);
        when(incident.getStatus()).thenReturn("closed");
        when(result.with("isReadOnly", true)).thenReturn(result);

        // Act
        handler.preventEditClosedTicket(context);

        // Assert
        verify(context).setResult(result);
        verify(result).with("isReadOnly", true);
    }

    @Test
    void testPreventEditClosedTicket_WhenTicketIsOpen() {
        // Arrange
        when(context.getCqn()).thenReturn(select);
        when(select.ref()).thenReturn(java.util.Collections.singletonList(() -> "Incidents"));
        when(context.getResult()).thenReturn(result);
        when(result.as(Incidents.class)).thenReturn(incident);
        when(incident.getStatus()).thenReturn("open");

        // Act
        handler.preventEditClosedTicket(context);

        // Assert
        verify(context, never()).setResult(any());
        verify(result, never()).with(anyString(), anyBoolean());
    }

    @Test
    void testPreventEditClosedTicket_WhenStatusIsNull() {
        // Arrange
        when(context.getCqn()).thenReturn(select);
        when(select.ref()).thenReturn(java.util.Collections.singletonList(() -> "Incidents"));
        when(context.getResult()).thenReturn(result);
        when(result.as(Incidents.class)).thenReturn(incident);
        when(incident.getStatus()).thenReturn(null);

        // Act
        handler.preventEditClosedTicket(context);

        // Assert
        verify(context, never()).setResult(any());
        verify(result, never()).with(anyString(), anyBoolean());
    }
}