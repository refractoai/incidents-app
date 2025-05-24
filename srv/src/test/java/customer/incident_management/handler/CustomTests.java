package customer.refracto.handler;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test class for {@link CustomerServiceHandler}.
 * Validates the bubble sort implementation for customer lists.
 */
class CustomerServiceHandlerTest {

    @InjectMocks
    private CustomerServiceHandler customerServiceHandler;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private Map<String, Object> createCustomer(String name) {
        Map<String, Object> customer = new HashMap<>();
        customer.put("name", name);
        return customer;
    }

    @Test
    @DisplayName("Test sorting empty list")
    void testSortEmptyList() {
        List<Map<String, Object>> customers = new ArrayList<>();
        customerServiceHandler.sortCustomersAfterRead(customers);
        assertTrue(customers.isEmpty());
    }

    @Test
    @DisplayName("Test sorting single customer")
    void testSortSingleCustomer() {
        List<Map<String, Object>> customers = new ArrayList<>();
        customers.add(createCustomer("John"));
        customerServiceHandler.sortCustomersAfterRead(customers);
        assertEquals(1, customers.size());
        assertEquals("John", customers.get(0).get("name"));
    }

    @Test
    @DisplayName("Test sorting multiple customers")
    void testSortMultipleCustomers() {
        List<Map<String, Object>> customers = new ArrayList<>();
        customers.add(createCustomer("Charlie"));
        customers.add(createCustomer("Alice"));
        customers.add(createCustomer("Bob"));
        
        customerServiceHandler.sortCustomersAfterRead(customers);
        
        assertEquals(3, customers.size());
        assertEquals("Alice", customers.get(0).get("name"));
        assertEquals("Bob", customers.get(1).get("name"));
        assertEquals("Charlie", customers.get(2).get("name"));
    }

    @Test
    @DisplayName("Test sorting with null names")
    void testSortWithNullNames() {
        List<Map<String, Object>> customers = new ArrayList<>();
        customers.add(createCustomer(null));
        customers.add(createCustomer("Alice"));
        customers.add(createCustomer(null));
        customers.add(createCustomer("Bob"));
        
        customerServiceHandler.sortCustomersAfterRead(customers);
        
        assertEquals(4, customers.size());
        assertEquals(null, customers.get(0).get("name"));
        assertEquals(null, customers.get(1).get("name"));
        assertEquals("Alice", customers.get(2).get("name"));
        assertEquals("Bob", customers.get(3).get("name"));
    }

    @Test
    @DisplayName("Test sorting already sorted list")
    void testSortAlreadySortedList() {
        List<Map<String, Object>> customers = new ArrayList<>();
        customers.add(createCustomer("Alice"));
        customers.add(createCustomer("Bob"));
        customers.add(createCustomer("Charlie"));
        
        customerServiceHandler.sortCustomersAfterRead(customers);
        
        assertEquals(3, customers.size());
        assertEquals("Alice", customers.get(0).get("name"));
        assertEquals("Bob", customers.get(1).get("name"));
        assertEquals("Charlie", customers.get(2).get("name"));
    }
}