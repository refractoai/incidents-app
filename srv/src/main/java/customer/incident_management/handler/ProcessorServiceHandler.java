package customer.incident_management.handler;

import cds.gen.processorservice.Incidents;
import cds.gen.processorservice.ProcessorService_;
import cds.gen.sap.capire.incidents.*;
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
import java.util.List;
import java.util.Locale;
import com.sap.cds.ql.cqn.CqnUpdate;

@Component
@ServiceName(ProcessorService_.CDS_NAME)
public class ProcessorServiceHandler implements EventHandler {

    private static final Logger logger = LoggerFactory.getLogger(ProcessorServiceHandler.class);
    private static final String STATUS_CLOSED = "C";
    private static final String STATUS_RESOLVED = "R";

    private final PersistenceService db;

    public ProcessorServiceHandler(PersistenceService db) {
        this.db = db;
    }

    @Before(event = CqnService.EVENT_UPDATE)
    public void beforeUpdate(CqnUpdate update) {
        String status = db.run(Select.from(Incidents_.class)
                .where(i -> i.ID().eq(update.ref().getKeyValues().get(0).toString())))
                .single(Incidents_.class)
                .getStatus_code();

        if (STATUS_CLOSED.equals(status) || STATUS_RESOLVED.equals(status)) {
            String statusText = STATUS_CLOSED.equals(status) ? "closed" : "resolved";
            logger.error("Cannot update a {} incident", statusText);
            throw new ServiceException(ErrorStatuses.BAD_REQUEST, 
                    String.format("Error: Cannot update a %s ticket", statusText));
        }
    }
}