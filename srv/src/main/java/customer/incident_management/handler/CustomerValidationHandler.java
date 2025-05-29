package customer.incident_management.handler;

import cds.gen.processorservice.*;
import com.sap.cds.services.handler.EventHandler;
import com.sap.cds.services.handler.annotations.Before;
import com.sap.cds.services.handler.annotations.ServiceName;
import com.sap.cds.services.persistence.PersistenceService;
import com.sap.cds.services.ErrorStatuses;
import com.sap.cds.services.ServiceException;
import com.sap.cds.services.cds.CqnService;
import org.springframework.stereotype.Component;

@Component
@ServiceName("CustomerService")
public class CustomerValidationHandler implements EventHandler {

    private final PersistenceService db;

    public CustomerValidationHandler(PersistenceService db) {
        this.db = db;
    }

    @Before(event = CqnService.EVENT_CREATE)
    public void validateCreditCardNumber(Customer customer) {
        String creditCardNumber = customer.getCreditCardNumber();
        if (!isValidCreditCardNumber(creditCardNumber)) {
            throw new ServiceException(ErrorStatuses.BAD_REQUEST, 
                "Invalid credit card number format");
        }
    }

    private boolean isValidCreditCardNumber(String cardNumber) {
        if (cardNumber == null || cardNumber.trim().isEmpty()) {
            return false;
        }
        
        // Remove any spaces or hyphens
        cardNumber = cardNumber.replaceAll("[ -]", "");
        
        // Check if the card number contains only digits and has valid length
        if (!cardNumber.matches("\\d{13,19}")) {
            return false;
        }
        
        // Luhn algorithm implementation
        int sum = 0;
        boolean alternate = false;
        
        for (int i = cardNumber.length() - 1; i >= 0; i--) {
            int digit = Character.getNumericValue(cardNumber.charAt(i));
            
            if (alternate) {
                digit *= 2;
                if (digit > 9) {
                    digit -= 9;
                }
            }
            
            sum += digit;
            alternate = !alternate;
        }
        
        return sum % 10 == 0;
    }
}