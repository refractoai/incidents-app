package com.customer.triggers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import cds.gen.catalogservice.Tickets;
import com.sap.cds.ql.cqn.CqnInsert;
import com.sap.cds.services.cds.CdsCreateEventContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class TicketTriggerHandlerTest {

    @InjectMocks
    private TicketTriggerHandler ticketTriggerHandler;

    @Mock
    private CdsCreateEventContext context;

    @Mock
    private CqnInsert cqnInsert;

    private Tickets testTicket;

    @BeforeEach
    void setUp() {
        testTicket = Tickets.create();
        when(context.getCqn()).thenReturn(cqnInsert);
        when(cqnInsert.entries()).thenReturn(List.of(testTicket));
    }

    @Test
    void testUrgentTicketStatusSetToHigh() {
        // Given
        testTicket.setTitle("URGENT: System Down");
        
        // When
        ticketTriggerHandler.onTicketCreation(context);
        
        // Then
        assertEquals("HIGH", testTicket.getStatus());
    }

    @Test
    void testNonUrgentTicketStatusUnchanged() {
        // Given
        testTicket.setTitle("Regular Maintenance Request");
        
        // When
        ticketTriggerHandler.onTicketCreation(context);
        
        // Then
        assertNull(testTicket.getStatus());
    }

    @Test
    void testNullTitleHandling() {
        // Given
        testTicket.setTitle(null);
        
        // When
        ticketTriggerHandler.onTicketCreation(context);
        
        // Then
        assertNull(testTicket.getStatus());
    }
}