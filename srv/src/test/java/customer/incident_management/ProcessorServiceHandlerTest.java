package customer.incident_management;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.sap.cds.services.ServiceException;
import com.sap.cds.services.ErrorStatuses;

import cds.gen.sap.capire.incidents.Incidents;
import cds.gen.sap.capire.incidents.Status;
import customer.incident_management.handler.ProcessorServiceHandler;

@ExtendWith(MockitoExtension.class)
public class ProcessorServiceHandlerTest {

    @InjectMocks
    private ProcessorServiceHandler handler;

    @Mock
    private Incidents mockIncident;

    @Mock
    private Status mockStatus;

    @BeforeEach
    void setUp() {
        when(mockIncident.getStatus()).thenReturn(mockStatus);
    }

    @Test
    void testUpdateClosedIncident_ThrowsException() {
        // Arrange
        when(mockStatus.getCode()).thenReturn(Status.CodeEnum.closed);
        Stream<Incidents> incidents = Stream.of(mockIncident);

        // Act & Assert
        ServiceException exception = assertThrows(ServiceException.class,
            () -> handler.beforeIncidentUpdate(incidents));

        assertEquals(ErrorStatuses.FORBIDDEN, exception.getStatusCode());
        assertEquals("Cannot update closed ticket", exception.getMessage());
    }

    @Test
    void testUpdateNonClosedIncident_Succeeds() {
        // Arrange
        when(mockStatus.getCode()).thenReturn(Status.CodeEnum.in_process);
        Stream<Incidents> incidents = Stream.of(mockIncident);

        // Act & Assert - should not throw any exception
        handler.beforeIncidentUpdate(incidents);
    }

    @Test
    void testUpdateIncidentWithNullStatus_Succeeds() {
        // Arrange
        when(mockIncident.getStatus()).thenReturn(null);
        Stream<Incidents> incidents = Stream.of(mockIncident);

        // Act & Assert - should not throw any exception
        handler.beforeIncidentUpdate(incidents);
    }

    @Test
    void testUpdateIncidentWithNullStatusCode_Succeeds() {
        // Arrange
        when(mockStatus.getCode()).thenReturn(null);
        Stream<Incidents> incidents = Stream.of(mockIncident);

        // Act & Assert - should not throw any exception
        handler.beforeIncidentUpdate(incidents);
    }
}