package expert.os.demos.ecommerce.batch;

import expert.os.demos.ecommerce.Customer;
import expert.os.demos.ecommerce.CustomerTier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@DisplayName("Customer tier processor")
class CustomerTierProcessorTest {

    private CustomerTierProcessor processor;

    @BeforeEach
    void setUp() {
        processor = new CustomerTierProcessor(new SegmentationThresholds(List.of(
                threshold("10", CustomerTier.BRONZE),
                threshold("1000", CustomerTier.SILVER),
                threshold("5000", CustomerTier.GOLD),
                threshold("10000", CustomerTier.PLATINUM)
        )));
    }

    @Nested
    @DisplayName("Given a customer already in the calculated tier")
    class MatchingTier {

        @Test
        @DisplayName("When processed, then the customer is filtered")
        void shouldFilterCustomer() {
            Customer customer = customer("7500", CustomerTier.GOLD);

            assertNull(processor.processItem(customer));
        }
    }

    @Nested
    @DisplayName("Given a customer with an outdated tier")
    class OutdatedTier {

        @Test
        @DisplayName("When processed, then an updated customer is returned")
        void shouldReturnCustomerForUpdate() {
            Customer customer = customer("7500", CustomerTier.SILVER);

            Customer updated = processor.processItem(customer);

            assertEquals(CustomerTier.GOLD, updated.getTier());
        }
    }

    private static Customer customer(String totalSpent, CustomerTier tier) {
        return Customer.builder()
                .id("CUST-001")
                .name("Customer")
                .totalSpent(new BigDecimal(totalSpent))
                .tier(tier)
                .build();
    }

    private static SegmentationThreshold threshold(String value, CustomerTier tier) {
        return new SegmentationThreshold(new BigDecimal(value), tier);
    }
}
