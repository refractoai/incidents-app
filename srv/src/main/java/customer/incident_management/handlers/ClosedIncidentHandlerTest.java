package customer.incident_management.handlers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import cds.gen.adminservice.Incidents;
import cds.gen.adminservice.Incidents_;
import com.sap.cds.ql.Select;
import com.sap.cds.services.ServiceException;
import com.sap.cds.services.persistence.PersistenceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.sap.cds.Result;

@ExtendWith(MockitoExtension.class)
class ClosedIncidentHandlerTest {

    @Mock
    private PersistenceService db;

    @Mock
    private Result result;

    @InjectMocks
    private ClosedIncidentHandler handler;

    private Incidents closedIncident;
    private Incidents openIncident;
    private Incidents updateIncident;

    @BeforeEach
    void setUp() {
        // Setup test data
        closedIncident = Incidents.create();
        closedIncident.setId("closed-123");
        closedIncident.setStatusCode("C");

        openIncident = Incidents.create();
        openIncident.setId("open-123");
        openIncident.setStatusCode("N");

        updateIncident = Incidents.create();
        updateIncident.setId("closed-123");
    }

    @Test
    void shouldAllowUpdateToOpenIncident() {
        // Arrange
        when(db.run(any(Select.class))).thenReturn(result);
        when(result.single(Incidents.class)).thenReturn(openIncident);

        // Act & Assert
        assertDoesNotThrow(() -> handler.preventClosedIncidentModification(updateIncident));
    }

    @Test
    void shouldPreventUpdateToClosedIncident() {
        // Arrange
        when(db.run(any(Select.class))).thenReturn(result);
        when(result.single(Incidents.class)).thenReturn(closedIncident);
        updateIncident.setStatusCode("N");

        // Act & Assert
        ServiceException exception = assertThrows(
            ServiceException.class,
            () -> handler.preventClosedIncidentModification(updateIncident)
        );
        assertTrue(exception.getMessage().contains("Cannot modify a closed incident ticket"));
    }

    @Test
    void shouldAllowReopeningClosedIncident() {
        // Arrange
        when(db.run(any(Select.class))).thenReturn(result);
        when(result.single(Incidents.class)).thenReturn(closedIncident);
        updateIncident.setStatusCode("R"); // Setting to reopen status

        // Act & Assert
        assertDoesNotThrow(() -> handler.preventClosedIncidentModification(updateIncident));
    }

    @Test
    void shouldHandleNonExistentIncident() {
        // Arrange
        when(db.run(any(Select.class))).thenReturn(result);
        when(result.single(Incidents.class)).thenReturn(null);

        // Act & Assert
        ServiceException exception = assertThrows(
            ServiceException.class,
            () -> handler.preventClosedIncidentModification(updateIncident)
        );
        assertTrue(exception.getMessage().contains("not found"));
    }
}