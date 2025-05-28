package com.customer.triggers;

import cds.gen.catalogservice.Tickets;
import com.sap.cds.services.cds.CdsCreateEventContext;
import com.sap.cds.services.handler.EventHandler;
import com.sap.cds.services.handler.annotations.On;
import com.sap.cds.services.handler.annotations.ServiceName;
import org.springframework.stereotype.Component;

@Component
@ServiceName(value = "CatalogService")
public class TicketTriggerHandler implements EventHandler {

    @On(event = CdsCreateEventContext.CREATE, entity = "CatalogService.Tickets")
    public void onTicketCreation(CdsCreateEventContext context) {
        Tickets ticket = context.getCqn().entries().get(0).as(Tickets.class);
        
        if (ticket.getTitle() != null && ticket.getTitle().toUpperCase().contains("URGENT")) {
            ticket.setStatus("HIGH");
        }
    }
}