package customer.incident_management.handler;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import cds.gen.processorservice.Incidents;
import cds.gen.sap.capire.incidents.Incidents_;
import com.sap.cds.Result;
import com.sap.cds.ql.cqn.CqnSelect;
import com.sap.cds.ql.cqn.CqnUpdate;
import com.sap.cds.services.ErrorStatuses;
import com.sap.cds.services.ServiceException;
import com.sap.cds.services.persistence.PersistenceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;

@ExtendWith(MockitoExtension.class)
class ProcessorServiceHandlerTest {

    @Mock
    private PersistenceService mockDb;

    @Mock
    private Result mockResult;

    @Mock
    private CqnUpdate mockUpdate;

    @Mock
    private Incidents mockIncident;

    @InjectMocks
    private ProcessorServiceHandler handler;

    @BeforeEach
    void setUp() {
        when(mockUpdate.ref()).thenReturn(mock(CqnSelect.class));
        when(mockUpdate.ref().getKeyValues()).thenReturn(Collections.singletonList("test-id"));
    }

    @Test
    void beforeUpdate_WhenIncidentIsNew_ShouldAllowUpdate() {
        // Arrange
        when(mockDb.run(any(CqnSelect.class))).thenReturn(mockResult);
        when(mockResult.single(Incidents_.class)).thenReturn(mockIncident);
        when(mockIncident.getStatus_code()).thenReturn("N");

        // Act & Assert
        assertDoesNotThrow(() -> handler.beforeUpdate(mockUpdate));
        verify(mockDb).run(any(CqnSelect.class));
    }

    @Test
    void beforeUpdate_WhenIncidentIsClosed_ShouldThrowServiceException() {
        // Arrange
        when(mockDb.run(any(CqnSelect.class))).thenReturn(mockResult);
        when(mockResult.single(Incidents_.class)).thenReturn(mockIncident);
        when(mockIncident.getStatus_code()).thenReturn("C");

        // Act & Assert
        ServiceException exception = assertThrows(
            ServiceException.class,
            () -> handler.beforeUpdate(mockUpdate)
        );

        assertEquals(ErrorStatuses.BAD_REQUEST, exception.getCode());
        assertEquals("Error: Cannot update a closed ticket", exception.getMessage());
        verify(mockDb).run(any(CqnSelect.class));
    }

    @Test
    void beforeUpdate_WhenIncidentIsResolved_ShouldThrowServiceException() {
        // Arrange
        when(mockDb.run(any(CqnSelect.class))).thenReturn(mockResult);
        when(mockResult.single(Incidents_.class)).thenReturn(mockIncident);
        when(mockIncident.getStatus_code()).thenReturn("R");

        // Act & Assert
        ServiceException exception = assertThrows(
            ServiceException.class,
            () -> handler.beforeUpdate(mockUpdate)
        );

        assertEquals(ErrorStatuses.BAD_REQUEST, exception.getCode());
        assertEquals("Error: Cannot update a resolved ticket", exception.getMessage());
        verify(mockDb).run(any(CqnSelect.class));
    }

    @Test
    void beforeUpdate_WithNullStatus_ShouldAllowUpdate() {
        // Arrange
        when(mockDb.run(any(CqnSelect.class))).thenReturn(mockResult);
        when(mockResult.single(Incidents_.class)).thenReturn(mockIncident);
        when(mockIncident.getStatus_code()).thenReturn(null);

        // Act & Assert
        assertDoesNotThrow(() -> handler.beforeUpdate(mockUpdate));
        verify(mockDb).run(any(CqnSelect.class));
    }

    @Test
    void beforeUpdate_WhenIncidentIsInProcess_ShouldAllowUpdate() {
        // Arrange
        when(mockDb.run(any(CqnSelect.class))).thenReturn(mockResult);
        when(mockResult.single(Incidents_.class)).thenReturn(mockIncident);
        when(mockIncident.getStatus_code()).thenReturn("I");

        // Act & Assert
        assertDoesNotThrow(() -> handler.beforeUpdate(mockUpdate));
        verify(mockDb).run(any(CqnSelect.class));
    }
}