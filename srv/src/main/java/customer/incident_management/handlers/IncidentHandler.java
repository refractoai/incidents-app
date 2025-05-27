package customer.incident_management.handlers;

import cds.gen.incidentservice.Incidents;
import com.sap.cds.services.cds.CdsReadEventContext;
import com.sap.cds.services.handler.EventHandler;
import com.sap.cds.services.handler.annotations.On;
import com.sap.cds.services.handler.annotations.ServiceName;
import org.springframework.stereotype.Component;

@Component
@ServiceName("IncidentService")
public class IncidentHandler implements EventHandler {

    @On(event = CdsReadEventContext.CDS_NAME)
    public void preventEditClosedTicket(CdsReadEventContext context) {
        if (context.getCqn().ref().stream().anyMatch(ref -> ref.toString().equals("Incidents"))) {
            Incidents incident = context.getResult().as(Incidents.class);
            if (incident.getStatus() != null && incident.getStatus().equals("closed")) {
                context.setResult(context.getResult().with("isReadOnly", true));
            }
        }
    }
}