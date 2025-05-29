package customer.incident_management.handlers;

import cds.gen.processorservice.Incidents;
import com.sap.cds.services.handler.EventHandler;
import com.sap.cds.services.handler.annotations.Before;
import com.sap.cds.services.handler.annotations.ServiceName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
@ServiceName("ProcessorService")
public class UrgentTicketHandler implements EventHandler {
    
    private static final Logger logger = LoggerFactory.getLogger(UrgentTicketHandler.class);

    /**
     * Handler to automatically set urgency to HIGH for new incidents
     */
    @Before(event = "CREATE")
    public void setHighUrgencyForNewIncidents(List<Incidents> incidents) {
        for (Incidents incident : incidents) {
            if (incident.getUrgency() == null || !incident.getUrgency().getCode().equals("H")) {
                incident.setUrgencyCode("H");
                logger.info("Set urgency to HIGH for new incident: {}", incident.getTitle());
            }
        }
    }

    /**
     * Handler to ensure high urgency for incidents during updates
     */
    @Before(event = "UPDATE")
    public void updateHighUrgencyForIncidents(Incidents incident) {
        if (incident.getUrgency() == null || !incident.getUrgency().getCode().equals("H")) {
            incident.setUrgencyCode("H");
            logger.info("Updated urgency to HIGH for incident: {}", incident.getTitle());
        }
    }
}