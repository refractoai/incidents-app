package customer.incident_management.handler;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import cds.gen.incidentservice.Incidents;

@ExtendWith(MockitoExtension.class)
public class IncidentsStatusHandlerTest {

    @InjectMocks
    private IncidentsStatusHandler handler;

    @Mock
    private Incidents incidents;

    @BeforeEach
    void setUp() {
        incidents = mock(Incidents.class);
    }

    @Test
    void testBeforeCreate_WithUrgentTitle_SetsHighStatus() {
        // Arrange
        when(incidents.getTitle()).thenReturn("Urgent Server Issue");

        // Act
        handler.beforeCreate(incidents);

        // Assert
        verify(incidents).setStatus("high");
    }

    @Test
    void testBeforeCreate_WithNonUrgentTitle_DoesNotSetStatus() {
        // Arrange
        when(incidents.getTitle()).thenReturn("Normal Server Issue");

        // Act
        handler.beforeCreate(incidents);

        // Assert
        verify(incidents, never()).setStatus(any());
    }

    @Test
    void testBeforeCreate_WithNullTitle_DoesNotSetStatus() {
        // Arrange
        when(incidents.getTitle()).thenReturn(null);

        // Act
        handler.beforeCreate(incidents);

        // Assert
        verify(incidents, never()).setStatus(any());
    }
}