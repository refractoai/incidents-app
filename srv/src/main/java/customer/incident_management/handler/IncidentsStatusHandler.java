package customer.incident_management.handler;

import java.util.*;
import com.sap.cds.services.handler.EventHandler;
import com.sap.cds.services.handler.annotations.Before;
import com.sap.cds.services.handler.annotations.ServiceName;
import org.springframework.stereotype.Component;

import cds.gen.incidentservice.Incidents;
import cds.gen.incidentservice.Incidents_;

@Component
@ServiceName("IncidentService")
public class IncidentsStatusHandler implements EventHandler {

    @Before(event = CqnService.EVENT_CREATE, entity = Incidents_.CDS_NAME)
    public void beforeCreating(Incidents incidents) {
        String title = incidents.getTitle();
        if (title != null && title.toLowerCase().contains("urgent")) {
            incidents.setStatus("high");
        }
    }
}