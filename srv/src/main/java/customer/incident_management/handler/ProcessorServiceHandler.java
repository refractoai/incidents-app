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

@Component
@ServiceName(ProcessorService_.CDS_NAME)
public class ProcessorServiceHandler implements EventHandler {

    private static final Logger logger = LoggerFactory.getLogger(ProcessorServiceHandler.class);

    private final PersistenceService db;

    public ProcessorServiceHandler(PersistenceService db) {
        this.db = db;
    }

}
