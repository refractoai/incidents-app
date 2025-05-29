package customer.incident_management.handler;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.sap.cds.Row;
import com.sap.cds.ql.Select;
import com.sap.cds.services.cds.CdsService;

@RunWith(MockitoJUnitRunner.class)
public class CustomerSortHandlerTest {

    @Mock
    private CdsService.EventContext context;

    @Mock
    private CdsService service;

    @Mock
    private Row newCustomer;

    private CustomerSortHandler handler;
    private List<Row> mockCustomers;

    @Before
    public void setup() {
        handler = new CustomerSortHandler();
        mockCustomers = new ArrayList<>();

        // Setup mock behavior
        when(context.getService()).thenReturn(service);
        when(context.get("data")).thenReturn(newCustomer);
    }

    @Test
    public void testBeforeCustomerCreateWithEmptyList() {
        // Setup
        when(service.run(any(Select.class))).thenReturn(mockCustomers);
        when(newCustomer.get("name")).thenReturn("John");

        // Execute
        handler.beforeCustomerCreate(context);

        // Verify
        verify(context).getService();
        verify(service).run(any(Select.class));
        verify(context).set(eq("sortedCustomerNames"), any(List.class));
    }

    @Test
    public void testBeforeCustomerCreateWithUnsortedList() {
        // Setup mock customers
        Row customer1 = mock(Row.class);
        Row customer2 = mock(Row.class);
        when(customer1.get("name")).thenReturn("Zack");
        when(customer2.get("name")).thenReturn("Amy");
        mockCustomers.add(customer1);
        mockCustomers.add(customer2);

        when(service.run(any(Select.class))).thenReturn(mockCustomers);
        when(newCustomer.get("name")).thenReturn("Bob");

        // Execute
        handler.beforeCustomerCreate(context);

        // Verify sorting
        verify(context).set(eq("sortedCustomerNames"), argThat(list -> {
            List<String> names = (List<String>) list;
            return names.size() == 3 && 
                   names.get(0).equals("Amy") &&
                   names.get(1).equals("Bob") &&
                   names.get(2).equals("Zack");
        }));
    }

    @Test
    public void testBeforeCustomerCreateWithSingleCustomer() {
        // Setup
        Row customer1 = mock(Row.class);
        when(customer1.get("name")).thenReturn("John");
        mockCustomers.add(customer1);

        when(service.run(any(Select.class))).thenReturn(mockCustomers);
        when(newCustomer.get("name")).thenReturn("Alice");

        // Execute
        handler.beforeCustomerCreate(context);

        // Verify sorting
        verify(context).set(eq("sortedCustomerNames"), argThat(list -> {
            List<String> names = (List<String>) list;
            return names.size() == 2 && 
                   names.get(0).equals("Alice") &&
                   names.get(1).equals("John");
        }));
    }

    @Test
    public void testBeforeCustomerCreateWithDuplicateNames() {
        // Setup
        Row customer1 = mock(Row.class);
        Row customer2 = mock(Row.class);
        when(customer1.get("name")).thenReturn("John");
        when(customer2.get("name")).thenReturn("John");
        mockCustomers.add(customer1);
        mockCustomers.add(customer2);

        when(service.run(any(Select.class))).thenReturn(mockCustomers);
        when(newCustomer.get("name")).thenReturn("John");

        // Execute
        handler.beforeCustomerCreate(context);

        // Verify sorting
        verify(context).set(eq("sortedCustomerNames"), argThat(list -> {
            List<String> names = (List<String>) list;
            return names.size() == 3 && 
                   names.get(0).equals("John") &&
                   names.get(1).equals("John") &&
                   names.get(2).equals("John");
        }));
    }
}