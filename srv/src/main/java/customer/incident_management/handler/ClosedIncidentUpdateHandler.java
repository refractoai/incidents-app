package customer.incident_management.handler;

import cds.gen.processorservice.*;
import com.sap.cds.ql.cqn.CqnSelect;
import com.sap.cds.services.ErrorStatuses;
import com.sap.cds.services.handler.EventHandler;
import com.sap.cds.services.handler.annotations.Before;
import com.sap.cds.services.handler.annotations.ServiceName;
import com.sap.cds.services.persistence.PersistenceService;
import org.springframework.stereotype.Component;
import com.sap.cds.services.cds.CdsUpdateEventContext;
import static com.sap.cds.ql.CQL.*;

@Component
@ServiceName(ProcessorService_.CDS_NAME)
public class ClosedIncidentUpdateHandler implements EventHandler {

    private static final String STATUS_CLOSED = "C";
    private final PersistenceService db;

    public ClosedIncidentUpdateHandler(PersistenceService db) {
        this.db = db;
    }

    @Before(event = CdsUpdateEventContext.UPDATE, entity = Incidents_.CDS_NAME)
    public void preventClosedTicketUpdate(CdsUpdateEventContext context) {
        String incidentId = context.getCqn().ref().segments().get(1).toString();
        
        // Get the current status of the incident
        CqnSelect select = Select.from(Incidents_.class)
            .where(i -> i.ID().eq(incidentId));
        Incidents incident = db.run(select).single(Incidents.class);

        // If the incident is closed, prevent any updates
        if (STATUS_CLOSED.equals(incident.getStatusCode())) {
            context.setResult(ErrorStatuses.BAD_REQUEST.withError(
                "CLOSED_TICKET_UPDATE", 
                "You cannot modify a closed ticket."
            ));
            context.setCompleted();
        }
    }
}