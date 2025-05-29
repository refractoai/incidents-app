package customer.incident_management.handlers;

import cds.gen.adminservice.Incidents;
import cds.gen.adminservice.AdminService_;
import com.sap.cds.ql.Select;
import com.sap.cds.services.ErrorStatuses;
import com.sap.cds.services.ServiceException;
import com.sap.cds.services.cds.CqnService;
import com.sap.cds.services.handler.EventHandler;
import com.sap.cds.services.handler.annotations.Before;
import com.sap.cds.services.handler.annotations.ServiceName;
import com.sap.cds.services.persistence.PersistenceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import java.util.Optional;

/**
 * Handler for managing closed incident tickets.
 * Implements validation logic to prevent modifications to closed incidents.
 */
@Component
@ServiceName(AdminService_.CDS_NAME)
public class ClosedIncidentHandler implements EventHandler {

    private static final Logger logger = LoggerFactory.getLogger(ClosedIncidentHandler.class);
    
    // Status codes
    private static final String STATUS_CLOSED = "C";
    private static final String STATUS_REOPEN = "R";
    
    private final PersistenceService db;

    public ClosedIncidentHandler(PersistenceService db) {
        this.db = db;
    }

    /**
     * Prevents updates to incidents that are already closed.
     * This handler runs before any update operation on incidents.
     * 
     * @param incident The incident being updated
     * @throws ServiceException if the incident is closed and the update is not allowed
     */
    @Before(event = CqnService.EVENT_UPDATE)
    public void preventClosedIncidentModification(Incidents incident) {
        String incidentId = incident.getId();
        logger.debug("Validating update request for incident: {}", incidentId);
        
        try {
            // Fetch current incident state
            Incidents existingIncident = getExistingIncident(incidentId);
            
            // Validate the update
            validateIncidentUpdate(existingIncident, incident);
            
        } catch (ServiceException se) {
            // Log and rethrow service exceptions
            logger.warn("Update rejected: {}", se.getMessage());
            throw se;
        } catch (Exception e) {
            // Log and wrap unexpected errors
            logger.error("Unexpected error during incident update validation", e);
            throw new ServiceException(
                ErrorStatuses.SERVER_ERROR,
                "Internal error while processing incident update"
            );
        }
    }

    /**
     * Prevents bulk updates that might include closed incidents.
     * Validates each incident in the collection individually.
     *
     * @param incidents Collection of incidents to be updated
     */
    @Before(event = CqnService.EVENT_UPDATE)
    public void preventBulkClosedIncidentModification(Iterable<Incidents> incidents) {
        incidents.forEach(this::preventClosedIncidentModification);
    }

    /**
     * Retrieves the current state of an incident from the database.
     *
     * @param incidentId ID of the incident to retrieve
     * @return The existing incident
     * @throws ServiceException if the incident cannot be found
     */
    private Incidents getExistingIncident(String incidentId) {
        return Optional.ofNullable(
            db.run(
                Select.from(Incidents_.class)
                    .where(i -> i.ID().eq(incidentId))
            ).single(Incidents.class)
        ).orElseThrow(() -> new ServiceException(
            ErrorStatuses.NOT_FOUND,
            String.format("Incident with ID %s not found", incidentId)
        ));
    }

    /**
     * Validates whether an update to an incident should be allowed.
     *
     * @param existingIncident The current state of the incident
     * @param updatedIncident The proposed updates to the incident
     * @throws ServiceException if the update is not allowed
     */
    private void validateIncidentUpdate(Incidents existingIncident, Incidents updatedIncident) {
        if (STATUS_CLOSED.equals(existingIncident.getStatusCode())) {
            // Allow updates only if explicitly reopening the incident
            if (!STATUS_REOPEN.equals(updatedIncident.getStatusCode())) {
                logger.warn("Attempted to modify closed incident: {}", existingIncident.getId());
                throw new ServiceException(
                    ErrorStatuses.CONFLICT,
                    "Cannot modify a closed incident ticket. To make changes, please reopen the ticket first."
                );
            }
            logger.info("Allowing update to closed incident {} - reopening ticket", existingIncident.getId());
        }
    }
}