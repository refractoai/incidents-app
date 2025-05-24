package customer.refracto.handler; 

import com.sap.cds.services.cds.CdsService;
import com.sap.cds.services.handler.EventHandler;
import com.sap.cds.services.handler.annotations.After;
import com.sap.cds.services.handler.annotations.ServiceName;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Handler for applying custom logic to the CustomerService.
 * This handler includes functionality to sort the list of customers
 * by name using a bubble sort algorithm after they are read.
 * Assumes the package name 'customer.refracto.handler' aligns with your project structure
 * (e.g., 'refracto' derived from your project name 'Refracto' in lowercase).
 */
@Component
@ServiceName("CustomerService") // Assumes your CAP service is named 'CustomerService'.
                                // Adjust if your service has a different name (e.g., "CatalogService").
public class CustomerServiceHandler implements EventHandler {

    /**
     * After a READ operation on the "Customers" entity set, this method
     * sorts the retrieved list of customers by their 'name' field using
     * the bubble sort algorithm.
     *
     * Assumes:
     * 1. The entity set exposed by the service is named "Customers".
     *    Adjust the 'entity' attribute in @After if it's different (e.g., "CustomerList").
     * 2. Each customer Map contains a key "name" of type String.
     *    Adjust the key and casting if a different field or type is used for sorting.
     * 3. The 'name' field should be part of the 'Customers' entity definition in your schema.cds.
     *
     * @param customers A list of Maps, where each Map represents a customer entity.
     *                  This list is modified in-place.
     */
    @After(event = CdsService.EVENT_READ, entity = "Customers")
    public void sortCustomersAfterRead(List<Map<String, Object>> customers) {
        if (customers == null || customers.size() <= 1) {
            return; // No need to sort if the list is empty or has one element
        }

        int n = customers.size();
        boolean swapped;
        for (int i = 0; i < n - 1; i++) {
            swapped = false;
            for (int j = 0; j < n - i - 1; j++) {
                Map<String, Object> customer1 = customers.get(j);
                Map<String, Object> customer2 = customers.get(j + 1);

                // Retrieve names for comparison.
                // Assumes 'name' field exists and is a String.
                String name1 = (String) customer1.get("name");
                String name2 = (String) customer2.get("name");

                // Comparison logic for bubble sort, handling nulls (nulls come first)
                int compareResult;
                if (name1 == null && name2 == null) {
                    compareResult = 0; // Both null, considered equal for sorting stability
                } else if (name1 == null) {
                    compareResult = -1; // name1 is null, name2 is not; name1 comes before name2
                } else if (name2 == null) {
                    compareResult = 1;  // name1 is not null, name2 is; name1 comes after name2
                } else {
                    compareResult = name1.compareTo(name2); // Both non-null, standard string comparison
                }

                // If customer1's name is greater than customer2's name, swap them
                if (compareResult > 0) {
                    Collections.swap(customers, j, j + 1);
                    swapped = true;
                }
            }

            // Optimization: if no two elements were swapped in the inner loop,
            // the list is already sorted, and we can break early.
            if (!swapped) {
                break;
            }
        }
    }
}