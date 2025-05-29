package customer.incident_management.handler;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

import com.sap.cds.services.handler.EventHandler;
import com.sap.cds.services.handler.annotations.Before;
import com.sap.cds.services.handler.annotations.ServiceName;
import com.sap.cds.services.cds.CdsService;
import com.sap.cds.services.handler.annotations.On;
import com.sap.cds.ql.Select;
import com.sap.cds.Row;
import org.springframework.stereotype.Component;

@Component
@ServiceName("CustomerService")
public class CustomerSortHandler implements EventHandler {
    
    @Before(event = CdsService.EVENT_CREATE, entity = "CustomerService.Customers")
    public void beforeCustomerCreate(CdsService.EventContext context) {
        // Get all existing customers
        CdsService service = context.getService();
        List<Row> customers = service.run(Select.from("CustomerService.Customers")).listOf(Row.class);
        
        // Extract customer names
        List<String> customerNames = new ArrayList<>();
        for (Row customer : customers) {
            customerNames.add((String) customer.get("name"));
        }
        
        // Add the new customer name
        Row newCustomer = context.get("data");
        String newCustomerName = (String) newCustomer.get("name");
        customerNames.add(newCustomerName);
        
        // Bubble sort implementation
        int n = customerNames.size();
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                if (customerNames.get(j).compareTo(customerNames.get(j + 1)) > 0) {
                    // Swap customerNames[j] and customerNames[j+1]
                    String temp = customerNames.get(j);
                    customerNames.set(j, customerNames.get(j + 1));
                    customerNames.set(j + 1, temp);
                }
            }
        }
        
        // Store sorted list in context for potential later use
        context.set("sortedCustomerNames", customerNames);
    }
}