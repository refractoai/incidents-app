package customer.incident_management.handler;

import cds.gen.processorservice.*;
import com.sap.cds.services.handler.EventHandler;
import com.sap.cds.services.handler.annotations.Before;
import com.sap.cds.services.handler.annotations.On;
import com.sap.cds.services.handler.annotations.ServiceName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import com.sap.cds.services.cds.CdsCreateEventContext;
import com.sap.cds.services.cds.CdsUpdateEventContext;
import com.sap.cds.services.persistence.PersistenceService;
import com.sap.cds.ql.cqn.CqnSelect;
import static com.sap.cds.ql.CQL.*;
import java.util.UUID;

@Component
@ServiceName(ProcessorService_.CDS_NAME)
public class IncidentStatusHandler implements EventHandler {

    private static final Logger logger = LoggerFactory.getLogger(IncidentStatusHandler.class);
    private static final String STATUS_NEW = "N";
    
    private final PersistenceService db;
    
    public IncidentStatusHandler(PersistenceService db) {
        this.db = db;
    }

    @Before(event = CdsCreateEventContext.CREATE)
    public void beforeCreate(CdsCreateEventContext context) {
        logger.info("Setting initial status for new incident");
        Incidents incident = context.getCqn().entries().get(0).as(Incidents.class);
        incident.setStatusCode(STATUS_NEW);
        logger.info("Incident status set to 'new' (N)");
    }
    
    @Before(event = CdsUpdateEventContext.UPDATE)
    public void beforeUpdate(CdsUpdateEventContext context) {
        Incidents updatedIncident = context.getCqn().entries().get(0).as(Incidents.class);
        String newStatusCode = updatedIncident.getStatusCode();
        
        if (newStatusCode != null) {
            String incidentId = context.getCqn().ref().segments().get(1).toString();
            
            // Get the current status before update
            CqnSelect select = Select.from(Incidents_.class)
                .where(i -> i.ID().eq(incidentId));
            Incidents currentIncident = db.run(select).single(Incidents.class);
            String oldStatusCode = currentIncident.getStatusCode();
            
            // Only create history if status is actually changing
            if (!newStatusCode.equals(oldStatusCode)) {
                StatusHistory history = StatusHistory.create();
                history.setId(UUID.randomUUID());
                history.setIncidentId(incidentId);
                history.setOldStatusCode(oldStatusCode);
                history.setNewStatusCode(newStatusCode);
                
                db.persist(history);
                logger.info("Created status history entry for incident {} ({}->{})", 
                    incidentId, oldStatusCode, newStatusCode);
            }
        }
    }
}