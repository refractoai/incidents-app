package customer.incident_management.handlers;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import cds.gen.sap.capire.incidents.Incidents;
import cds.gen.sap.capire.incidents.Incidents_;
import com.sap.cds.ql.Delete;
import com.sap.cds.ql.Select;
import com.sap.cds.services.cds.CdsService;
import com.sap.cds.services.handler.EventHandler;
import com.sap.cds.services.handler.annotations.After;
import com.sap.cds.services.handler.annotations.ServiceName;
import org.springframework.stereotype.Component;

@Component
@ServiceName("sap.capire.incidents.IncidentService")
public class IncidentHandler implements EventHandler {
    
    @After(event = CdsService.EVENT_UPDATE, entity = Incidents_.CDS_NAME)
    public void removeTicketsGaurav(CdsService service) {
        // Get current timestamp
        Instant now = Instant.now();
        
        // Find all closed incidents older than 2 days
        var incidents = service.run(
            Select.from(Incidents_.class)
                .where(i -> i.status_code().eq("C")
                    .and(i.modifiedAt().lt(now.minus(2, ChronoUnit.DAYS))))
        ).listOf(Incidents.class);
        
        // Delete the found incidents
        if (!incidents.isEmpty()) {
            incidents.forEach(incident -> 
                service.run(Delete.from(Incidents_.class).where(i -> 
                    i.ID().eq(incident.getId()))));
        }
    }
}