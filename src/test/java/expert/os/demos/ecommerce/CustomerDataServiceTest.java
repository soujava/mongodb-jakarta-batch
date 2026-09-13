package expert.os.demos.ecommerce;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DisplayName("Customer data service")
class CustomerDataServiceTest {

    @Test
    @DisplayName("Given seed JSON, when customers are loaded, then records are converted to entities")
    void shouldConvertSeedRecordsToCustomers() {
        List<Customer> customers = new CustomerDataService().loadCustomers();

        assertAll(
                () -> assertEquals(100, customers.size()),
                () -> assertNotNull(customers.getFirst().getId()),
                () -> assertNotNull(customers.getFirst().getTier())
        );
    }

    @Test
    @DisplayName("Given an unclassified customer, when statistics are calculated, then it is included without failing")
    void shouldIncludeUnclassifiedCustomerInTotal() {
        List<Customer> customers = List.of(
                customer("CUST-001", CustomerTier.BRONZE),
                customer("CUST-002", null)
        );

        CustomerDataService.CustomerStatistics statistics =
                CustomerDataService.summarize(customers);

        assertAll(
                () -> assertEquals(2, statistics.totalCustomers()),
                () -> assertEquals(1, statistics.tierCounts().get(CustomerTier.BRONZE))
        );
    }

    private static Customer customer(String id, CustomerTier tier) {
        return Customer.builder()
                .id(id)
                .name("Customer")
                .totalSpent(BigDecimal.TEN)
                .tier(tier)
                .build();
    }
}
