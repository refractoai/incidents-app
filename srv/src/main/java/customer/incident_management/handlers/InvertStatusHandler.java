package customer.incident_management.handlers;

import cds.gen.incidentservice.Incidents;
import com.sap.cds.services.handler.EventHandler;
import com.sap.cds.services.handler.annotations.On;
import com.sap.cds.services.handler.annotations.ServiceName;
import com.sap.cds.services.cds.CdsService;
import com.sap.cds.ql.Update;
import com.sap.cds.ql.cqn.CqnUpdate;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Map;
import com.sap.cds.services.persistence.PersistenceService;
import org.springframework.beans.factory.annotation.Autowired;
import com.sap.cds.ql.Select;

@Component
@ServiceName("IncidentService")
public class InvertStatusHandler implements EventHandler {

    @Autowired
    PersistenceService db;

    @On(entity = "IncidentService.Incidents", event = "invertStatus")
    public void invertClosedTickets(CdsService.EventContext context) {
        // Get all closed incidents
        List<Incidents> closedIncidents = db.run(Select.from("IncidentService.Incidents")
            .where(i -> i.get("status").eq("C")))
            .listOf(Incidents.class);

        // Update each closed incident to new status
        for (Incidents incident : closedIncidents) {
            CqnUpdate update = Update.entity("IncidentService.Incidents")
                .data("status", "N")
                .where(i -> i.get("ID").eq(incident.getId()));
            
            db.run(update);
        }

        // Return the count of updated incidents
        context.setResult(Map.of(
            "message", String.format("Successfully reopened %d incidents", closedIncidents.size()),
            "updatedCount", closedIncidents.size()
        ));
    }
}